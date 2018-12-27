package com.example.a21091.fourthproject;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class FileScanner {

    public String getParentPath(String path) {
        return new File(path).getAbsoluteFile().getParent();
    }

    public boolean isDirectory(String path) {
        return new File(path).isDirectory();
    }

    public List<File> get(String path) {
        List<File> files = new ArrayList<>();

        files.addAll(getFolders(path));
        files.addAll(getFiles(path));
        return files;
    }

    private List<File> getFiles(String path) {

        File file = new File(path);

        List<File> fileList = new ArrayList<>();

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isFile()) {
                    fileList.add(f);
                }
            }
        }
        return fileList;
    }

    private List<File> getFolders(String path) {

        File file = new File(path);
        List<File> fileList = new ArrayList<>();

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    fileList.add(f);
                }
            }
        }
        return fileList;
    }

    public boolean isAvailable(String path) {
        return new File(path).canRead();

    }

    public boolean copyFileOrDirectory(String srcDir, String dstDir) {

        try {
            File src = new File(srcDir);
            File dst = new File(dstDir);

            if (src.isDirectory()) {
                if (!dst.exists()) {
                    boolean mkdirs = dst.mkdirs();
                    if (!mkdirs) {
                        return false;
                    }
                }

                String files[] = src.list();

                for (String file : files) {
                    String src1 = new File(src, file).getPath();
                    String dst1 = new File(dst, file).getPath();
                    copyFileOrDirectory(src1, dst1);
                }

            } else {
                if (!copyFile(src, dst)) {
                    return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public boolean copyFile(File sourceFile, File destFile) throws IOException {

        if (!destFile.exists()) {
            if (!destFile.createNewFile()) {
                return false;
            }
        }

        try (FileChannel source = new FileInputStream(sourceFile).getChannel();

             FileChannel destination = new FileOutputStream(destFile).getChannel()) {
            destination.transferFrom(source, 0, source.size());
        }
        return true;
    }

    public String getNewPathForCopy(String path) {

        File file = new File(path);
        String newNameTemplate = "";
        int n = 1;

        File[] files = file.getParentFile().listFiles();
        String[] split = null;

        if (file.isFile()) {
            split = file.getPath().split("\\.");
            for (int i = 0; i < split.length - 1; i++) {
                newNameTemplate = newNameTemplate.concat(split[i]);
            }
        } else {
            newNameTemplate = file.getPath();
        }

        String newName = newNameTemplate.concat("(" + String.valueOf(n) + ")");

        if (file.isFile()) {
            assert split != null;
            newName = newName.concat("." + split[split.length - 1]);
        }

        for (int j = 0; j < files.length; j++) {

            if (files[j].getPath().equals(newName)) {
                n++;
                newName = newNameTemplate.concat("(" + String.valueOf(n) + ")");

                if (file.isFile()) {
                    newName = newName.concat("." + split[split.length - 1]);
                }
                j = -1;
            }
        }
        return newName;
    }
}