package yancey.chelper.android.rawtext.span;

import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import androidx.annotation.NonNull;

public class TargetSelectorSpan extends ImageSpan {

    /**
     * A constant indicating that the bottom of this span should be aligned
     * with the bottom of the surrounding text, i.e., at the same level as the
     * lowest descender in the text.
     */
    public static final int ALIGN_BOTTOM = 0;

    /**
     * A constant indicating that the bottom of this span should be aligned
     * with the baseline of the surrounding text.
     */
    public static final int ALIGN_BASELINE = 1;

    /**
     * A constant indicating that this span should be vertically centered between
     * the top and the lowest descender.
     */
    public static final int ALIGN_CENTER = 2;

    private String selector;

    public TargetSelectorSpan(@NonNull Drawable drawable, int verticalAlignment) {
        super(drawable, verticalAlignment);
    }
}
