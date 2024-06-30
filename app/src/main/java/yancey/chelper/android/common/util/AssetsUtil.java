package yancey.chelper.android.common.util;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AssetsUtil {

    private AssetsUtil() {

    }

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
