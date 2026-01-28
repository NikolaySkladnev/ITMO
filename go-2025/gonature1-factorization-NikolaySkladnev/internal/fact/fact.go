package fact

import (
	"context"
	"errors"
	"io"
	"math"
	"runtime"
	"strconv"
	"strings"
	"sync"
)

const lineSeparator = "\n"

var (
	ErrFactorizationCancelled = errors.New("cancelled")
	ErrWriterInteraction      = errors.New("writer interaction")
)

type Factorizer interface {
	Factorize(ctx context.Context, numbers []int, writer io.Writer) error
}

type factorizerImpl struct {
	factWorkers  int
	writeWorkers int
}

func New(opts ...FactorizeOption) (*factorizerImpl, error) {
	f := &factorizerImpl{
		factWorkers:  runtime.GOMAXPROCS(-1),
		writeWorkers: runtime.GOMAXPROCS(-1),
	}
	for _, opt := range opts {
		opt(f)
	}

	if f.factWorkers <= 0 {
		return nil, errors.New("number of factorization workers should be greater than 0: " + strconv.Itoa(f.factWorkers))
	}
	if f.writeWorkers <= 0 {
		return nil, errors.New("number of write workers should be greater than 0: " + strconv.Itoa(f.writeWorkers))
	}

	return f, nil
}

type FactorizeOption func(*factorizerImpl)

func WithFactorizationWorkers(workers int) FactorizeOption {
	return func(f *factorizerImpl) {
		f.factWorkers = workers
	}
}

func WithWriteWorkers(workers int) FactorizeOption {
	return func(f *factorizerImpl) {
		f.writeWorkers = workers
	}
}

func (f *factorizerImpl) Factorize(ctx context.Context, numbers []int, writer io.Writer) error {
	if len(numbers) == 0 {
		return nil
	}

	if ctx == nil {
		ctx = context.Background()
	}

	if ctx.Err() != nil {
		return getCtxErr(ctx)
	}

	parentCtx := ctx
	ctx, cancel := context.WithCancel(parentCtx)
	defer cancel()

	jobs := make(chan int)
	results := make(chan string)

	var factWG sync.WaitGroup
	f.bootFactWorkers(ctx, &factWG, jobs, results)

	var (
		writeErr error
		once     sync.Once
		writeWG  sync.WaitGroup
	)
	f.bootWriteWorkers(ctx, &writeWG, results, writer, &once, &writeErr, cancel)

	produce(ctx, numbers, jobs)

	close(jobs)
	factWG.Wait()
	close(results)
	writeWG.Wait()

	if writeErr != nil {
		return writeErr
	}
	if parentCtx.Err() != nil {
		return getCtxErr(parentCtx)
	}
	if ctx.Err() != nil {
		return ErrFactorizationCancelled
	}
	return nil
}

func (f *factorizerImpl) bootFactWorkers(ctx context.Context, factWG *sync.WaitGroup, jobs <-chan int, results chan string) {
	for i := 0; i < f.factWorkers; i++ {
		factWG.Go(func() {
			for {
				select {
				case <-ctx.Done():
					return
				case n, ok := <-jobs:
					if !ok {
						return
					}
					line := buildResponse(n, factorizeNum(n))
					select {
					case <-ctx.Done():
						return
					case results <- line:
					}
				}
			}
		})
	}
}

func (f *factorizerImpl) bootWriteWorkers(ctx context.Context, writeWG *sync.WaitGroup, results <-chan string, writer io.Writer, once *sync.Once, writeErr *error, cancel context.CancelFunc) {
	for i := 0; i < f.writeWorkers; i++ {
		writeWG.Go(func() {
			for {
				select {
				case <-ctx.Done():
					return
				case s, ok := <-results:
					if !ok {
						return
					}
					if _, err := writer.Write([]byte(s)); err != nil {
						once.Do(func() {
							*writeErr = errors.Join(ErrWriterInteraction, err)
							cancel()
						})
						return
					}
				}
			}
		})
	}
}

func produce(ctx context.Context, numbers []int, jobs chan<- int) {
	for _, n := range numbers {
		select {
		case <-ctx.Done():
			return
		case jobs <- n:
		}
	}
}

func getCtxErr(ctx context.Context) error {
	cause := context.Cause(ctx)
	if errors.Is(cause, context.Canceled) || errors.Is(cause, context.DeadlineExceeded) {
		return ErrFactorizationCancelled
	}
	return cause
}

func factorizeNum(n int) []int {
	if n <= 1 && n >= -1 {
		return []int{n}
	}

	factors := make([]int, 0)

	u := uint64(n)
	if n < 0 {
		factors = append(factors, -1)
		u = 0 - u
	}

	uSqrt := uint64(math.Sqrt(float64(u)))
	for i := uint64(2); i <= uSqrt; i++ {
		for u%i == 0 {
			factors = append(factors, int(i))
			u /= i
		}
	}

	if u != 1 {
		factors = append(factors, int(u))
	}

	return factors
}

func buildResponse(n int, factors []int) string {
	Response := new(strings.Builder)

	Response.WriteString(strconv.Itoa(n) + " = ")

	factorStrings := make([]string, len(factors))
	for i, factor := range factors {
		factorStrings[i] = strconv.Itoa(factor)
	}

	Response.WriteString(strings.Join(factorStrings, " * ") + lineSeparator)

	return Response.String()
}
