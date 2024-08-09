package yancey.chelper.android.enumeration.core;

import java.util.function.DoubleSupplier;

/**
 * 穷举表达式的参数提供器
 */
public class CustomDoubleSupplier implements DoubleSupplier {

    private final double interval;
    private double current;

    public CustomDoubleSupplier(double start, double interval) {
        this.interval = interval;
        this.current = start;
    }

    @Override
    public double getAsDouble() {
        return current;
    }

    public void nextTime() {
        current += interval;
    }
}
