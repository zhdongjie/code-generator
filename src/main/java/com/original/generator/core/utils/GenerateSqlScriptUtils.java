package com.original.generator.core.utils;


import com.original.generator.core.domain.bo.BusinessModuleBo;
import com.original.generator.core.domain.bo.FieldBo;
import com.original.generator.core.domain.bo.GenerateProjectBo;

import java.util.List;

public class GenerateSqlScriptUtils {


    public static final String DATABASE_CHARACTER = "utf8mb4";
    public static final String DATABASE_COLLATE = "utf8mb4_general_ci";

    public static String generateSqlScript(GenerateProjectBo generateProject) {
        List<BusinessModuleBo> businessModules = generateProject.getBusinessModuleList();
        boolean cover = generateProject.isCover();
        StringBuilder sqlScript = new StringBuilder();
        businessModules.forEach(businessModule -> {
            String moduleSqlScript = generateModuleSqlScript(businessModule, cover);
            sqlScript.append(moduleSqlScript)
                    .append("\n");
        });
        return sqlScript.toString();
    }

    private static String generateModuleSqlScript(BusinessModuleBo businessModule, boolean cover) {
        String moduleName = businessModule.getModuleName();
        // 数据库表名
        String tableName = StringConverterUtil.camelToLowerCaseUnderscore(moduleName);
        String comment = businessModule.getComment();

        List<FieldBo> fields = businessModule.getFieldList();
        if (fields.isEmpty()) {
            return "";
        }
        FieldBo primaryKeyField = businessModule.getFieldList()
                .stream()
                .filter(FieldBo::isPrimaryKey)
                .findFirst()
                .orElse(null);

        if (primaryKeyField == null) {
            return "";
        }

        StringBuilder sqlScript = new StringBuilder();
        if (cover) {
            sqlScript.append("DROP TABLE IF EXISTS `").append(tableName).append("`;").append("\n")
                    .append("CREATE TABLE `").append(tableName).append("` (").append("\n");
        } else {
            sqlScript.append("CREATE TABLE IF NOT EXISTS `").append(tableName).append("` (").append("\n");
        }
        // 拼接每个字段对应的SQL
        businessModule.getFieldList().forEach(fieldBo -> sqlScript.append(getColumnSqlScript(fieldBo)));

        // 拼接主键对应的SQL
        // 字段名
        String primaryKeyColumnName = primaryKeyField.getColumnName();
        String primaryKeySql = "  PRIMARY KEY (`" + primaryKeyColumnName + "`) USING BTREE";
        sqlScript.append(primaryKeySql);

        // 添加主键和表注释
        sqlScript.append("\n) ENGINE = InnoDB CHARACTER SET = ").append(DATABASE_CHARACTER)
                .append(" COLLATE = ").append(DATABASE_COLLATE)
                .append(" COMMENT = '").append(comment).append("' ROW_FORMAT = Dynamic;\n");

        return sqlScript.toString();
    }

    private static String getColumnSqlScript(FieldBo field) {
        StringBuilder columnSqlScript = new StringBuilder();
        // 字段名
        String columnName = field.getColumnName();
        // 字段类型
        String columnType = field.getColumnType();
        columnSqlScript.append("  `")
                .append(columnName).append("` ")
                .append(columnType);
        if (field.getColumnLength() != null) {
            columnSqlScript.append("(")
                    .append(field.getColumnLength());
            if (field.getColumnDecimalPoint() != null) {
                columnSqlScript.append(",")
                        .append(field.getColumnDecimalPoint());
            }
            columnSqlScript.append(")");
        }

        if (field.getFieldType().equals("String")) {
            columnSqlScript.append(" CHARACTER SET ")
                    .append(DATABASE_CHARACTER)
                    .append(" COLLATE ")
                    .append(DATABASE_COLLATE);
        }

        if (field.isPrimaryKey()) {
            columnSqlScript.append(" NOT NULL");
        } else {
            columnSqlScript.append(" NULL DEFAULT NULL");
        }

        // 添加字段注释
        if (field.getComment() != null) {
            columnSqlScript.append(" COMMENT '")
                    .append(field.getComment())
                    .append("'");
        }
        columnSqlScript.append(",\n");
        return columnSqlScript.toString();
    }

}
