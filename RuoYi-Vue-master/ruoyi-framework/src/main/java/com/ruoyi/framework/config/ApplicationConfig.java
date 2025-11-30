package com.ruoyi.framework.config;

import java.util.TimeZone;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 程序注解配置
 *
 * @author ruoyi
 */
@Configuration
// 表示通过aop框架暴露该代理对象,AopContext能够访问
@EnableAspectJAutoProxy(exposeProxy = true)
// 指定要扫描的Mapper类的包的路径
@MapperScan("com.ruoyi.**.mapper")
public class ApplicationConfig
{
    /**
     * Jackson时区配置 - 使用Asia/Shanghai时区，与服务器时区一致
     * 注意：禁用 WRITE_DATES_AS_TIMESTAMPS，让 @JsonFormat 注解生效
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization()
    {
        return jacksonObjectMapperBuilder -> {
            // 设置为Asia/Shanghai时区（UTC+8），与服务器和数据库时区一致
            jacksonObjectMapperBuilder.timeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            // 禁用时间戳格式，使用 @JsonFormat 注解指定的格式
            jacksonObjectMapperBuilder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        };
    }
}
