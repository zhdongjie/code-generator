package com.original.generator.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GitUtils {

    public static void cloneRepository(String repoUrl, String destinationPath) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("git", "clone", repoUrl, destinationPath);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        // 读取 Git 输出
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("Git clone failed with exit code " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Git clone process was interrupted", e);
        }
    }

    // TODO 通过下载压缩包的方式下载目标仓库

}
