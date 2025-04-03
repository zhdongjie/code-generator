package com.original.generator.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "velocity")
public class VelocityProperties {
    private String resourceLoader = "class";
    private String classResourceLoader = "org.apache.velocity.runtime.resource.loader.DataSourceResourceLoader";
    private String inputEncoding = "UTF-8";
    private String outputEncoding = "UTF-8";
    private boolean cacheEnabled = true;
    private int cacheSize = 100;
    private boolean precompileEnabled = true;
    private boolean hotReloadEnabled = false;
    private String datasourceTable = "tb_velocity_template";
    private String datasourceKeyColumn = "id_template";
    private String datasourceTemplateColumn = "template_definition";
    private String datasourceTimestampColumn = "template_timestamp";
    private int modificationCheckInterval = 60;

}