package yancey.chelper.android.common.util;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 软件内置资源管理相关的工具类
 */
public class AssetsUtil {

    /**
     * 不允许创建实例
     */
    private AssetsUtil() {

    }

    /**
     * 读取内置资源中的文本
     *
     * @param context 上下文
     * @param path    资源路径
     * @return 读取的文本
     * @throws IOException IO错误
     */
    public static String readStringFromAssets(Context context, String path) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(context.getAssets().open(path))) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int count; (count = bis.read(buffer)) != -1; ) {
                os.write(buffer, 0, count);
            }
            return os.toString();
        }
    }

}
