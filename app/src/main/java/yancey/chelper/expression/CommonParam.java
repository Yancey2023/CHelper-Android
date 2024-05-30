package yancey.chelper.expression;

public class CommonParam implements Param {

    private final String param;

    public CommonParam(String param) {
        this.param = param;
    }

    @Override
    public String getValue() {
        return param;
    }
}
