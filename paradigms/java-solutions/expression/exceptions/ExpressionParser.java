package expression.exceptions;

import expression.*;
import expression.parser.BaseParser;
import expression.parser.StringSource;

import java.util.List;
import java.util.Objects;

public final class ExpressionParser extends BaseParser implements TripleParser, ListParser {

    private List<String> variables;
    private boolean check;

    public TripleExpression parse(String expression) throws ParseException {
        this.check = false;
        setSource(new StringSource(expression));
        return parseStart();
    }

    @Override
    public ListExpression parse(String expression, List<String> variables) throws ParseException {
        this.check = true;
        this.variables = variables;
        setSource(new StringSource(expression));
        return parseStart();
    }

    private GeneralInterface parseStart() throws ParseException{
        GeneralInterface tmp = parseOR();
        if (!eof()) {
            if (take(')')) throw new ParseException("Expected open bracket on position: " + getPosition());
            throw new ParseException("Unexpected input \"" + getCh() + "\" on position: " + getPosition());
        }
        return tmp;
    }

    private GeneralInterface parseOR() throws ParseException{
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

    private GeneralInterface parseXOR() throws ParseException {
        skipWhitespace();
        GeneralInterface left = parseAND();
        skipWhitespace();
        while (true) {
            skipWhitespace();
            if (take('^')) {
                left = new XOR(left, parseAND());
            } else {
                return left;
            }
        }
    }

    private GeneralInterface parseAND() throws ParseException {
        skipWhitespace();
        GeneralInterface left = parseAddSubtract();
        skipWhitespace();
        while (true) {
            skipWhitespace();
            if (take('&')) {
                left = new AND(left, parseAddSubtract());
            } else {
                return left;
            }
        }
    }

    private GeneralInterface parseAddSubtract() throws ParseException {
        skipWhitespace();
        GeneralInterface left = parseMultiDivide();
        skipWhitespace();
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

    private GeneralInterface parseMultiDivide() throws ParseException{
        skipWhitespace();
        GeneralInterface left = parseUnary();
        skipWhitespace();
        while (true) {
            skipWhitespace();
            if (take('*')) {
                left = new CheckedMultiply(left, parseUnary());
            } else if (take('/')) {
                left = new CheckedDivide(left, parseUnary());
            } else {
                return left;
            }
        }
    }

    private GeneralInterface parseUnary()  throws ParseException{
        skipWhitespace();
        if (take('-')) {
            if (Character.isDigit(getCh())) {
                return parseElements("-");
            } else {
                skipWhitespace();
                return new CheckedNegate(parseUnary());
            }
        } else if (!(test('l') || test('t'))) {
            return parseElements("");
        } else {
            char zeroes = take();
            if (!take('0')) {
                throw new ParseException("Const, varible or expression expected on position: " + getPosition());
            }
            if (Character.isWhitespace(getCh()) || Objects.equals('(', getCh())) {
                if (zeroes == 'l') {
                    return new L_Zeroes(parseUnary());
                } else {
                    return new R_Zeroes(parseUnary());
                }
            } else {
                throw new ParseException("Unexpected input \"" + getCh() + "\" on position: " + getPosition());
            }
        }
    }

    private GeneralInterface parseElements(String begin) throws ParseException {
        skipWhitespace();
        if (test('-')) {
            return parseUnary();
        } else if (test('-') || between('0', '9')) {
            return parseInteger(begin);
        } else if (take('(')) {
            GeneralInterface left = parseOR();
            if (!take(')')) throw new ParseException("Expected closing bracket on position: " + getPosition());
            return left;
        } else if (check) {
            int index = variables.indexOf(parseVarible());
            if (index == -1) throw new ParseException("No such variable");
            return new Variable(index);
        } else if (between('x', 'z')) {
            char temp = take();
            return new Variable(switch (temp) {
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

    private GeneralInterface parseInteger(String begin) {
        StringBuilder number = new StringBuilder(begin);
        if (take('0')) {
            return new Const(0);
        }
        while (between('0', '9')) {
            number.append(take());
        }
        try {
            return new Const(Integer.parseInt(number.toString()));
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