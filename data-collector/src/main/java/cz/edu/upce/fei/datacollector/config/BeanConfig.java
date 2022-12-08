package cz.edu.upce.fei.datacollector.config;

import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class BeanConfig {

	@Bean
	public HealthIndicator dataSourceHealthIndicator(final DataSource dataSource) {
		return new DataSourceHealthIndicator(dataSource, "SELECT 1;");
	}
}
