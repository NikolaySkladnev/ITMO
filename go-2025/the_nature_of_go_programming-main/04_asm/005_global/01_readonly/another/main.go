package another

import (
	"fmt"
	_ "unsafe"
)

//go:linkname nameSym main.name
var nameSym [16]byte

func PrintName() {
	first := string(nameSym[:5])    // "hello"
	second := string(nameSym[5:16]) // "@igoroutine"
	fmt.Printf("%q + %q\n", first, second)
}
