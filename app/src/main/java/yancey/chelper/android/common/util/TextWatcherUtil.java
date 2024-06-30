package yancey.chelper.android.common.util;

import android.text.Editable;
import android.text.TextWatcher;

import java.util.function.Consumer;

public class TextWatcherUtil {

    private TextWatcherUtil() {

    }

    public static TextWatcher onTextChanged(Consumer<CharSequence> consumer) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                consumer.accept(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    public static TextWatcher afterTextChanged(Consumer<Editable> consumer) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                consumer.accept(s);
            }
        };
    }
}
