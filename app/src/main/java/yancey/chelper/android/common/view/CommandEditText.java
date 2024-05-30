package yancey.chelper.android.common.view;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;

import java.util.Objects;
import androidx.appcompat.widget.AppCompatEditText;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import yancey.chelper.util.SelectedString;

public class CommandEditText extends AppCompatEditText {

    private final SelectedString[] inputs = new SelectedString[50];
    private int which = 0;
    private Consumer<String> onTextChanged;
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
        inputs[0] = new SelectedString("", 0);
    }

    public void setListener(Consumer<String> onTextChanged, Runnable onSelectionChanged, BooleanSupplier isGuiLoaded) {
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
        onTextChanged(getSelectedString());
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (isGuiLoaded != null && isGuiLoaded.getAsBoolean() && onSelectionChanged != null) {
            onSelectionChanged.run();
        }
    }

    public void onTextChanged(SelectedString selectedString) {
        if (isGuiLoaded == null || !isGuiLoaded.getAsBoolean() || (inputs[which] != null && Objects.equals(selectedString.string, inputs[which].string))) {
            return;
        }
        if (which == inputs.length - 1) {
            for (int i = 0; i < which; i++) {
                inputs[i] = inputs[i + 1];
            }
        } else {
            which++;
            if (which != inputs.length - 1) {
                for (int i = which + 1; i < inputs.length; i++) {
                    inputs[i] = null;
                }
            }
        }
        inputs[which] = selectedString;
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
        onTextChanged(selectedString);
        setText(selectedString.string);
        setSelection(selectedString.start, selectedString.end);
    }


    public void undo() {
        if (which != 0) {
            setSelectedString(inputs[--which]);
        }
    }

    public void redo() {
        if (which != inputs.length - 1 && inputs[which + 1] != null) {
            setSelectedString(inputs[++which]);
        }
    }

    public void delete() {
        setText("");
    }
}
