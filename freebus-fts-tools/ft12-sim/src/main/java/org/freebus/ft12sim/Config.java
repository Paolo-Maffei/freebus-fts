package org.freebus.ft12sim;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;

public class Config extends SimpleConfig {
	private Logger logger = Logger.getLogger(getClass());

	private Environment env;

	public Config() throws IOException {
		env = new Environment();
			try {
			load(env.getAppDir() + "/config.ini");

			logger.info("Load Config");
		} catch (FileNotFoundException e1) {
			put("comport", "");
			put("XMLfile", "");
			put("XSDfile", "");
			save(env.getAppDir() + "/config.ini");

		} catch (IOException e1) {

		}
		
	}
	public void save() throws IOException {
		save(env.getAppDir() + "/config.ini");

	}
}
