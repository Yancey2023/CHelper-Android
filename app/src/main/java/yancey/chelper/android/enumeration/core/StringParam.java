package yancey.chelper.android.enumeration.core;

/**
 * 穷举表达式的文本
 */
public class StringParam implements Param {

    private final String param;

    public StringParam(String param) {
        this.param = param;
    }

    @Override
    public String getValue() {
        return param;
    }
}
