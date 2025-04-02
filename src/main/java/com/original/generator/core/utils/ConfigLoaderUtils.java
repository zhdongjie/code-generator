package com.original.generator.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.original.generator.core.domain.info.TemplateProjectInfo;

import java.io.File;
import java.io.IOException;

public class ConfigLoaderUtils {

    public static TemplateProjectInfo loadConfig(String configFilePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(configFilePath), TemplateProjectInfo.class);
    }

}
