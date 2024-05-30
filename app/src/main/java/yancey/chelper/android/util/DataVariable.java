package yancey.chelper.android.util;

import redempt.crunch.data.Pair;
import yancey.chelper.expression.CustomDoubleSupplier;

public class DataVariable {

    public String name, start, interval;

    public DataVariable() {
        this("","","");
    }

    public DataVariable(String name, String start, String interval) {
        this.name = name;
        this.start = start;
        this.interval = interval;
    }

    public Pair<String, CustomDoubleSupplier> getValue() throws NumberFormatException{
        return new Pair<>(name, new CustomDoubleSupplier(Double.parseDouble(start), Double.parseDouble(interval)));
    }
}
