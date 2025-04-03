package com.original.generator.core.utils;

import com.original.generator.core.domain.bo.BusinessModuleBo;
import com.original.generator.core.domain.bo.FieldBo;
import com.original.generator.core.domain.bo.GenerateProjectBo;
import com.original.generator.core.exception.SqlGenerationException;

import java.util.List;
import java.util.stream.Collectors;

public class GenerateSqlScriptUtils {
    private static final String DEFAULT_CHARSET = "utf8mb4";
    private static final String DEFAULT_COLLATION = "utf8mb4_general_ci";
    private static final String DEFAULT_ENGINE = "InnoDB";

    public static String generateSqlScript(GenerateProjectBo project) {
        if (project == null) {
            throw new SqlGenerationException("Project cannot be null");
        }

        List<BusinessModuleBo> modules = project.getBusinessModuleList();
        if (modules == null || modules.isEmpty()) {
            throw new SqlGenerationException("No business modules found in project");
        }

        return modules.stream()
                .map(GenerateSqlScriptUtils::generateModuleSqlScript)
                .collect(Collectors.joining("\n\n"));
    }

    private static String generateModuleSqlScript(BusinessModuleBo module) {
        if (module == null) {
            throw new SqlGenerationException("Module cannot be null");
        }

        String moduleName = module.getModuleName();
        if (moduleName == null || moduleName.trim().isEmpty()) {
            throw new SqlGenerationException("Module name cannot be null or empty");
        }

        List<FieldBo> fields = module.getFieldList();
        if (fields == null || fields.isEmpty()) {
            throw new SqlGenerationException("No fields found in module: " + moduleName);
        }

        String tableName = StringConverterUtil.camelToLowerCaseUnderscore(moduleName);
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS `").append(tableName).append("` (\n");

        // 添加字段定义
        String fieldsSql = fields.stream()
                .map(GenerateSqlScriptUtils::getColumnSqlScript)
                .collect(Collectors.joining(",\n"));
        sql.append(fieldsSql);

        // 添加主键
        String primaryKey = fields.stream()
                .filter(FieldBo::isPrimaryKey)
                .map(field -> "`" + field.getColumnName() + "`")
                .collect(Collectors.joining(", "));
        if (!primaryKey.isEmpty()) {
            sql.append(",\nPRIMARY KEY (").append(primaryKey).append(")");
        }

        // 添加表选项
        sql.append("\n) ENGINE=").append(DEFAULT_ENGINE)
                .append(" DEFAULT CHARSET=").append(DEFAULT_CHARSET)
                .append(" COLLATE=").append(DEFAULT_COLLATION)
                .append(" COMMENT='").append(module.getComment()).append("';");

        return sql.toString();
    }

    private static String getColumnSqlScript(FieldBo field) {
        if (field == null) {
            throw new SqlGenerationException("Field cannot be null");
        }

        String columnName = field.getColumnName();
        if (columnName == null || columnName.trim().isEmpty()) {
            throw new SqlGenerationException("Column name cannot be null or empty");
        }

        StringBuilder sql = new StringBuilder();
        sql.append("  `").append(columnName).append("` ").append(field.getColumnType());

        // 添加长度和小数点
        if (field.getColumnLength() != null) {
            sql.append("(").append(field.getColumnLength());
            if (field.getColumnDecimalPoint() != null) {
                sql.append(",").append(field.getColumnDecimalPoint());
            }
            sql.append(")");
        }

        // 添加非空约束
        if (field.isPrimaryKey()) {
            sql.append(" NOT NULL");
        }

        // 添加默认值
        if (field.getColumnDefaultValue() != null) {
            sql.append(" DEFAULT ").append(field.getColumnDefaultValue());
        }

        // 添加注释
        if (field.getComment() != null && !field.getComment().trim().isEmpty()) {
            sql.append(" COMMENT '").append(field.getComment()).append("'");
        }

        return sql.toString();
    }
}
