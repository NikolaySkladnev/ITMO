package cardcrypter

import (
	"crypto/aes"
	"crypto/cipher"
	"crypto/rand"
	"errors"
	"runtime"
	"sync"
	"unsafe"
)

const cardLen = 16
const hextable = "0123456789abcdef"

type CardNumber [cardLen]byte

type Card struct {
	ID     string
	Number CardNumber
}

type Crypter interface {
	Encrypt(cards []Card, key []byte) ([]string, error)
}

type crypterImpl struct {
	workers int
}

type CrypterOption func(*crypterImpl)

func New(opts ...CrypterOption) *crypterImpl {
	c := &crypterImpl{
		workers: runtime.GOMAXPROCS(-1),
	}
	for _, opt := range opts {
		opt(c)
	}
	return c
}

func WithWorkers(workers int) CrypterOption {
	return func(s *crypterImpl) {
		s.workers = workers
	}
}

func (c *crypterImpl) Encrypt(cards []Card, key []byte) ([]string, error) {
	n := len(cards)
	if n == 0 {
		return []string{}, nil
	}

	workers, err := c.getWorkers(n)
	if err != nil {
		return nil, err
	}

	block, err := aes.NewCipher(key)
	if err != nil {
		return nil, err
	}
	gcm, err := cipher.NewGCM(block)
	if err != nil {
		return nil, err
	}

	base, rem := n/workers, n%workers

	var wg sync.WaitGroup
	var firstErr error
	out := make([]string, n)
	nonceSize := gcm.NonceSize()

	start := 0
	for w := 0; w < workers; w++ {
		count := base
		if w < rem {
			count++
		}
		from := start
		to := from + count
		start = to

		wg.Go(func() {
			dst := make([]byte, nonceSize, nonceSize+cardLen+gcm.Overhead())

			for i := from; i < to; i++ {
				if _, err := rand.Read(dst[:nonceSize]); err != nil {
					firstErr = err
					return
				}

				out[i] = encodeToStringOneAlloc(gcm.Seal(
					dst[:nonceSize],
					dst[:nonceSize],
					cards[i].Number[:],
					unsafe.Slice(unsafe.StringData(cards[i].ID), len(cards[i].ID))))
			}
		})
	}

	wg.Wait()
	if firstErr != nil {
		return nil, firstErr
	}
	return out, nil
}

func (c *crypterImpl) getWorkers(n int) (int, error) {
	var workers int
	if c.workers <= 0 {
		return 0, errors.New("negative workers")
	}
	workers = c.workers
	if workers > n {
		workers = n
	}
	return workers, nil
}

func encodeToStringOneAlloc(src []byte) string {
	dst := make([]byte, len(src)*2)
	encodeToString(src, dst)
	return unsafe.String(unsafe.SliceData(dst), len(dst))
}

func encodeToString(src []byte, dst []byte) {
	j := 0
	for _, v := range src {
		dst[j] = hextable[v>>4]
		dst[j+1] = hextable[v&0x0f]
		j += 2
	}
}
