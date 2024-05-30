package yancey.chelper.expression;

import java.util.function.DoubleSupplier;

public class VariableData {

    public String name;
    public double min, max;
    public double interval;

    public VariableData(String name, double min, double max, double interval) {
        this.name = name;
        this.min = min;
        this.max = max;
        this.interval = interval;
    }

    public DoubleSupplier getDoubleSupplier() {
        return new DoubleSupplier() {

            private double min, max;
            private double interval;

            @Override
            public double getAsDouble() {
                return 0;
            }
        };
    }
}
