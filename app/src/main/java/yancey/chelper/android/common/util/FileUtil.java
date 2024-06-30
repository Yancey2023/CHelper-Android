package yancey.chelper.android.common.util;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringJoiner;

public class FileUtil {

    private static final int BUFFER_SIZE = 1024;

    public static String getFilePath(String... strings) {
        StringJoiner sj = new StringJoiner(File.separator);
        for (String string : strings) {
            sj.add(string);
        }
        return sj.toString();
    }

    @SuppressWarnings("unused")
    public static File getFile(String... strings) {
        return new File(getFilePath(strings));
    }

    public static boolean createParentFile(File file) {
        if (file.exists()) {
            return true;
        }
        File parent = file.getParentFile();
        return parent == null || parent.exists() || parent.mkdirs();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static boolean writeString(File file, String str) {
        if (!createParentFile(file)) {
            return false;
        }
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(str.getBytes());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Nullable
    public static String readString(File file) {
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
            return new String(readAllByte(inputStream));
        } catch (IOException e) {
            return null;
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public static int copyAllBytes(InputStream inputStream, OutputStream outputStream) throws IOException {
        int byteCount = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        while (true) {
            int read = inputStream.read(buffer);
            if (read == -1) {
                break;
            }
            outputStream.write(buffer, 0, read);
            byteCount += read;
        }
        return byteCount;
    }

    public static byte[] readAllByte(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            copyAllBytes(inputStream, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }
}
