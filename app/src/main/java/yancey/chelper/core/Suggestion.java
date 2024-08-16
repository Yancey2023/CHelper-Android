package yancey.chelper.core;

import java.lang.annotation.Native;

/**
 * 与c++交互时获取的单个补全提示
 */
public class Suggestion {
    @Native
    public String name;
    @Native
    public String description;
}
