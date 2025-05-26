package org.petya8bachey.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfig {

    // 1. Определяем и привязываем свойства для основного (admin) источника данных
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource") // Привязывает свойства с префиксом 'spring.datasource'
    public DataSourceProperties adminDataSourceProperties() {
        return new DataSourceProperties();
    }

    // 2. Создаем основной HikariDataSource, используя привязанные свойства
    @Bean
    @Primary
    public HikariDataSource adminDataSource(@Qualifier("adminDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    // 3. Определяем и привязываем свойства для клиентского источника данных
    @Bean
    @ConfigurationProperties("app.datasource.client") // Привязывает свойства с префиксом 'app.datasource.client'
    public DataSourceProperties clientDataSourceProperties() {
        return new DataSourceProperties();
    }

    // 4. Создаем клиентский HikariDataSource, используя привязанные свойства
    @Bean
    public HikariDataSource clientDataSource(@Qualifier("clientDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    // 5. Определяем и привязываем свойства для брокерского источника данных
    @Bean
    @ConfigurationProperties("app.datasource.broker") // Привязывает свойства с префиксом 'app.datasource.broker'
    public DataSourceProperties brokerDataSourceProperties() {
        return new DataSourceProperties();
    }

    // 6. Создаем брокерский HikariDataSource, используя привязанные свойства
    @Bean
    public HikariDataSource brokerDataSource(@Qualifier("brokerDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }
}
