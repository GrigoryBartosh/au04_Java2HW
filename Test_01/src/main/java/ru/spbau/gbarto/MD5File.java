package ru.spbau.gbarto;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;

class MD5File {
    private String path;
    private File file;

    MD5File(String path) {
        this.path = path;
        this.file = new File(path);
    }

    MD5File(File file) {
        this.file = file;
        this.path = file.getAbsolutePath();
    }

    boolean isDirectory() {
        return file.isDirectory();
    }

    File[] listFiles() {
        File[] files = file.listFiles();
        Arrays.sort(files, (Comparator) (f1, f2) -> ((File) f1).getName().compareTo(((File) f2).getName()));
        return files;
    }

    String getName() {
        return file.getName();
    }

    static String getMD5byString(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest);
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    private String getMD5byContent() {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(Files.readAllBytes(Paths.get(path)));
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest);
        } catch (NoSuchAlgorithmException | IOException e) {
            return "";
        }
    }

    String getMD5() {
        if (!isDirectory()) {
            return getMD5byContent();
        }

        StringBuilder ans = new StringBuilder(getName());

        for (File f : listFiles()) {
            MD5File md5f = new MD5File(f);
            ans.append(md5f.getMD5());
        }

        return getMD5byString(ans.toString());
    }
}
