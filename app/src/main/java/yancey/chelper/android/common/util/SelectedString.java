package yancey.chelper.android.common.util;

import android.text.Editable;

import androidx.annotation.Nullable;

/**
 * 用于存储被选择着的文本
 */
public class SelectedString {
    // 文本
    @Nullable
    public final Editable editable;
    // 光标开始位置
    public final int selectionStart;
    // 光标结束位置
    public final int selectionEnd;

    public SelectedString(@Nullable Editable editable, int position) {
        this.editable = editable;
        this.selectionStart = position;
        this.selectionEnd = position;
    }

    public SelectedString(@Nullable Editable editable, int selectionStart, int selectionEnd) {
        this.editable = editable;
        this.selectionStart = selectionStart;
        this.selectionEnd = selectionEnd;
    }

}
