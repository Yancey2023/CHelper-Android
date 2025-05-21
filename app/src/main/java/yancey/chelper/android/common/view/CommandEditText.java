/**
 * It is part of CHelper. CHelper is a command helper for Minecraft Bedrock Edition.
 * Copyright (C) 2025  Yancey
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package yancey.chelper.android.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import yancey.chelper.R;
import yancey.chelper.core.SelectedString;
import yancey.chelper.core.ErrorReason;

/**
 * 命令输入框
 */
public class CommandEditText extends AppCompatEditText {

    private final SelectedString[] history = new SelectedString[50];
    private int which = 0;
    @Nullable
    private Consumer<String> onTextChanged;
    @Nullable
    private Runnable onSelectionChanged;
    private BooleanSupplier isGuiLoaded;
    private ErrorReason[] errorReasons;

    public CommandEditText(Context context) {
        super(context);
        init();
    }

    public CommandEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CommandEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        history[0] = new SelectedString("", 0);
    }

    public void setListener(@Nullable Consumer<String> onTextChanged, @Nullable Runnable onSelectionChanged, @NonNull BooleanSupplier isGuiLoaded) {
        this.onTextChanged = onTextChanged;
        this.onSelectionChanged = onSelectionChanged;
        this.isGuiLoaded = isGuiLoaded;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (onTextChanged != null) {
            onTextChanged.accept(text.toString());
        }
        tryAddHistory(getSelectedString());
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (isGuiLoaded != null && isGuiLoaded.getAsBoolean() && onSelectionChanged != null) {
            onSelectionChanged.run();
        }
    }

    public void tryAddHistory(SelectedString selectedString) {
        // 如果界面没有加载出来或着与当前历史记录相同，就不记录
        if (isGuiLoaded == null || !isGuiLoaded.getAsBoolean() || (history[which] != null && Objects.equals(selectedString.text, history[which].text))) {
            return;
        }
        if (which == history.length - 1) {
            // 如果达到数量限制，就舍弃历史最旧的记录
            for (int i = 0; i < which; i++) {
                history[i] = history[i + 1];
            }
        } else {
            // 如果还可以记录，就把后面的记录全部设置为空
            which++;
            if (which != history.length - 1) {
                for (int i = which + 1; i < history.length; i++) {
                    history[i] = null;
                }
            }
        }
        // 记录当前内容
        history[which] = selectedString;
    }

    /**
     * 获取当前选中的内容
     */
    public SelectedString getSelectedString() {
        Editable editable = getText();
        if (editable == null) {
            return new SelectedString("", 0, 0);
        } else {
            return new SelectedString(editable.toString(), getSelectionStart(), getSelectionEnd());
        }
    }

    /**
     * 设置当前选中的内容
     *
     * @param selectedString 被选择着的文本
     */
    public void setSelectedString(SelectedString selectedString) {
        if (selectedString == null) {
            setText("");
            return;
        }
        tryAddHistory(selectedString);
        if (!Objects.equals(selectedString.text, Objects.requireNonNull(getText()).toString())) {
            setText(selectedString.text);
        }
        if (getSelectionStart() != selectedString.selectionStart || getSelectionEnd() != selectedString.selectionEnd) {
            setSelection(selectedString.selectionStart, selectedString.selectionEnd);
        }
    }

    /**
     * 撤回
     */
    public void undo() {
        if (which != 0) {
            setSelectedString(history[--which]);
        }
    }

    /**
     * 恢复
     */
    public void redo() {
        if (which != history.length - 1 && history[which + 1] != null) {
            setSelectedString(history[++which]);
        }
    }

    /**
     * 删除所有内容
     */
    public void delete() {
        setText("");
    }

    /**
     * 设置文本颜色
     *
     * @param colors 每个字符的颜色
     */
    public void setColors(int[] colors) {
        Editable text = this.getText();
        if (text == null) {
            return;
        }
        boolean isSpannableStringBuilder;
        SpannableStringBuilder spannableStringBuilder;
        if (text instanceof SpannableStringBuilder spannableStringBuilder0) {
            isSpannableStringBuilder = true;
            spannableStringBuilder = spannableStringBuilder0;
        } else {
            isSpannableStringBuilder = false;
            spannableStringBuilder = new SpannableStringBuilder(text);
        }
        Arrays.stream(spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), ForegroundColorSpan.class))
                .forEach(spannableStringBuilder::removeSpan);
        int normalColor = getContext().getColor(R.color.text_main);
        for (int i = 0; i < colors.length; i++) {
            int color = colors[i];
            if (color == 0) {
                color = normalColor;
            }
            spannableStringBuilder.setSpan(new ForegroundColorSpan(color), i, i + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
        if (!isSpannableStringBuilder) {
            int selectionStart = getSelectionStart();
            int selectionEnd = getSelectionEnd();
            setText(spannableStringBuilder);
            setSelection(selectionStart, selectionEnd);
        }
    }

    /**
     * 设置错误文本样式
     *
     * @param errorReasons 错误原因
     */
    public void setErrorReasons(ErrorReason[] errorReasons) {
        this.errorReasons = errorReasons;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);

        // 绘制命令错误位置的下划线
        if (errorReasons != null) {
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStrokeWidth(2);

            int offsetY = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP,
                    10,
                    getResources().getDisplayMetrics()
            );

            Layout layout = getLayout();
            int length = Objects.requireNonNull(getText()).length();
            for (ErrorReason errorReason : errorReasons) {
                int start = errorReason.start;
                int end = errorReason.end;
                if (start < 0 || end > length) {
                    continue;
                }
                if (start == end && length != 0) {
                    if (start == length) {
                        start--;
                    } else {
                        end++;
                    }
                }

                int lineStart = layout.getLineForOffset(start);
                int lineEnd = layout.getLineForOffset(end);

                if (lineStart == lineEnd) {
                    // 只有同一行生效
                    int lineBottom = layout.getLineBottom(lineStart);
                    float y = lineBottom + offsetY;
                    float startX = layout.getPrimaryHorizontal(start);
                    float endX = layout.getSecondaryHorizontal(end);
                    canvas.drawLine(startX, y, endX, y, paint);
                } else {
                    // TODO 待实现多行逻辑
                }
            }
        }
    }

}
