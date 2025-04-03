package com.original.generator.core.utils;

import java.util.regex.Pattern;

public class StringConverterUtil {

    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");
    private static final Pattern UPPER_CASE_PATTERN = Pattern.compile("(?=[A-Z])");
    private static final Pattern UNDERSCORE_PATTERN = Pattern.compile("_");

    /**
     * 将下划线命名转换为驼峰命名
     *
     * @param underscore           下划线命名的字符串
     * @param lowerCaseFirstLetter 是否将首字母小写
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
     * 将下划线命名转换为大驼峰命名
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
     *
     * @param camelCase 驼峰命名的字符串
     * @param upperCase 是否将下划线命名转换为大写
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
     *
     * @param camelCase 驼峰命名的字符串
     * @return 小写下划线命名的字符串
     * @throws IllegalArgumentException 如果输入字符串包含非法字符
     */
    public static String camelToLowerCaseUnderscore(String camelCase) {
        return camelToUnderscore(camelCase, false);
    }

    /**
     * 将驼峰命名或下划线命名转换为短横杠命名
     *
     * @param input     驼峰命名或下划线命名的字符串
     * @param upperCase 是否将短横杠命名转换为大写
     * @return 短横杠命名的字符串
     * @throws IllegalArgumentException 如果输入字符串包含非法字符
     */
    private static String toKebabCase(String input, boolean upperCase) {
        validateInput(input);

        String intermediate = camelToUnderscore(input, upperCase);
        return intermediate.replace("_", "-");
    }

    /**
     * 将驼峰命名或下划线命名转换为大写短横杠命名
     *
     * @param input 驼峰命名或下划线命名的字符串
     * @return 大写短横杠命名的字符串
     * @throws IllegalArgumentException 如果输入字符串包含非法字符
     */
    public static String toUpperCaseKebabCase(String input) {
        return toKebabCase(input, true);
    }

    /**
     * 将驼峰命名或下划线命名转换为小写短横杠命名
     *
     * @param input 驼峰命名或下划线命名的字符串
     * @return 小写短横杠命名的字符串
     * @throws IllegalArgumentException 如果输入字符串包含非法字符
     */
    public static String toLowerCaseKebabCase(String input) {
        return toKebabCase(input, false);
    }

    /**
     * 验证输入字符串
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
