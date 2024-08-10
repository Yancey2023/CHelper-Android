package yancey.chelper.android.common.util;

import org.jetbrains.annotations.NotNull;

/**
 * 用于存储被选择着的文本
 */
public class SelectedString {
    // 文本
    @NotNull
    public final String text;
    // 光标开始位置
    public final int selectionStart;
    // 光标结束位置
    public final int selectionEnd;

    public SelectedString(@NotNull String text, int position) {
        this.text = text;
        this.selectionStart = position;
        this.selectionEnd = position;
    }

    public SelectedString(@NotNull String text, int selectionStart, int selectionEnd) {
        this.text = text;
        this.selectionStart = selectionStart;
        this.selectionEnd = selectionEnd;
    }

}
