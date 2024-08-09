package yancey.chelper.android.enumeration.data;

import redempt.crunch.data.Pair;
import yancey.chelper.android.enumeration.core.CustomDoubleSupplier;

/**
 * 穷举界面列表中每个视图的数据
 */
public class DataVariable {

    public String name, start, interval;

    public DataVariable() {
        this("", "", "");
    }

    public DataVariable(String name, String start, String interval) {
        this.name = name;
        this.start = start;
        this.interval = interval;
    }

    public Pair<String, CustomDoubleSupplier> getValue() throws NumberFormatException {
        return new Pair<>(name, new CustomDoubleSupplier(Double.parseDouble(start), Double.parseDouble(interval)));
    }
}
