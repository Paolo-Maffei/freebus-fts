package org.freebus.ft12sim;

import java.io.File;

import org.apache.log4j.BasicConfigurator;

/**
 * System specific settings.
 */
public final class Environment {
	static private final String osname, tempDir, homeDir;
	static private String appDir = null;
	static private String appName = "ft12sim";

	static {
		// Configure Log4J
		BasicConfigurator.configure();

		osname = System.getProperty("os.name", "").toLowerCase();
		if (osname.startsWith("windows")) {
			tempDir = "c:/windows/temp";
			homeDir = System.getenv("USERPROFILE");
		} else {
			tempDir = "/tmp";
			homeDir = System.getenv("HOME");
		}
	}

	/**
	 * @return the name of the operating system.
	 */
	public static String getOS() {
		return osname;
	}

	/**
	 * @return the temporary directory (/tmp for Unix, c:\temp for Windows).
	 */
	public static String getTempDir() {
		return tempDir;
	}

	/**
	 * @return the user's home directory.
	 */
	public static String getHomeDir() {
		return homeDir;
	}

	/**
	 * Returns the application's user-data directory. The name of the directory
	 * depends on the platform and uses the application name.
	 *
	 * @return the directory for application specific user data.
	 */
	public String getAppDir() {
		if (appDir == null) {
			if (osname.startsWith("linux")) {
				appDir = homeDir + "/." + appName;
			} else {
				appDir = homeDir + '/' + appName;
			}

			final File appDirFile = new File(appDir);
			if (!appDirFile.isDirectory()) {
				if (!appDirFile.mkdir())
					throw new RuntimeException(
							"Cannot create application directory: " + appDir);
			}
		}
		return appDir;
	}

	/**
	 * Set the name of the application. Default: "fts"
	 */
	public static void setAppName(String appName) {
		appName = appName.replace('\\', '-').replace('/', '-')
				.replace(':', '-').replace(' ', '_');
		Environment.appName = appName;
		appDir = null;
	}

	/**
	 * @return the name of the application.
	 */
	public static String getAppName() {
		return appName;
	}
}
