package com.nhis.comm.config;

import com.nhis.comm.context.orm.DefaultRepository;
import com.nhis.comm.context.orm.OrmInterceptor;
import com.nhis.comm.context.orm.SystemRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import com.nhis.comm.context.orm.DefaultRepository.DefaultDataSourceProperties;
import com.nhis.comm.context.orm.SystemRepository.SystemDataSourceProperties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * 응용 프로그램의 데이터베이스 연결 정의를 표현합니다
 */
@Configuration
@EnableConfigurationProperties({DefaultDataSourceProperties.class, SystemDataSourceProperties.class })
public class ApplicationDbConfig {

    /** 지속시 메타 정보 플러그 접속을 할 인터셉터 */
    @Bean
    OrmInterceptor ormInterceptor() {
        return new OrmInterceptor();
    }
    
    /** 표준 스키마에 대한 연결 정의를 표현합니다. */
    @Configuration
    static class DefaultDbConfig {
        @Bean
        @Primary
        @DependsOn(DefaultRepository.BeanNameEmf)
        DefaultRepository defaultRepository() {
            return new DefaultRepository();
        }
        
        @Bean(name = DefaultRepository.BeanNameDs, destroyMethod = "close")
        @Primary
        DataSource dataSource(DefaultDataSourceProperties props) {
            return props.dataSource();
        }
        
        @Bean(name = DefaultRepository.BeanNameEmf)
        @Primary
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(
                DefaultDataSourceProperties props,
                @Qualifier(DefaultRepository.BeanNameDs) final DataSource dataSource) {
            return props.entityManagerFactoryBean(dataSource);
        }

        @Bean(name = DefaultRepository.BeanNameTx)
        @Primary
        JpaTransactionManager transactionManager(
                DefaultDataSourceProperties props,
                @Qualifier(DefaultRepository.BeanNameEmf) final EntityManagerFactory emf) {
            return props.transactionManager(emf);
        }

    }

    /** 시스템 스키마에 연결 정의를 표현합니다 */
    @Configuration
    static class SystemDbConfig {
        
        @Bean
        @DependsOn(SystemRepository.BeanNameEmf)
        SystemRepository systemRepository() {
            return new SystemRepository();
        }
        
        @Bean(name = SystemRepository.BeanNameDs, destroyMethod = "close")
        DataSource systemDataSource(SystemDataSourceProperties props) {
            return props.dataSource();
        }
        
        @Bean(name = SystemRepository.BeanNameEmf)
        LocalContainerEntityManagerFactoryBean systemEntityManagerFactoryBean(
                SystemDataSourceProperties props,
                @Qualifier(SystemRepository.BeanNameDs) final DataSource dataSource) {
            return props.entityManagerFactoryBean(dataSource);
        }

        @Bean(name = SystemRepository.BeanNameTx)
        JpaTransactionManager systemTransactionManager(
                SystemDataSourceProperties props,
                @Qualifier(SystemRepository.BeanNameEmf) final EntityManagerFactory emf) {
            return props.transactionManager(emf);
        }

    }
    
}
