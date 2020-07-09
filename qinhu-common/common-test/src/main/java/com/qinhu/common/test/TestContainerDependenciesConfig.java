package com.qinhu.common.test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import javax.sql.DataSource;

/**
 * @description: 测试容器依赖的Bean配置
 * @author: qh
 * @create: 2020-07-06 17:49
 **/
@Configuration
public class TestContainerDependenciesConfig {
//%r2f+_4ti@)u_s9ha^g0(=z2s2dj&yz0edf(9ak467mv=gb(e&
    @Autowired
    ConfigurableEnvironment environment;

    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        String  jdbcUrl = "jdbc:mysql://${embedded.mysql.host}:${embedded.mysql.port}/${embedded.mysql.schema}?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true";
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setJdbcUrl(environment.resolvePlaceholders(jdbcUrl));
        hikariConfig.setUsername(environment.getProperty("embedded.mysql.user"));
        hikariConfig.setPassword(environment.getProperty("embedded.mysql.password"));
        return new HikariDataSource(hikariConfig);
    }

}
