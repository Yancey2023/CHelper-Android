package yancey.chelper.android.common.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.annotation.StringRes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ToastUtil {

    @IntDef({Toast.LENGTH_SHORT, Toast.LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {

    }

    private ToastUtil() {

    }

    public static void show(Context context, @StringRes int text) {
        show(context, context.getString(text), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, String text) {
        show(context, text, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, @StringRes int text, @Duration int duration) {
        show(context, context.getString(text), duration);
    }

    public static void show(Context context, String text, @Duration int duration) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        Toast.makeText(context, text, duration).show();
    }

}