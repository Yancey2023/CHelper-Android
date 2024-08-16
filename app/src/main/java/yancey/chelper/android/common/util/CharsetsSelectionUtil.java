package yancey.chelper.android.common.util;

import androidx.annotation.NonNull;

import java.nio.charset.StandardCharsets;

/**
 * 字符相关的工具类
 */
public class CharsetsSelectionUtil {

    /**
     * 获取utf-8到utf-16的光标位置转换
     *
     * @param text 文本
     * @return utf-8到utf-16的光标位置转换
     */
    @NonNull
    public static int[] utf8ToUtf16(@NonNull String text) {
        int[] indexTransform0 = utf16ToUtf8(text);
        int[] indexTransform = new int[indexTransform0[text.length()]];
        int i = 0;
        for (int j = 0; j <= text.length(); j++) {
            int k = indexTransform0[j];
            while (i <= k) {
                indexTransform[i] = j;
                i++;
            }
        }
        return indexTransform;
    }


    /**
     * 获取utf-16到utf-8的光标位置转换
     *
     * @param text 文本
     * @return utf-16到utf-8的光标位置转换
     */
    @NonNull
    public static int[] utf16ToUtf8(@NonNull String text) {
        int[] indexTransform = new int[text.length()];
        char[] charArray = text.toCharArray();
        int lastByteCount = 0;
        for (int i = 1; i < text.length(); i++) {
            lastByteCount += String.valueOf(charArray[i]).getBytes(StandardCharsets.UTF_8).length;
            indexTransform[i] = lastByteCount;
        }
        return indexTransform;
    }

    /**
     * 获取utf-16到utf-8的光标位置转换
     *
     * @param text      文本
     * @param selection 光标位置
     * @return utf-16到utf-8的光标位置转换
     */
    public static int utf16ToUtf8(@NonNull String text, int selection) {
        String substring;
        if (selection == text.length()) {
            substring = text;
        } else {
            substring = text.substring(0, selection);
        }
        return substring.getBytes(StandardCharsets.UTF_8).length;
    }

}
