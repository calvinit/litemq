package io.github.litemq.common.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 文件操作工具类
 *
 * @author calvinit
 * @since 0.0.1
 */
public class FileUtils {

    private FileUtils() {}

    public static boolean checkExists(String path) {
        return Files.exists(Paths.get(path));
    }

    public static void createDirs(String dir) {
        try {
            Path dirPath = Paths.get(dir);
            if (Files.notExists(dirPath)) {
                Files.createDirectories(dirPath);
            }
        } catch (IOException ignore) {}
    }

    public static void createFile(String path) {
        try {
            Path filePath = Paths.get(path);
            Path parent = filePath.getParent();
            if (parent != null && Files.notExists(parent)) {
                Files.createDirectories(parent);
            }
            Files.createFile(filePath);
        } catch (IOException ignore) {}
    }

    public static void deleteFile(String path) {
        try {
            Files.deleteIfExists(Paths.get(path));
        } catch (IOException ignore) {}
    }

    public static boolean writeFile(String path, Iterable<String> lines) {
        try {
            // 如果文件不存在，则创建，打开文件并将原有所有内容（如果有）删除，然后写入新内容
            Files.write(Paths.get(path), lines, StandardCharsets.UTF_8);
        } catch (IOException ignore) {
            return false;
        }
        return true;
    }

    public static boolean appendFile(String path, Iterable<String> lines) {
        try {
            // 如果文件不存在，则创建，打开文件并将内容追加写入到原内容（如果有）后面
            Files.write(Paths.get(path), lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignore) {
            return false;
        }
        return true;
    }

    public static String readFile(String path) {
        String content = null;
        try {
            content = Files.readString(Paths.get(path), StandardCharsets.UTF_8);
        } catch (IOException ignore) {}
        return content;
    }
}
