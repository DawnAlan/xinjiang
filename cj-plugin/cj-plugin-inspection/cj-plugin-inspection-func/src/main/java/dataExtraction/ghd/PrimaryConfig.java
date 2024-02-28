package dataExtraction.ghd;

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
 * @author yangshuo
 * @date 2023年10月08日
 */
@Configuration(value = "ghdPrimaryConfig")
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "ghdEntityManagerFactoryPrimary",
        transactionManagerRef = "ghdTransactionManagerPrimary",
        basePackages = {"dataExtraction.ghd.dao"}) //设置Repository所在位置
public class PrimaryConfig {
    @Bean
    @ConfigurationProperties("spring.datasource.ghd.properties")
    public Properties ghdPrimaryHibernateProperties() {
        return new Properties();
    }

    //@Primary
    @Bean
    @Qualifier("ghdPrimatyDataSource")
    @ConfigurationProperties("spring.datasource.ghd")
    public DataSource ghdPrimatyDataSource() {
        return new HikariDataSource();
    }

    //@Primary
    @Bean(name = "ghdEntityManagerFactoryPrimary")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(false);
        vendorAdapter.setGenerateDdl(false);

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(ghdPrimatyDataSource());
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        factoryBean.setPackagesToScan("dataExtraction.ghd.entity");
        factoryBean.setJpaProperties(ghdPrimaryHibernateProperties());
        factoryBean.setPersistenceUnitName("ghd");
        return factoryBean;
    }

    //@Primary
    @Bean
    PlatformTransactionManager ghdTransactionManagerPrimary() {
        return new JpaTransactionManager(entityManagerFactoryPrimary().getObject());
    }
}