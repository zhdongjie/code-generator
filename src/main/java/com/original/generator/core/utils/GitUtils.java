package com.original.generator.core.utils;

import com.original.generator.core.exception.GitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class GitUtils {
    private static final Logger logger = LoggerFactory.getLogger(GitUtils.class);
    private static final int DEFAULT_TIMEOUT_SECONDS = 300; // 5 minutes

    /**
     * 克隆Git仓库
     *
     * @param repoUrl         仓库URL
     * @param destinationPath 目标路径
     * @throws GitException 克隆失败时抛出
     */
    public static void cloneRepository(String repoUrl, String destinationPath) {
        cloneRepository(repoUrl, destinationPath, DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * 克隆Git仓库（带超时设置）
     *
     * @param repoUrl         仓库URL
     * @param destinationPath 目标路径
     * @param timeoutSeconds  超时时间（秒）
     * @throws GitException 克隆失败时抛出
     */
    public static void cloneRepository(String repoUrl, String destinationPath, int timeoutSeconds) {
        if (repoUrl == null || repoUrl.trim().isEmpty()) {
            throw new GitException("Repository URL cannot be null or empty");
        }
        if (destinationPath == null || destinationPath.trim().isEmpty()) {
            throw new GitException("Destination path cannot be null or empty");
        }

        ProcessBuilder processBuilder = new ProcessBuilder("git", "clone", repoUrl, destinationPath);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            boolean completed = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);

            if (!completed) {
                process.destroyForcibly();
                throw new GitException("Git clone operation timed out after " + timeoutSeconds + " seconds");
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                StringBuilder errorOutput = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorOutput.append(line).append("\n");
                    }
                }
                throw new GitException("Git clone failed with exit code " + exitCode + ". Error: " + errorOutput);
            }

            logger.info("Successfully cloned repository from {} to {}", repoUrl, destinationPath);
        } catch (IOException | InterruptedException e) {
            throw new GitException("Failed to clone repository: " + e.getMessage(), e);
        }
    }

    /**
     * 通过下载压缩包的方式下载目标仓库
     * TODO: 实现通过下载压缩包的方式下载目标仓库
     */
    public void downloadRepositoryAsZip(String repoUrl, String destinationPath) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
