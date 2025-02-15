"use strict"

function OperationPrototype(operation, symbol, ...args) {
    this.args = args
    this.operation = operation;
    this.symbol = symbol;
}

OperationPrototype.prototype.evaluate = function (...values) {
    return this.operation(...this.args.map((element) => element.evaluate(...values)))
}

OperationPrototype.prototype.toString = function () {
    return this.args.map((element) => element.toString()).join(" ") + " " + this.symbol
}

OperationPrototype.prototype.prefix = function () {
    return "(" + this.symbol + " " + this.args.map((element) => element.prefix()).join(" ") + ")"
}

function CreateOperation(operation, symbol) {
    function Operation(...args) {
        OperationPrototype.call(this, operation, symbol, ...args)
    }

    Operation.prototype = Object.create(OperationPrototype.prototype)
    return Operation
}

const sum = (...args) => (args.reduce((a, b) => a + b, 0))
const mathMean = (...args) => (sum(...args.map((element) => (element * (1 / args.length)))))

const Add = CreateOperation((a, b) => a + b, "+")
const Subtract = CreateOperation((a, b) => a - b, "-")
const Divide = CreateOperation((a, b) => a / b, "/")
const Multiply = CreateOperation((a, b) => a * b, "*")
const Sin = CreateOperation(Math.sin, "sin")
const Cos = CreateOperation((c) => Math.cos(c), "cos")
const Negate = CreateOperation((c) => -1 * c, "negate")
const Mean = CreateOperation(mathMean, "mean")
const Var = CreateOperation((...args) => (mathMean(...args.map((element) => Math.pow(element, 2))) - Math.pow(mathMean(...args), 2)), "var")

function Const(number) {
    this.number = number
    this.evaluate = function (...values) {
        return this.number
    }
    this.toString = function () {
        return this.number.toString()
    }
    this.prefix = function () {
        return this.number.toString()
    }
}

const varibles = ["x", "y", "z"]

function Variable(letter) {
    this.letter = letter
    this.evaluate = function (...values) {
        return values[varibles.indexOf(letter)]
    }
    this.toString = function () {
        return this.letter
    }
    this.prefix = function () {
        return this.letter
    }
}
function ParseException(message) {
    this.message = message;
}
ParseException.prototype = Object.create(Error.prototype);
ParseException.prototype.name = "ParseException";
ParseException.prototype.constructor = ParseException;


const opertions = {
    "+": [Add, 1, 2],
    "-": [Subtract, 1, 2],
    "*": [Multiply, 2, 2],
    "/": [Divide, 2, 2],
    "negate": [Negate, 3, 1],
    "sin": [Sin, 3, 1],
    "cos": [Cos, 3, 1],
    "mean": [Mean, 3, -1],
    "var": [Var, 3, -1]
}

let i;
let check;

const parsePrefix = function (expression) {
    i = 0
    check = 0
    expression = expression.split(/(\s+|\(|\))/).filter((element) => element.trim().length > 0)
    const res = parseArg(expression)
    if (i < expression.length) {
        throw new ParseException("Wrong arguments in position: " + i)
    }
    if (check !== 0) {
        throw new ParseException("Wrong brackets order in position: " + i)
    }
    return res
}

const parseArg = function (expression) {
    let element = expression[i++]
    if (element === "(") {
        check++
        element = expression[i++]

        if (!(element in opertions)) {
            throw new ParseException("Unknown operation in position: " + i)
        }

        let operation = opertions[element]
        let countArgs = operation[2]
        let args = []
        while (expression[i] !== ")" && i < expression.length && countArgs !== 0) {
            args.push(parseArg(expression))
            countArgs--
        }
        if ((operation[2] === -1 && args.length === 0) || (operation[2] !== -1 && args.length !== operation[2])) {
            throw new ParseException("Not enough arguments for operation in position: " + i)
        }
        if (expression[i] === ")") {
            check--
            i++
        }
        return (new operation[0](...args))
    } else if (varibles.includes(element)) {
        return (new Variable(element.toString()))
    } else if (!isNaN(element)) {
        return (new Const(parseInt(element)))
    } else if (element === ")"){
        throw new ParseException("Wrong brackets order in position: " + i)
    } else {
        throw new ParseException("Wrong argument in position: " + i)
    }
}
const parse = (expression) => {
    const stack = []
    expression = expression.split(" ")
    for (let i = 0; i < expression.length; i++) {

        if (expression[i] === "") {
            continue
        }

        const element = expression[i]
        if (element in opertions) {
            const operation = opertions[element]
            const args = []
            for (let j = 0; j < operation[2]; j++) {
                args.push(stack.pop())
            }
            stack.push(new operation[0](...args.reverse()))
        } else if (varibles.includes(element)) {
            stack.push(new Variable(element))
        } else {
            stack.push(new Const(parseInt(element)))
        }
    }
    return stack[0];
}
