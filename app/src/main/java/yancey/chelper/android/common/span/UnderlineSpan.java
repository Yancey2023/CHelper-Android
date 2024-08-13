package yancey.chelper.android.common.span;

import android.os.Build;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

public class UnderlineSpan extends CharacterStyle implements UpdateAppearance {

    private final int underlineColor;

    public UnderlineSpan(@ColorInt int underlineColor) {
        this.underlineColor = underlineColor;
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ds.underlineColor = underlineColor;
        }
        ds.setUnderlineText(true);
    }

    @NonNull
    @Override
    public String toString() {
        return "UnderlineSpan{}";
    }
}
