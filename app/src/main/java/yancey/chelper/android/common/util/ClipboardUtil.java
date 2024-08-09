package yancey.chelper.android.common.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * 剪切板相关的工具类
 */
public class ClipboardUtil {

    private static final String TAG = "ClipboardUtil";

    /**
     * 不允许创建实例
     */
    private ClipboardUtil() {

    }

    /**
     * 写入剪切板
     *
     * @param context      上下文
     * @param charSequence 文本
     * @return 是否成功
     */
    public static boolean setText(Context context, CharSequence charSequence) {
        try {
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboardManager == null) {
                return false;
            }
            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, charSequence));
            return true;
        } catch (Exception e) {
            Log.e(TAG, "fail to set text in clipboard service", e);
            return false;
        }
    }

    /**
     * 读取剪切板
     *
     * @param context 上下文
     * @return 读取的文本
     */
    public static @Nullable CharSequence getText(Context context) {
        try {
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboardManager == null || !clipboardManager.hasPrimaryClip()) {
                return null;
            }
            ClipData clip = clipboardManager.getPrimaryClip();
            if (clip == null || clip.getItemCount() == 0) {
                return null;
            }
            return clip.getItemAt(0).coerceToText(context);
        } catch (Exception e) {
            Log.e(TAG, "fail to get text in clipboard service", e);
            return null;
        }
    }

}
