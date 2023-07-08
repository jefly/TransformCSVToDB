package com.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationManager {

	private static final String PROPERTY_FILE = "application.properties";
	private static ConfigurationManager instance = null;
	private Properties properties = null;
	
	private ConfigurationManager() {
		
		properties = new Properties();
		
		try(InputStream inputStream = ConfigurationManager.class.getClassLoader().getResourceAsStream(PROPERTY_FILE)){
			
			properties.load(inputStream);
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ConfigurationManager getInstance() {
		
		if(instance == null) {
			
			synchronized (ConfigurationManager.class) {
				
				if(instance == null) {
					instance = new ConfigurationManager();
				}
			}
		}
		
		return instance;
	}
	
	public String getProperty(String key) {
		return properties.get(key).toString();
	}
}
