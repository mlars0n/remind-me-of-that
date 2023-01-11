package com.remindmeofthat.application;

import com.vaadin.flow.spring.annotation.EnableVaadin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
@ComponentScan(basePackages = "com.remindmeofthat")
@EnableVaadin(value = "com.remindmeofthat.web") //Where the Vaadin components live
public class RemindMeOfThatApplication {

	public static final Logger logger = LoggerFactory.getLogger(RemindMeOfThatApplication.class);

	public static final String CONFIG_FILE_SYSTEM_PROPERTY_NAME = "config.file";
	public static final String DB_URL_ENV_VAR_NAME = "local.db.url";
	public static final String DB_USERNAME_ENV_VAR_NAME = "local.db.username";
	public static final String DB_PASSWORD_ENV_VAR_NAME = "local.db.password";

	public static void main(String[] args) {

		//Check if the env.config file exists as pointed to by a "config.file" Java system property
		String configFileLocation = System.getProperty(CONFIG_FILE_SYSTEM_PROPERTY_NAME);
		if (configFileLocation!= null) {

			logger.info("The system property \"-Dconfig.file\" is set. Reading properties from [{}].", configFileLocation);

			final Properties props = new Properties();
			try (InputStream input = new FileInputStream(configFileLocation)) {
				props.load(input);
			} catch (IOException e) {

				//Exit if we could not find the properties file
				logger.error("Could not load properties file from [{}]", configFileLocation, e);
				System.exit(1);
			}

			//Check to make sure you got all the appropriate properties (this will help smooth out local setups)
			String configEnvFileErrorText = "Error: missing property [{}] or value in config file. Please check that this key is set properly. Exiting.";

			Arrays.stream(new String[] {
					DB_URL_ENV_VAR_NAME,
					DB_USERNAME_ENV_VAR_NAME,
					DB_PASSWORD_ENV_VAR_NAME
			}).forEach(keyName -> {
				if (props.getProperty(keyName) == null || props.getProperty(keyName).length() == 0) {
					logger.error(configEnvFileErrorText, keyName);
					System.exit(1);
				} else {
					System.setProperty(keyName, props.getProperty(keyName));
				}
			});
		}

		String errorMsgSolutionText = "Make sure to set it as an environment variable (if running in a container), system variable, or in " +
				"a configuration file using the system property \"-Dconfig.file\".";

		if (System.getProperty(DB_URL_ENV_VAR_NAME) == null && System.getenv(DB_URL_ENV_VAR_NAME) == null) {
			logger.error("Error: missing DB URL property [{}]. Exiting." + errorMsgSolutionText, DB_URL_ENV_VAR_NAME);
			System.exit(1);
		}

		//Check to make sure you got the correct environment variables for DB access and Google auth access
		// (basically for when this is running in a container). This should make it clear when the container environment has an issue
		if (System.getProperty(DB_USERNAME_ENV_VAR_NAME) == null && System.getenv(DB_USERNAME_ENV_VAR_NAME) == null) {
			logger.error("Error: missing DB username property [{}]. Exiting." + errorMsgSolutionText, DB_USERNAME_ENV_VAR_NAME);
			System.exit(1);
		}

		if (System.getProperty(DB_PASSWORD_ENV_VAR_NAME) == null && System.getenv(DB_PASSWORD_ENV_VAR_NAME) == null) {
			logger.error("Error: missing DB password property [{}]. Exiting." + errorMsgSolutionText, DB_PASSWORD_ENV_VAR_NAME);
			System.exit(1);
		}

		//Got the variables we need, proceed
		SpringApplication.run(RemindMeOfThatApplication.class, args);
	}

}
