package yancey.chelper.expression;

import java.util.function.DoubleSupplier;

public class CustomDoubleSupplier implements DoubleSupplier {

    private final double interval;
    private double a;

    public CustomDoubleSupplier(double start, double interval) {
        this.interval = interval;
        this.a = start;
    }

    @Override
    public double getAsDouble() {
        return a;
    }

    public void nextTime() {
        a += interval;
    }
}
