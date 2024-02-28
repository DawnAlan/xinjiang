package dataExtraction.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Description:</p>
 *
 * @author chenzhitao
 * @date 2022年12月09日
 * 文件工具类
 */
public class FileUtil {
    public static String FILE_NAME_PREFIX = "fileNamePrefix";
    public static String FILE_NAME_SUFFIX = "fileNameSuffix";
    public static String FILE_NAME = "fileName";

    /**
     * 根据文件路径，获取相同文件前缀的文件集合
     *
     * @param sourcePath
     * @return
     */
    public static Map<String, Map<Integer, File>> searchSourceFiles(String sourcePath) {
        File sourceFolder = new File(sourcePath);
        if (!sourceFolder.exists()) {
            return new HashMap<>();
        }
        //string是文件前缀<文件前缀，<文件后缀，文件>>
        HashMap<String, Map<Integer, File>> fileHashMap = new HashMap<>();
        File[] files = sourceFolder.listFiles();
        for (File file : files) {
            Map<String, String> fileNameMap = getFileName(file);
            String fileNamePrefix = fileNameMap.get(FILE_NAME_PREFIX);
            String fileNameSuffixStr = fileNameMap.get(FILE_NAME_SUFFIX);
            Integer fileNameSuffix = Integer.valueOf(fileNameSuffixStr);
            if (fileHashMap.get(fileNamePrefix) == null) {
                fileHashMap.put(fileNamePrefix, new HashMap<Integer, File>());
            }
            Map<Integer, File> fileMap = fileHashMap.get(fileNamePrefix);
            fileMap.put(fileNameSuffix, file);
        }
        return fileHashMap;
    }

    /**
     * 获取文件名、前缀、后缀
     *
     * @param file
     * @return
     */
    public static Map<String, String> getFileName(File file) {
        String name = file.getName();
        String[] strings = name.split("\\.");
        HashMap<String, String> map = new HashMap<>();
        map.put(FILE_NAME_PREFIX, strings[0]);
        map.put(FILE_NAME_SUFFIX, strings[1]);
        map.put(FILE_NAME, name);
        return map;
    }


    /**
     * 将文件转存到指定目录下
     */
    public static void moveFileTo(File file, String bakPath, String filePrefix) throws IOException {
        //目的目录路径
        File endDirection = new File(bakPath + "\\" + filePrefix);
        //#如果目的目录路径不存在，则进行创建
        if (!endDirection.exists()) {
            endDirection.mkdirs();
        }
        String fileName = file.getName();
        //目的文件路径=目的目录路径+源文件名称
        File endFile = new File(endDirection + "\\" + fileName);
        //如果该文件已存在，则先删除之前文件，再备份
        if (endFile.exists()) {
            endFile.delete();
        }
        Files.move(file.toPath(), endFile.toPath());
    }

    /**
     * 查询指定文件夹下的文件
     *
     * @param batPath
     * @return
     */
    public static Map<String, File> searchSBatFiles(String batPath) {
        File file = new File(batPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        HashMap<String, File> batFileMap = new HashMap<>();
        File[] files = file.listFiles();
        for (File subFile : files) {
            batFileMap.put(subFile.getName(), subFile);
        }
        return batFileMap;
    }

    /**
     * 删除指定文件夹及其文件夹下的目录
     *
     * @param endDirection
     * @return
     */
    public static Boolean deleteFileByPrefix(File endDirection) {
        if (!endDirection.exists()) {
            return true;
        }
        File[] files = endDirection.listFiles();
        //将file子目录及子文件放进文件数组
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFileByPrefix(file);
                }
                if (file.isFile()) {
                    file.delete();
                }
            }
        }
        return endDirection.delete();
    }

    /**
     * 删除指定目录下的，相同文件前缀的文件
     *
     * @param path
     * @param prefix
     * @return
     */
    public static boolean deleteFilesForPathByPrefix(String path, String prefix) {
        boolean success = true;
        try (DirectoryStream newDirectoryStream = Files.newDirectoryStream(Paths.get(path), prefix + "*")) {
            for (Object newDirectoryStreamItem : newDirectoryStream) {
                Files.delete((Path) newDirectoryStreamItem);
            }
        } catch (final Exception e) {
            success = false;
            e.printStackTrace();
        }
        return success;
    }

    /**
     * 写入到指定文件
     *
     * @param fileName
     * @param content
     */
    public static void writeToFile(String fileName, String content) {
        try (FileOutputStream outputStream = new FileOutputStream(fileName);) {
            outputStream.write(content.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除文件
     */
    public static void deleteFile(File file) {
        file.deleteOnExit();
    }
}
