package com.original.generator.core.utils;

import com.original.generator.core.domain.bo.BusinessModuleBo;
import com.original.generator.core.domain.bo.FieldBo;
import com.original.generator.core.domain.bo.GenerateProjectBo;
import com.original.generator.core.exception.SqlGenerationException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SQL脚本生成工具类
 * 负责根据业务模块生成数据库表创建脚本
 * <p>
 * 主要功能：
 * 1. 生成完整的建表SQL脚本
 * 2. 支持字段类型、长度、默认值等配置
 * 3. 自动处理主键和表注释
 *
 * @author 代码生成器团队
 * @version 1.0
 */
public class GenerateSqlScriptUtils {
    /**
     * 默认字符集
     * 使用utf8mb4以支持完整的Unicode字符集
     */
    private static final String DEFAULT_CHARSET = "utf8mb4";

    /**
     * 默认排序规则
     * 使用utf8mb4_general_ci以支持不区分大小写的排序
     */
    private static final String DEFAULT_COLLATION = "utf8mb4_general_ci";

    /**
     * 默认存储引擎
     * 使用InnoDB以支持事务和外键
     */
    private static final String DEFAULT_ENGINE = "InnoDB";

    /**
     * 生成项目的SQL脚本
     * 根据项目配置生成所有业务模块的建表语句
     * <p>
     * 步骤：
     * 1. 验证项目对象
     * 2. 获取业务模块列表
     * 3. 为每个模块生成建表语句
     * 4. 合并所有SQL语句
     *
     * @param project 项目配置对象
     * @return 完整的SQL建表脚本
     * @throws SqlGenerationException 如果生成过程中出现错误
     */
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

    /**
     * 生成单个模块的SQL脚本
     * 根据业务模块配置生成建表语句
     * <p>
     * 步骤：
     * 1. 验证模块对象
     * 2. 获取字段列表
     * 3. 生成表名
     * 4. 生成字段定义
     * 5. 添加主键约束
     * 6. 设置表选项
     *
     * @param module 业务模块对象
     * @return 模块的建表SQL语句
     * @throws SqlGenerationException 如果生成过程中出现错误
     */
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

    /**
     * 生成字段的SQL定义
     * 根据字段配置生成列定义语句
     * <p>
     * 步骤：
     * 1. 验证字段对象
     * 2. 生成列名和类型
     * 3. 添加长度和小数点配置
     * 4. 添加非空约束
     * 5. 添加默认值
     * 6. 添加字段注释
     *
     * @param field 字段配置对象
     * @return 字段的SQL定义
     * @throws SqlGenerationException 如果生成过程中出现错误
     */
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
