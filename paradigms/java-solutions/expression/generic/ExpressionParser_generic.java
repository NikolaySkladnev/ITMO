package expression.generic;

import expression.exceptions.OverflowException;
import expression.exceptions.ParseException;
import expression.parser.BaseParser;
import expression.parser.StringSource;

import java.util.Objects;

public final class ExpressionParser_generic<T> extends BaseParser {

    Operations<T> operation;

    public ExpressionParser_generic(Operations<T> operation) {
        this.operation = operation;
    }

    public Generic<T> parse(String expression) throws ParseException {
        setSource(new StringSource(expression));
        return parseStart();
    }

    private Generic<T> parseStart() throws ParseException{
        Generic<T> tmp = parseAddSubtract();
        if (!eof()) {
            if (take(')')) throw new ParseException("Expected open bracket on position: " + getPosition());
            throw new ParseException("Unexpected input \"" + getCh() + "\" on position: " + getPosition());
        }
        return tmp;
    }

    private Generic<T> parseAddSubtract() throws ParseException {
        skipWhitespace();
        Generic<T> left = parseMultiDivide();
        skipWhitespace();
        while (true) {
            skipWhitespace();
            if (take('+')) {
                left = new Add_generic<>(left, parseMultiDivide(), operation);
            } else if (take('-')) {
                left = new Subtract_generic<>(left, parseMultiDivide(), operation);
            } else {
                return left;
            }
        }
    }

    private Generic<T> parseMultiDivide() throws ParseException{
        skipWhitespace();
        Generic<T> left = parseUnary();
        skipWhitespace();
        while (true) {
            skipWhitespace();
            if (take('*')) {
                left = new Multiply_generic<>(left, parseUnary(), operation);
            } else if (take('/')) {
                left = new Divide_generic<>(left, parseUnary(), operation);
            } else {
                return left;
            }
        }
    }

    private Generic<T> parseUnary()  throws ParseException{
        skipWhitespace();
        if (take('-')) {
            if (Character.isDigit(getCh())) {
                return parseElements("-");
            } else {
                skipWhitespace();
                return new Negate_generic<>(parseUnary(), operation);
            }
        } else {
            return parseElements("");
        }
    }

    private Generic<T> parseElements(String begin) throws ParseException {
        skipWhitespace();
        if (test('-')) {
            return parseUnary();
        } else if (test('-') || between('0', '9')) {
            return parseInteger(begin);
        } else if (take('(')) {
            Generic<T> left = parseAddSubtract();
            if (!take(')')) throw new ParseException("Expected closing bracket on position: " + getPosition());
            return left;
        } else if (between('x', 'z')) {
            char temp = take();
            return new Variable_generic<>(switch (temp) {
                case 'x' -> "x";
                case 'y' -> "y";
                case 'z' -> "z";
                default -> throw new ParseException("Unexpected value: " + temp);
            });
        } else {
            throw new ParseException("Const, varible or expression expected on position: " + getPosition());
        }
    }

    private String parseVarible() {
        StringBuilder str = new StringBuilder();
        while (Character.isDigit(getCh()) || Objects.equals('$', getCh())) {
            str.append(take());
        }
        return String.valueOf(str);
    }

    private Generic<T> parseInteger(String begin) {
        StringBuilder number = new StringBuilder(begin);
        if (take('0')) {
            return new Const_generic<>(operation.parse(0));
        }
        while (between('0', '9')) {
            number.append(take());
        }
        try {
            return new Const_generic<>(operation.parse(number.toString()));
        } catch (NumberFormatException error) {
            throw new OverflowException("Const: Overflow");
        }
    }

    private void skipWhitespace() {
        while (Character.isWhitespace(getCh())) {
            take();
        }
    }
}