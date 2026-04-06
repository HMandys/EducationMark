package com.edumark.common.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson 全局配置
 * 解决雪花算法生成的 Long ID 在前端 JavaScript 中精度丢失的问题
 *
 * @author HMandy
 */
@Configuration
public class JacksonConfig {

    /**
     * 将 Long 类型序列化为 String，避免前端 JavaScript 精度丢失
     * JavaScript Number.MAX_SAFE_INTEGER = 9007199254740991 (约16位)
     * 雪花算法生成的 ID 通常是 18-19 位，会超出安全范围
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            // Long 类型序列化为 String，避免前端精度丢失
            builder.serializerByType(Long.class, ToStringSerializer.instance);
            builder.serializerByType(Long.TYPE, ToStringSerializer.instance);

            // 显式保留 Java 8 时间类型支持，避免自定义配置覆盖默认模块
            builder.modulesToInstall(JavaTimeModule.class);
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        };
    }
}
