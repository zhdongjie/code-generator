package com.original.generator.core.utils;

public class StringConverterUtil {

    /**
     * 将下划线命名转换为驼峰命名
     *
     * @param underscore 下划线命名的字符串
     * @param lowerCaseFirstLetter 是否将首字母小写
     * @return 驼峰命名的字符串
     */
    private static String underscoreToCamelCase(String underscore, boolean lowerCaseFirstLetter) {
        if (underscore == null || underscore.isEmpty()) {
            return underscore;
        }
        String[] parts = underscore.split("_");
        StringBuilder camelCase = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (!part.isEmpty()) {
                if (i == 0 && lowerCaseFirstLetter) {
                    camelCase.append(Character.toLowerCase(part.charAt(0)));
                } else {
                    camelCase.append(Character.toUpperCase(part.charAt(0)));
                }
                camelCase.append(part.substring(1).toLowerCase());
            }
        }
        return camelCase.toString();
    }

    /**
     * 将下划线命名转换为大驼峰命名
     *
     * @param underscore 下划线命名的字符串
     * @return 大驼峰命名的字符串
     */
    public static String underscoreToPascalCase(String underscore) {
        return underscoreToCamelCase(underscore, false);
    }

    /**
     * 将下划线命名转换为小驼峰命名
     *
     * @param underscore 下划线命名的字符串
     * @return 小驼峰命名的字符串
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
     */
    private static String camelToUnderscore(String camelCase, boolean upperCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }
        StringBuilder underscore = new StringBuilder();
        for (int i = 0; i < camelCase.length(); i++) {
            char ch = camelCase.charAt(i);
            if (Character.isUpperCase(ch)) {
                if (i > 0) {
                    underscore.append("_");
                }
                underscore.append(upperCase ? Character.toUpperCase(ch) : Character.toLowerCase(ch));
            } else {
                underscore.append(ch);
            }
        }
        return underscore.toString();
    }

    /**
     * 将驼峰命名转换为大写下划线命名
     *
     * @param camelCase 驼峰命名的字符串
     * @return 大写下划线命名的字符串
     */
    public static String camelToUpperCaseUnderscore(String camelCase) {
        return camelToUnderscore(camelCase, true);
    }

    /**
     * 将驼峰命名转换为小写下划线命名
     *
     * @param camelCase 驼峰命名的字符串
     * @return 小写下划线命名的字符串
     */
    public static String camelToLowerCaseUnderscore(String camelCase) {
        return camelToUnderscore(camelCase, false);
    }

    /**
     * 将驼峰命名或下划线命名转换为短横杠命名
     *
     * @param input 驼峰命名或下划线命名的字符串
     * @param upperCase 是否将短横杠命名转换为大写
     * @return 短横杠命名的字符串
     */
    private static String toKebabCase(String input, boolean upperCase) {
        String intermediate = camelToUnderscore(input, upperCase);
        return intermediate.replace("_", "-");
    }

    /**
     * 将驼峰命名或下划线命名转换为大写短横杠命名
     *
     * @param input 驼峰命名或下划线命名的字符串
     * @return 大写短横杠命名的字符串
     */
    public static String toUpperCaseKebabCase(String input) {
        return toKebabCase(input, true);
    }

    /**
     * 将驼峰命名或下划线命名转换为小写短横杠命名
     *
     * @param input 驼峰命名或下划线命名的字符串
     * @return 小写短横杠命名的字符串
     */
    public static String toLowerCaseKebabCase(String input) {
        return toKebabCase(input, false);
    }

}
