// :NOTE: use strict
"use strict"
// :NOTE: use const

// :NOTE: Упростить
// было let add = (f, g) = (x, y, z) => f(x, y, z) + g(x, y, z)
const operation = (f) => (...args) => (...val) => f(...args.map((element) => (element(...val))))
const add = operation((x, y) => x + y)
const subtract = operation((x, y) => x - y)
const divide = operation((x, y) => x / y)
const multiply = operation((x, y) => x * y)
const negate = operation((x) => -1 * x)
const square = operation((x) => x ** 2) // :NOTE: Math.pow
const sqrt = operation(Math.sqrt)

// :NOTE: Упростить
// был switch/case в varible
const allVariable = ["x", "y", "z"]
const variable = (letter) => (...val) => val[allVariable.indexOf(letter)]
const cnst = (c) => () => c
const pi = () => Math.PI
const e = () => Math.E

// :NOTE: where's example?
