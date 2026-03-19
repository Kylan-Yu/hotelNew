package com.kylan.hotel.config;

import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisConfig {

    @Bean
    public Interceptor sqlDataScopeInterceptor() {
        return new SqlDataScopeInterceptor();
    }

    @Bean
    public ConfigurationCustomizer mybatisConfigurationCustomizer(Interceptor sqlDataScopeInterceptor) {
        return configuration -> configuration.addInterceptor(sqlDataScopeInterceptor);
    }
}
