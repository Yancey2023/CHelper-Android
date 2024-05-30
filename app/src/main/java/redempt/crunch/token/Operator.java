package redempt.crunch.token;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

/**
 * Represents an Operator which can be used in mathematical expressions
 *
 * @author Redempt
 */
public enum Operator implements Token {

    BOOLEAN_OR("|", 0, (a, b) -> (a == 1 || b == 1) ? 1d : 0d),
    BOOLEAN_OR_ALT("||", 0, (a, b) -> (a == 1 || b == 1) ? 1d : 0d),
    BOOLEAN_AND("&", 0, (a, b) -> (a == 1 && b == 1) ? 1d : 0d),
    BOOLEAN_AND_ALT("&&", 0, (a, b) -> (a == 1 && b == 1) ? 1d : 0d),
    GREATER_THAN(">", 1, (a, b) -> a > b ? 1d : 0d),
    LESS_THAN("<", 1, (a, b) -> a < b ? 1d : 0d),
    EQUAL_TO("=", 1, (a, b) -> a == b ? 1d : 0d),
    EQUAL_TO_ALT("==", 1, (a, b) -> a == b ? 1d : 0d),
    NOT_EQUAL_TO("!=", 1, (a, b) -> a != b ? 1d : 0d),
    GREATER_THAN_OR_EQUAL_TO(">=", 1, (a, b) -> a >= b ? 1d : 0d),
    LESS_THAN_OR_EQUAL_TO("<=", 1, (a, b) -> a <= b ? 1d : 0d),
    BOOLEAN_NOT("!", 9, d -> d == 0 ? 1d : 0d),
    RANDOM_DOUBLE("rand", 6, d -> ThreadLocalRandom.current().nextDouble() * d, false, false),
    ROUND("round", 6, d -> (double) Math.round(d)),
    CEILING("ceil", 6, Math::ceil),
    FLOOR("floor", 6, Math::floor),
    ARC_SINE("asin", 6, Math::asin),
    ARC_COSINE("acos", 6, Math::acos),
    ARC_TANGENT("atan", 6, Math::atan),
    SINE("sin", 6, Math::sin),
    COSINE("cos", 6, Math::cos),
    TANGENT("tan", 6, Math::tan),
    HYPERBOLIC_SINE("sinh", 6, Math::sinh),
    HYPERBOLIC_COSINE("cosh", 6, Math::cosh),
    HYPERBOLIC_TANGENT("tanh", 6, Math::tanh),
    ABSOLUTE_VALUE("abs", 6, Math::abs),
    LOGARITHM("log", 6, Math::log),
    SQUARE_ROOT("sqrt", 6, Math::sqrt),
    CUBE_ROOT("cbrt", 6, Math::cbrt),
    EXPONENT("^", 5, Math::pow),
    MULTIPLY("*", 4, (a, b) -> a * b),
    DIVIDE("/", 4, (a, b) -> a / b),
    MODULUS("%", 4, (a, b) -> a % b),
    ADD("+", 3, Double::sum),
    SUBTRACT("-", 3, (a, b) -> a - b),
    NEGATE("-", 10, d -> -d, true),
    SCIENTIFIC_NOTATION("E", 5, (a, b) -> a * Math.pow(10, b));

    private final String name;
    private final boolean unary;
    private final DoubleBinaryOperator operate;
    private final int priority;
    private final boolean internal;
    private boolean canInline = true;

    Operator(String name, int priority, DoubleBinaryOperator operate) {
        this(name, priority, operate, false);
    }

    Operator(String name, int priority, DoubleBinaryOperator operate, boolean internal) {
        this.name = name;
        this.operate = operate;
        this.unary = false;
        this.priority = priority;
        this.internal = internal;
    }

    Operator(String name, int priority, DoubleUnaryOperator operate) {
        this(name, priority, operate, false);
    }

    Operator(String name, int priority, DoubleUnaryOperator operate, boolean internal) {
        this.name = name;
        this.operate = (a, b) -> operate.applyAsDouble(a);
        this.unary = true;
        this.priority = priority;
        this.internal = internal;
    }

    Operator(String name, int priority, DoubleUnaryOperator operate, boolean internal, boolean canInline) {
        this.name = name;
        this.operate = (a, b) -> operate.applyAsDouble(a);
        this.unary = true;
        this.priority = priority;
        this.internal = internal;
        this.canInline = canInline;
    }

    /**
     * @return The priority of the operation in evaluation - higher priority will be evaluated first
     */
    public int getPriority() {
        return priority;
    }

    /**
     * @return The symbol to represent this Operator in expressions
     */
    public String getSymbol() {
        return name;
    }

    /**
     * @return Whether this Operator operates on only one value
     */
    public boolean isUnary() {
        return unary;
    }

    /**
     * Applies this Operator to the given values
     *
     * @param first  The first value
     * @param second The second value
     * @return The resulting value
     */
    public double operate(double first, double second) {
        return operate.applyAsDouble(first, second);
    }

    /**
     * Applies this Operator to one value
     *
     * @param value The operand value
     * @return The resulting value
     */
    public double operate(double value) {
        return operate.applyAsDouble(value, 0d);
    }

    /**
     * @return Whether this Operator can only be used internally
     * <p>
     * If true, it will not be generated simply by typing its symbol
     */
    public boolean isInternal() {
        return internal;
    }

    @Override
    public TokenType getType() {
        return TokenType.OPERATOR;
    }

    public String toString() {
        return getSymbol();
    }

    public boolean canInline() {
        return canInline;
    }

}
