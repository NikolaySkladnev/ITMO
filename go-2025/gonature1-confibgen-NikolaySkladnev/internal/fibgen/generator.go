package fibonacci

import (
	"errors"
	"sync/atomic"
)

type Generator interface {
	Next() uint64
}

var _ Generator = (*generatorImpl)(nil)

type generatorImpl struct {
	index uint64
}

var ErrFibonacciOverflow = errors.New("fibonacci overflow: uint64 limit reached")

const maxFibonacciIndex = 94

func NewGenerator() *generatorImpl {
	return &generatorImpl{
		index: 0,
	}
}

func (g *generatorImpl) Next() uint64 {
	index := atomic.AddUint64(&g.index, 1) - 1
	if index >= maxFibonacciIndex {
		panic(ErrFibonacciOverflow)
	}
	return fibN(index)
}

func fibN(n uint64) uint64 {
	if n <= 1 {
		return n
	}

	fn, _ := fibPair(n)
	return fn
}

func fibPair(n uint64) (uint64, uint64) {
	if n == 0 {
		return 0, 1
	}

	fHalf, fHalfNext := fibPair(n / 2)

	fDouble := fHalf * (2*fHalfNext - fHalf)
	fDoubleNext := fHalf*fHalf + fHalfNext*fHalfNext

	if n%2 == 1 {
		fDouble, fDoubleNext = fDoubleNext, fDouble+fDoubleNext
	}

	return fDouble, fDoubleNext
}
