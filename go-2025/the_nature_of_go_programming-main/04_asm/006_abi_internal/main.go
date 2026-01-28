package main

var v int

func main() {
	foo()
	bar()
}

// TEXT    main.foo(SB), LEAF|NOFRAME|ABIInternal, $0-0

//go:noinline
func foo() {
	v++
}

//go:noinline
func bar() {
	v--
}
