package com.auh.open.mq.consumer.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;

import static com.alibaba.druid.pool.DruidAbstractDataSource.DEFAULT_WHILE_IDLE;

@Configuration
@Slf4j
@MapperScan({"com.auh.open.mq.consumer.mapper", "com.auh.open.mq.consumer.mapper.xml"})
@EnableTransactionManagement
@Getter
@Setter
public class MybatisPlugConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource druidDataSource() throws SQLException {
        DruidDataSource datasource = new DruidDataSource();
        //物理连接数
        datasource.setInitialSize(3);
        //最小连接数
        datasource.setMinIdle(1);
        //60秒超时
        datasource.setMaxWait(60000);
        datasource.setRemoveAbandoned(true);
        datasource.setFilters("config,stat,wall,slf4j");
        datasource.setConnectionProperties("druid.log.stmt=true;druid.log.rs=true;druid.log.stmt.executableSql=true");
        datasource.setMinEvictableIdleTimeMillis(60000);
        datasource.setRemoveAbandonedTimeout(2 * 60 * 60 * 1000);
        datasource.setValidationQuery("SELECT 1");
        datasource.setTestOnBorrow(true);
        datasource.setTestWhileIdle(DEFAULT_WHILE_IDLE);
        return datasource;
    }

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }


}
