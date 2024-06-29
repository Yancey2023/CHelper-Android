package yancey.chelper.expression;

import redempt.crunch.CompiledExpression;

public class ExpressionParam implements Param {

    private final CompiledExpression expression;
    private final boolean isFloat;

    public ExpressionParam(CompiledExpression expression, boolean isFloat) {
        this.expression = expression;
        this.isFloat = isFloat;
    }

    @Override
    public String getValue() {
        double a = expression.evaluate();
        return isFloat ? Float.toString((float) a) : Integer.toString((int) a);
    }
}
