package com.bigvikinggames.circuitchecker.logging;

import java.io.File;
import java.net.URL;

import org.apache.log4j.PropertyConfigurator;

public class LoggingConfig {

	/**
	 * Configure log4j.
	 */
	public static void configureLog4j() {
		// Configure from built in config
		ClassLoader loader = LoggingConfig.class.getClassLoader();
		URL configUrl = loader.getResource("log4j.properties");
		PropertyConfigurator.configure(configUrl);

		// Then check for config file
		File log4jFile = new File("config/log4j.properties");
		PropertyConfigurator.configureAndWatch(log4jFile.getAbsolutePath());
	}

}
