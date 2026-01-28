package main

import (
	"fmt"
	_ "unsafe"
)

//go:linkname nameSym main.name
var nameSym [16]byte

func main() {
	first := string(nameSym[:5])    // "hello"
	second := string(nameSym[5:16]) // "@igoroutine"
	fmt.Printf("%q + %q\n", first, second)

	//another.PrintName()
	//nameSym[9] = 5
}
