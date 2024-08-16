package yancey.chelper.core;

import java.lang.annotation.Native;

/**
 * 与c++交互时使用补全提示返回的结果
 */
public class ClickSuggestionResult {
    /**
     * 文本
     */
    @Native
    public String text;
    /**
     * 光标位置
     */
    @Native
    public int selection;
}
