package main

import (
	"fmt"
	"github.com/central-university-dev/2025-spring-go-course-lesson1/fizzbuzz"
)

func main() {
	for i := 0; i <= 100; i++ {
		fmt.Println(fizzbuzz.FizzBuzz(i))
	}
}
