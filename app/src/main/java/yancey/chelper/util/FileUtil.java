package yancey.chelper.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.StringJoiner;

public class FileUtil {

    public static String getFilePath(String... strings) {
        StringJoiner sj = new StringJoiner(File.separator);
        for (String string : strings) {
            sj.add(string);
        }
        return sj.toString();
    }

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

    public static String readString(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            return null;
        }
    }
}
