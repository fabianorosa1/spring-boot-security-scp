package com.sap.cp.appsec.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.zaxxer.hikari.HikariDataSource;

/**
 * This is the cloud database configuration class which reads the database
 * properties automatically from the application environment i.e VCAP_SERVICES
 * based on the service bound to the application.
 *
 */
@Profile("cloud")
@Configuration
public class CloudDatabaseConfig extends AbstractCloudConfig {
	private static final Logger logger = LoggerFactory.getLogger(CloudDatabaseConfig.class);

	private JSONObject getVcapServicesHanaCredentials() {
		JSONObject jsonObj = new JSONObject(System.getenv("VCAP_SERVICES"));
		logger.trace(">>>Enter jsonObj: " + jsonObj);

		JSONArray jsonArr = jsonObj.getJSONArray("hana");
		logger.trace(">>>Enter jsonArr: " + jsonArr);

		JSONObject credentials = jsonArr.getJSONObject(0).getJSONObject("credentials");
		logger.trace(">>>Enter credentials: " + credentials);

		return credentials;
	}

	/**
	 * Returns cloud hana datasource
	 * 
	 * @return datasource
	 * @throws SQLException
	 */
	@Bean
	public DataSource datasource() throws SQLException {
		logger.info(">>>Enter datasource");

		JSONObject credentials = getVcapServicesHanaCredentials();

		return DataSourceBuilder.create()
				.type(HikariDataSource.class)
				.driverClassName(credentials.getString("driver"))
				.url(credentials.getString("url"))
				.username(credentials.getString("user"))
				.password(credentials.getString("password"))
				.build();	
	}
}