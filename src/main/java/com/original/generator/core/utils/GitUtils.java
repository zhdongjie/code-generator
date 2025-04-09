package com.original.generator.core.utils;

import com.original.generator.core.exception.GitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * Git工具类
 * 负责Git仓库的操作，包括克隆和下载
 * <p>
 * 主要功能：
 * 1. 克隆Git仓库
 * 2. 支持超时设置
 * 3. 错误处理和日志记录
 *
 * @author 代码生成器团队
 * @version 1.0
 */
@Component
public class GitUtils {
    /**
     * 日志记录器
     * 用于记录Git操作的日志
     */
    private static final Logger logger = LoggerFactory.getLogger(GitUtils.class);

    /**
     * 默认超时时间（秒）
     * 克隆操作的默认超时时间为5分钟
     */
    private static final int DEFAULT_TIMEOUT_SECONDS = 300; // 5 minutes

    /**
     * 克隆Git仓库
     * 使用默认超时时间克隆指定的Git仓库
     * <p>
     * 步骤：
     * 1. 调用带超时参数的克隆方法
     * 2. 使用默认超时时间
     *
     * @param repoUrl         仓库URL
     * @param destinationPath 目标路径
     * @throws GitException 如果克隆操作失败
     */
    public static void cloneRepository(String repoUrl, String destinationPath) {
        cloneRepository(repoUrl, destinationPath, DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * 克隆Git仓库（带超时设置）
     * 克隆指定的Git仓库，支持自定义超时时间
     * <p>
     * 步骤：
     * 1. 验证参数有效性
     * 2. 创建Git克隆进程
     * 3. 等待进程完成或超时
     * 4. 检查退出码
     * 5. 处理错误输出
     *
     * @param repoUrl         仓库URL
     * @param destinationPath 目标路径
     * @param timeoutSeconds  超时时间（秒）
     * @throws GitException 如果克隆操作失败或超时
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
     * 将Git仓库下载为ZIP压缩包
     * <p>
     * 注意：此方法尚未实现
     *
     * @param repoUrl         仓库URL
     * @param destinationPath 目标路径
     * @throws UnsupportedOperationException 因为方法尚未实现
     */
    public void downloadRepositoryAsZip(String repoUrl, String destinationPath) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
