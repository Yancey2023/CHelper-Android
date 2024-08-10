package yancey.chelper.android.common.view;

import android.content.Context;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import yancey.chelper.R;
import yancey.chelper.android.common.util.SelectedString;

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
        if (isGuiLoaded == null || !isGuiLoaded.getAsBoolean() || (history[which] != null && Objects.equals(selectedString, history[which]))) {
            return;
        }
        if (which == history.length - 1) {
            for (int i = 0; i < which; i++) {
                history[i] = history[i + 1];
            }
        } else {
            which++;
            if (which != history.length - 1) {
                for (int i = which + 1; i < history.length; i++) {
                    history[i] = null;
                }
            }
        }
        history[which] = selectedString;
    }

    public SelectedString getSelectedString() {
        Editable editable = getText();
        if (editable == null) {
            return new SelectedString("", 0, 0);
        } else {
            return new SelectedString(editable.toString(), getSelectionStart(), getSelectionEnd());
        }
    }

    public void setSelectedString(SelectedString selectedString) {
        if (selectedString == null) {
            setText("");
            return;
        }
        tryAddHistory(selectedString);
        setText(selectedString.text);
        setSelection(selectedString.selectionStart, selectedString.selectionEnd);
    }


    public void undo() {
        if (which != 0) {
            setSelectedString(history[--which]);
        }
    }

    public void redo() {
        if (which != history.length - 1 && history[which + 1] != null) {
            setSelectedString(history[++which]);
        }
    }

    public void delete() {
        setText("");
    }

    public void setColors(int[] colors) {
        int normalColor = getContext().getColor(R.color.text_main);
        Editable editable = getText();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(editable);
        for (int i = 0; i < colors.length; i++) {
            int color = colors[i];
            if (color == 0) {
                color = normalColor;
            }
            spannableStringBuilder.setSpan(new ForegroundColorSpan(color), i, i + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        setText(spannableStringBuilder);
        setSelection(selectionStart, selectionEnd);
    }

}
