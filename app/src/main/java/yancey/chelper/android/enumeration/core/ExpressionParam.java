package yancey.chelper.android.enumeration.core;

import redempt.crunch.CompiledExpression;

/**
 * 穷举表达式的变量
 */
public class ExpressionParam implements Param {

    private final CompiledExpression expression;
    /**
     * 是否是小数
     */
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
