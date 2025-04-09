package com.original.generator.core.utils;

import java.util.regex.Pattern;

/**
 * 字符串转换工具类
 * 提供各种命名规范之间的转换功能，包括驼峰命名、下划线命名和短横线命名
 * <p>
 * 主要功能：
 * 1. 驼峰命名与下划线命名的相互转换
 * 2. 驼峰命名与短横线命名的相互转换
 * 3. 支持大小写转换
 * 4. 输入验证和异常处理
 *
 * @author 代码生成器团队
 * @version 1.0
 */
public class StringConverterUtil {

    /**
     * 有效名称的正则表达式模式
     * 只允许字母、数字和下划线
     */
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

    /**
     * 大写字母的正则表达式模式
     * 用于识别驼峰命名中的单词边界
     */
    private static final Pattern UPPER_CASE_PATTERN = Pattern.compile("(?=[A-Z])");

    /**
     * 下划线的正则表达式模式
     * 用于分割下划线命名的单词
     */
    private static final Pattern UNDERSCORE_PATTERN = Pattern.compile("_");

    /**
     * 将下划线命名转换为驼峰命名
     * 支持首字母大小写控制
     * <p>
     * 步骤：
     * 1. 验证输入字符串
     * 2. 分割下划线命名的单词
     * 3. 转换每个单词的首字母
     * 4. 拼接结果
     *
     * @param underscore           下划线命名的字符串
     * @param lowerCaseFirstLetter 是否将首字母转换为小写
     * @return 驼峰命名的字符串
     * @throws IllegalArgumentException 如果输入字符串包含非法字符
     */
    private static String underscoreToCamelCase(String underscore, boolean lowerCaseFirstLetter) {
        validateInput(underscore);

        String[] parts = UNDERSCORE_PATTERN.split(underscore);
        if (parts.length == 0) {
            return underscore;
        }

        StringBuilder camelCase = new StringBuilder(underscore.length());
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (!part.isEmpty()) {
                if (i == 0 && lowerCaseFirstLetter) {
                    camelCase.append(Character.toLowerCase(part.charAt(0)));
                } else {
                    camelCase.append(Character.toUpperCase(part.charAt(0)));
                }
                if (part.length() > 1) {
                    camelCase.append(part.substring(1).toLowerCase());
                }
            }
        }
        return camelCase.toString();
    }

    /**
     * 将下划线命名转换为大驼峰命名（帕斯卡命名）
     * 所有单词首字母大写
     *
     * @param underscore 下划线命名的字符串
     * @return 大驼峰命名的字符串
     * @throws IllegalArgumentException 如果输入字符串包含非法字符
     */
    public static String underscoreToPascalCase(String underscore) {
        return underscoreToCamelCase(underscore, false);
    }

    /**
     * 将下划线命名转换为小驼峰命名
     * 第一个单词首字母小写，其余单词首字母大写
     *
     * @param underscore 下划线命名的字符串
     * @return 小驼峰命名的字符串
     * @throws IllegalArgumentException 如果输入字符串包含非法字符
     */
    public static String underscoreToCamelCase(String underscore) {
        return underscoreToCamelCase(underscore, true);
    }

    /**
     * 将驼峰命名转换为下划线命名
     * 支持大小写控制
     * <p>
     * 步骤：
     * 1. 验证输入字符串
     * 2. 识别驼峰命名的单词边界
     * 3. 转换每个单词为指定大小写
     * 4. 用下划线连接单词
     *
     * @param camelCase 驼峰命名的字符串
     * @param upperCase 是否将结果转换为大写
     * @return 下划线命名的字符串
     * @throws IllegalArgumentException 如果输入字符串包含非法字符
     */
    private static String camelToUnderscore(String camelCase, boolean upperCase) {
        validateInput(camelCase);

        if (camelCase.isEmpty()) {
            return camelCase;
        }

        String[] parts = UPPER_CASE_PATTERN.split(camelCase);
        if (parts.length == 0) {
            return camelCase;
        }

        StringBuilder underscore = new StringBuilder(camelCase.length() + parts.length - 1);
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (!part.isEmpty()) {
                if (i > 0) {
                    underscore.append("_");
                }
                if (upperCase) {
                    underscore.append(part.toUpperCase());
                } else {
                    underscore.append(part.toLowerCase());
                }
            }
        }
        return underscore.toString();
    }

    /**
     * 将驼峰命名转换为大写下划线命名
     * 所有单词转换为大写并用下划线连接
     *
     * @param camelCase 驼峰命名的字符串
     * @return 大写下划线命名的字符串
     * @throws IllegalArgumentException 如果输入字符串包含非法字符
     */
    public static String camelToUpperCaseUnderscore(String camelCase) {
        return camelToUnderscore(camelCase, true);
    }

    /**
     * 将驼峰命名转换为小写下划线命名
     * 所有单词转换为小写并用下划线连接
     *
     * @param camelCase 驼峰命名的字符串
     * @return 小写下划线命名的字符串
     * @throws IllegalArgumentException 如果输入字符串包含非法字符
     */
    public static String camelToLowerCaseUnderscore(String camelCase) {
        return camelToUnderscore(camelCase, false);
    }

    /**
     * 将驼峰命名或下划线命名转换为短横线命名
     * 支持大小写控制
     * <p>
     * 步骤：
     * 1. 验证输入字符串
     * 2. 转换为下划线命名
     * 3. 将下划线替换为短横线
     *
     * @param input     驼峰命名或下划线命名的字符串
     * @param upperCase 是否将结果转换为大写
     * @return 短横线命名的字符串
     * @throws IllegalArgumentException 如果输入字符串包含非法字符
     */
    private static String toKebabCase(String input, boolean upperCase) {
        validateInput(input);

        String intermediate = camelToUnderscore(input, upperCase);
        return intermediate.replace("_", "-");
    }

    /**
     * 将驼峰命名或下划线命名转换为大写短横线命名
     * 所有单词转换为大写并用短横线连接
     *
     * @param input 驼峰命名或下划线命名的字符串
     * @return 大写短横线命名的字符串
     * @throws IllegalArgumentException 如果输入字符串包含非法字符
     */
    public static String toUpperCaseKebabCase(String input) {
        return toKebabCase(input, true);
    }

    /**
     * 将驼峰命名或下划线命名转换为小写短横线命名
     * 所有单词转换为小写并用短横线连接
     *
     * @param input 驼峰命名或下划线命名的字符串
     * @return 小写短横线命名的字符串
     * @throws IllegalArgumentException 如果输入字符串包含非法字符
     */
    public static String toLowerCaseKebabCase(String input) {
        return toKebabCase(input, false);
    }

    /**
     * 验证输入字符串
     * 检查字符串是否符合命名规范要求
     * <p>
     * 步骤：
     * 1. 检查字符串是否为null
     * 2. 检查字符串是否只包含合法字符
     *
     * @param input 要验证的字符串
     * @throws IllegalArgumentException 如果输入字符串包含非法字符
     */
    private static void validateInput(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input string cannot be null");
        }
        if (!VALID_NAME_PATTERN.matcher(input).matches()) {
            throw new IllegalArgumentException("Input string contains invalid characters: " + input);
        }
    }
}
