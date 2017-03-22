/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

/**
 *
 * @author Max and Arthur
 */
public class Config {

	private Properties myPref;

	private static Config instance;

	private File configfolder;

	public static final Font DEFAULT_FONT = new Font("SansSerif", Font.BOLD, 18);

	public static Config getInstance() {

		if (instance == null) {
			try {
				instance = new Config();
			} catch (URISyntaxException e) {
				// FATALITY
				e.printStackTrace();
			}
		}

		return instance;
	}

	private Config() throws URISyntaxException {

		// config is stored next to the jar file
		CodeSource codeSource = Main.class.getProtectionDomain().getCodeSource();
		File jarFile = new File(codeSource.getLocation().toURI().getPath());
		String jarDir = jarFile.getParentFile().getPath();

		configfolder = new File(jarDir + File.separator + "ksarcfg");

		if (!configfolder.exists()) {
			configfolder.mkdirs();
		}

		// use .properties file to store config
		myPref = new Properties();

		try {
			FileInputStream input = new FileInputStream(
					configfolder.getAbsolutePath() + File.separator + "config.properties");
			// load a properties file
			myPref.load(input);

		} catch (IOException e) {
			// load defaut properties
			myPref.setProperty("lookAndFeel", UIManager.getLookAndFeel().getName());
			myPref.setProperty("lastReadDirectory", "");
			myPref.setProperty("IgnoreLinesBeginingWith", "Average:@|@##@|@Summary@|@Moyenne");
			myPref.setProperty("HeaderDateFormat", "DD/MM/YYYY");
			myPref.setProperty("VersionNumber","0.0.0");
		}

	}

	public void saveConfig() {

		try (FileOutputStream output = new FileOutputStream(
				configfolder.getAbsolutePath() + File.separator + "config.properties")) {

			// save properties to project root folder
			myPref.store(output, null);

		} catch (IOException e) {
			Logger.getLogger(Config.class.getName()).log(Level.WARNING, "Unable to save configuration file", e);
		}
	}

	public String getLandf() {
		return myPref.getProperty("lookAndFeel");
	}

	public void setLandf(String landf) {
		myPref.setProperty("lookAndFeel", landf);
	}

	public File getLastReadDirectory() {
		return new File(myPref.getProperty("lastReadDirectory"));
	}

	public void setLastReadDirectory(String lastReadDirectory) {
		if (lastReadDirectory != null && !lastReadDirectory.trim().isEmpty()) {
			myPref.put("lastReadDirectory", lastReadDirectory);
		}
	}

	public void setLastReadDirectory(File lastReadDirectory) {
		if (lastReadDirectory != null) {
			setLastReadDirectory(lastReadDirectory.toString());
		}
	}

	public File getLastExportDirectory() {
		return new File(myPref.getProperty("lastExportDirectory", ""));
	}

	public void setLastExportDirectory(String lastExportDirectory) {
		if (lastExportDirectory != null && !lastExportDirectory.trim().isEmpty()) {
			myPref.put("lastExportDirectory", lastExportDirectory);
		}
	}

	public void setLastExportDirectory(File lastExportDirectory) {
		if (lastExportDirectory != null) {
			setLastExportDirectory(lastExportDirectory.toString());
		}
	}

	public String getLastCommand() {
		return myPref.getProperty("lastCommand", "");
	}

	public void setLastCommand(String lastCommand) {
		myPref.put("lastCommand", lastCommand);
	}

	public ArrayList<String> getHost_history() {

		ArrayList<String> hostHistory = new ArrayList<>();

		int size = 0;
		try {
			Integer.parseInt(myPref.getProperty("HostHistory", "0"));
		} catch (NumberFormatException e) {
			// it's ok to fail
		}

		for (int i = 0; i < size; i++) {
			hostHistory.add(myPref.getProperty("HostHistory_" + i, ""));
		}

		return hostHistory;
	}

	public void addHost_history(String e) {
		ArrayList<String> tmp = getHost_history();
		tmp.add(e);

		for (int i = 0; i < tmp.size(); i++) {
			myPref.put("HostHistory_" + i, tmp.get(i));
		}

		myPref.setProperty("HostHistory", String.valueOf(tmp.size()));

	}

	public static Font getDEFAULT_FONT() {
		return DEFAULT_FONT;
	}

	public int getImageHeight() {

		int res = 600;

		try {
			Integer.parseInt(myPref.getProperty("ImageHeight", "600"));
		} catch (NumberFormatException e) {
			// it's ok to fail
		}

		return res;
	}

	public void setImageHeight(int ImageHeight) {
		myPref.setProperty("ImageHeight", String.valueOf(ImageHeight));
	}

	public int getImageWidth() {

		int res = 800;

		try {
			Integer.parseInt(myPref.getProperty("ImageWidth", "800"));
		} catch (NumberFormatException e) {
			// it's ok to fail
		}

		return res;

	}

	public void setImageWidth(int ImageWidth) {
		myPref.setProperty("ImageWidth", String.valueOf(ImageWidth));
	}

	public String getPDFPageFormat() {
		return myPref.getProperty("PDFPageFormat", "A4");
	}

	public void setPDFPageFormat(String PDFPageFormat) {
		myPref.put("PDFPageFormat", PDFPageFormat);
	}

	public String getLinuxDateFormat() {
		return myPref.getProperty("LinuxDateFormat", "Always ask");
	}

	public void setLinuxDateFormat(String LinuxDateFormat) {
		myPref.put("LinuxDateFormat", LinuxDateFormat);
	}

	public ArrayList<String> getIgnoreLinesBeginingWith() {
		ArrayList<String> ignoreLine = new ArrayList<>();

		String toparse = myPref.getProperty("IgnoreLinesBeginingWith", "Average:@|@##@|@Summary@|@Moyenne");
		ignoreLine.addAll(Arrays.asList(toparse.split("@|@")));

		return ignoreLine;

	}


	public String getVersionNumber() {
		return myPref.getProperty("VersionNumber");
	}

	public String getDateFormat() {
		return myPref.getProperty("HeaderDateFormat");
	}
	
	public File getConfigFolder(){
		return configfolder;
	}

}
