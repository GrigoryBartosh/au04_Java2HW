package ru.spbau.gbarto;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class FScanner extends RecursiveTask<String> {
    private final MD5File file;

    FScanner(MD5File file) {
        this.file = file;
    }

    @Override
    protected String compute() {
        if (!file.isDirectory()) {
            return file.getMD5();
        }

        File[] files = file.listFiles();

        StringBuilder ans = new StringBuilder(file.getName());
        List<FScanner> tasks = new LinkedList<>();

        for (File f : files) {
            FScanner task = new FScanner(new MD5File(f));
            task.fork();
            tasks.add(task);
        }

        for (FScanner task : tasks) {
            String md5 = task.join();
            ans.append(md5);
        }

        return MD5File.getMD5byString(ans.toString());
    }
}
