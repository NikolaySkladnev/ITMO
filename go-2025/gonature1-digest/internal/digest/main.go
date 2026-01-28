package digest

import (
	"math"
	"math/cmplx"
	"math/rand/v2"
	"strings"
	"unsafe"
)

// GetCharByIndex returns the i-th character from the given string.
func GetCharByIndex(str string, idx int) rune {
	if idx < 0 {
		panic("index out of range")
	}

	i := 0
	for _, v := range str {
		if i == idx {
			return v
		}
		i++
	}

	panic("index out of range")
}

// GetStringBySliceOfIndexes returns a string formed by concatenating specific characters from the input string based
// on the provided indexes.
func GetStringBySliceOfIndexes(str string, indexes []int) string {
	symbols := []rune(str)
	var b strings.Builder
	b.Grow(len(indexes))
	for _, idx := range indexes {
		if idx < 0 || idx >= len(symbols) {
			panic("index out of range")
		}
		b.WriteRune(symbols[idx])
	}
	return b.String()
}

// ShiftPointer shifts the given pointer by the specified number of bytes using unsafe.Add.
func ShiftPointer(pointer **int, shift int) {
	*pointer = (*int)(unsafe.Add(unsafe.Pointer(*pointer), shift))
}

// IsComplexEqual compares two complex numbers and determines if they are equal.
func IsComplexEqual(a, b complex128) bool {
	if cmplx.IsNaN(a) || cmplx.IsNaN(b) {
		return false
	}

	if a == b {
		return true
	}

	return cmplx.Abs(a-b) < 1e-6
}

// GetRootsOfQuadraticEquation returns two roots of a quadratic equation ax^2 + bx + c = 0.
func GetRootsOfQuadraticEquation(a, b, c float64) (complex128, complex128) {
	D := math.Pow(b, 2) - 4*a*c
	var sqrtD complex128
	if D < 0 {
		sqrtD = complex(0, math.Sqrt(math.Abs(D)))
	} else {
		sqrtD = complex(math.Sqrt(D), 0)
	}
	x1 := (-complex(b, 0) + sqrtD) / 2 * complex(a, 0)
	x2 := (-complex(b, 0) - sqrtD) / 2 * complex(a, 0)
	return x1, x2
}

// Sort sorts in-place the given slice of integers in ascending order.
func Sort(source []int) {
	if len(source) < 2 {
		return
	}
	quickSort(source, 0, len(source)-1)
}

func quickSort(source []int, l, h int) []int {
	if l < h {
		var p int
		source, p = partition(source, l, h)
		source = quickSort(source, l, p-1)
		source = quickSort(source, p+1, h)
	}
	return source
}

func partition(source []int, l, h int) ([]int, int) {
	pivotIndex := rand.IntN(h-l+1) + l
	source[pivotIndex], source[h] = source[h], source[pivotIndex]

	pivot := source[h]
	i := l

	for j := l; j < h; j++ {
		if source[j] < pivot {
			source[i], source[j] = source[j], source[i]
			i++
		}
	}

	source[i], source[h] = source[h], source[i]
	return source, i
}

// ReverseSliceOne in-place reverses the order of elements in the given slice.
func ReverseSliceOne(s []int) {
	for i, j := 0, len(s)-1; i < j; i, j = i+1, j-1 {
		s[i], s[j] = s[j], s[i]
	}
}

// ReverseSliceTwo returns a new slice of integers with elements in reverse order compared to the input slice.
// The original slice remains unmodified.
func ReverseSliceTwo(s []int) []int {
	answer := make([]int, len(s))
	copy(answer, s)
	ReverseSliceOne(answer)
	return answer
}

// SwapPointers swaps the values of two pointers.
func SwapPointers(a, b *int) {
	if a == nil || b == nil {
		panic("pointer is nil")
	}

	*a, *b = *b, *a
}

// IsSliceEqual compares two slices of integers and returns true if they contain the same elements in the same order.
func IsSliceEqual(a, b []int) bool {
	if len(a) != len(b) {
		return false
	}

	for i, v := range a {
		if b[i] != v {
			return false
		}
	}

	return true
}

// DeleteByIndex deletes the element at the specified index from the slice and returns a new slice.
// The original slice remains unmodified.
func DeleteByIndex(s []int, idx int) []int {
	res := make([]int, 0, len(s))
	res = append(res, s[:idx]...)
	res = append(res, s[idx+1:]...)
	return res
}
