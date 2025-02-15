package expression.parser;

import expression.*;
import expression.exception.*;

public final class ExpressionParser extends BaseParser implements TripleParser {

    public TripleExpression parse(String expression) {
        setSource(new StringSource(expression));
        return parseStart();
    }

    private GeneralInterface parseStart() {
        return parseOR();
    }

    private GeneralInterface parseOR() {
        skipWhitespace();
        GeneralInterface left = parseXOR();
        while (true) {
            skipWhitespace();
            if (take('|')) {
                left = new OR(left, parseXOR());
            } else {
                return left;
            }
        }
    }

    private GeneralInterface parseXOR() {
        skipWhitespace();
        GeneralInterface left = parseAND();
        while (true) {
            skipWhitespace();
            if (take('^')) {
                left = new XOR(left, parseAND());
            } else {
                return left;
            }
        }
    }

    private GeneralInterface parseAND() {
        skipWhitespace();
        GeneralInterface left = parseAddSubtract();
        while (true) {
            skipWhitespace();
            if (take('&')) {
                left = new AND(left, parseAddSubtract());
            } else {
                return left;
            }
        }
    }

    private GeneralInterface parseAddSubtract() {
        skipWhitespace();
        GeneralInterface left = parseMultiDivide();
        while (true) {
            skipWhitespace();
            if (take('+')) {
                left = new CheckedAdd(left, parseMultiDivide());
            } else if (take('-')) {
                left = new CheckedSubtract(left, parseMultiDivide());
            } else {
                return left;
            }
        }
    }

    private GeneralInterface parseMultiDivide() {
        skipWhitespace();
        GeneralInterface left = parseNegate();
        while (true) {
            skipWhitespace();
            if (take('*')) {
                left = new CheckedMultiply(left, parseNegate());
            } else if (take('/')) {
                left = new CheckedDivide(left, parseNegate());
            } else {
                return left;
            }
        }
    }

    private GeneralInterface parseNegate() {
        skipWhitespace();
        if (take('-')) {
            if (Character.isDigit(getCh())) {
                return parseElements("-");
            } else {
                skipWhitespace();
                return new Negate(parseElements(""));
            }
        }
        return parseElements("");
    }

    private GeneralInterface parseElements(String begin) {
        skipWhitespace();
        GeneralInterface left;
        skipWhitespace();
        if (take('x')) {
            return new Variable("x");
        } else if (take('y')) {
            return new Variable("y");
        } else if (take('z')) {
            return new Variable("z");
        } else if (test('-')) {
            return parseNegate();
        } else if (test('-') || between('0', '9')) {
            return parseInteger(begin);
        } else if (take('(')) {
            left = parseStart();
            if (take(')')) {
                throw new BracketsException();
            }
        } else if (take(')')) {
            throw new BracketsException();
        } else {
            throw new ParseException();
        }
        return left;
    }

    private GeneralInterface parseInteger(String begin) {
        StringBuilder number = new StringBuilder(begin);
        if (take('0')) {
            return new Const(0);
        }
        while (between('0', '9')) {
            number.append(take());
        }
        return new Const(Integer.parseInt(number.toString()));
    }

    private void skipWhitespace() {
        while (Character.isWhitespace(getCh())) {
            take();
        }
    }
}