package dataExtraction.water;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * <p>Description:</p>
 *
 * @author chenzhitao
 * @date 2022年12月23日
 */
@Configuration(value = "waterPrimaryConfig")
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "waterentityManagerFactoryPrimary",
        transactionManagerRef = "watertransactionManagerPrimary",
        basePackages = {"dataExtraction.water.dao",}) //设置Repository所在位置
public class PrimaryConfig {
    @Bean
    @ConfigurationProperties("spring.datasource.water.properties")
    public Properties waterprimaryHibernateProperties() {
        return new Properties();
    }

    @Bean
    @Qualifier("waterjpaDataSource")
    @ConfigurationProperties("spring.datasource.water")
    public DataSource waterprimatyDataSource() {
        return new HikariDataSource();
    }

    @Bean(name = "waterentityManagerFactoryPrimary")
    public LocalContainerEntityManagerFactoryBean waterentityManagerFactoryPrimary() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(false);
        vendorAdapter.setGenerateDdl(false);

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(waterprimatyDataSource());
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        factoryBean.setPackagesToScan("dataExtraction.water.entity");
        factoryBean.setJpaProperties(waterprimaryHibernateProperties());
        factoryBean.setPersistenceUnitName("water");
        return factoryBean;
    }

    @Bean
    PlatformTransactionManager watertransactionManagerPrimary() {
        return new JpaTransactionManager(waterentityManagerFactoryPrimary().getObject());
    }
}
