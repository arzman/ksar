/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.atomique.ksar.ui.Desktop;
import net.atomique.ksar.xml.CnxHistory;
import net.atomique.ksar.xml.ColumnConfig;
import net.atomique.ksar.xml.HostInfo;
import net.atomique.ksar.xml.OSConfig;

/**
 *
 * @author Max
 */
public class GlobalOptions {

	private Desktop UI;

	private HashMap<String, ColumnConfig> columnlist;
	private HashMap<String, OSConfig> OSlist;
	private HashMap<String, CnxHistory> HistoryList;
	private HashMap<String, HostInfo> HostInfoList;
	private boolean dodebug = false;
	private String CLfilename = null;
	private HashMap<String, Class> ParserMap;

	private static GlobalOptions instance;

	public static GlobalOptions getInstance() {

		if (instance == null) {
			instance = new GlobalOptions();
		}

		return instance;
	}

	public boolean hasUI() {
		if (UI != null) {
			return true;
		}
		return false;
	}

	private GlobalOptions() {
		String[] OSParserNames = { "AIX", "HPUX", "Linux", "SunOS" };

		UI = new Desktop();

		columnlist = new HashMap<String, ColumnConfig>();
		OSlist = new HashMap<String, OSConfig>();
		ParserMap = new HashMap<String, Class>();
		HistoryList = new HashMap<String, CnxHistory>();
		HostInfoList = new HashMap<String, HostInfo>();

		for (String OSName : OSParserNames) {
			try {
				Class tmpclass = Class.forName("net.atomique.ksar.parser." + OSName);
				ParserMap.put(OSName, tmpclass);
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			}

		}

	}

	public void loadParserConfiguration() {

		XMLConfig tmp;
		String filename = Config.getInstance().getConfigFolder().getAbsolutePath() + File.separator + "Config.xml";
		File configXmlFile = new File(filename);

		if (!configXmlFile.exists()) {

			try (FileOutputStream fos = new FileOutputStream(configXmlFile);
					ReadableByteChannel src = Channels
							.newChannel(this.getClass().getResourceAsStream("/Config.xml"));) {

				FileChannel dest = fos.getChannel();

				dest.transferFrom(src, 0, Long.MAX_VALUE);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		try {
			tmp = new XMLConfig(new FileInputStream(filename));

			for (String parsername : ParserMap.keySet()) {

				
				String parserfilename = Config.getInstance().getConfigFolder().getAbsolutePath() + File.separator + parsername+".xml";
				File parserfilenameFile = new File(parserfilename);
				
				
				if (!parserfilenameFile.exists()) {

					try (FileOutputStream fos = new FileOutputStream(parserfilenameFile);
							ReadableByteChannel src = Channels
									.newChannel(this.getClass().getResourceAsStream("/"+parsername+".xml"));) {

						FileChannel dest = fos.getChannel();

						dest.transferFrom(src, 0, Long.MAX_VALUE);

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				
				
				InputStream is = this.getClass().getResourceAsStream("/" + parsername + ".xml");
				if (is != null) {
					tmp.load_config(new FileInputStream(parserfilename));
				}
			}

			filename = Config.getInstance().getConfigFolder().getAbsolutePath() + File.separator + "History.xml";
			if (new File(filename).canRead()) {
				tmp.load_config(filename);
			}

		} catch (FileNotFoundException e) {
			Logger.getLogger(GlobalOptions.class.getName()).log(Level.SEVERE, "Can't read config.xml file", e);
		}

	}

	public Desktop getUI() {
		return UI;
	}

	public void setUI(Desktop UI) {
		this.UI = UI;
	}

	public HashMap<String, ColumnConfig> getColorlist() {
		return columnlist;
	}

	public HashMap<String, OSConfig> getOSlist() {
		return OSlist;
	}

	public ColumnConfig getColumnConfig(String s) {
		if (columnlist.isEmpty()) {
			return null;
		}
		return columnlist.get(s);
	}

	public Color getDataColor(String s) {
		ColumnConfig tmp = columnlist.get(s);
		if (tmp != null) {
			return tmp.getData_color();
		} else {
			System.err.println("WARN: color not found for tag " + s);
		}
		return null;
	}

	public OSConfig getOSinfo(String s) {
		return OSlist.get(s);
	}

	public boolean isDodebug() {
		return dodebug;
	}

	public void setDodebug(boolean do_debug) {
		dodebug = do_debug;
	}

	public String getCLfilename() {
		return CLfilename;
	}

	public void setCLfilename(String CL_filename) {
		CLfilename = CL_filename;
	}

	public Class getParser(String s) {
		String tmp = s.replaceAll("-", "");
		if (ParserMap.isEmpty()) {
			return null;
		}
		return ParserMap.get(tmp);
	}

	public HashMap<String, HostInfo> getHostInfoList() {
		return HostInfoList;
	}

	public HostInfo getHostInfo(String s) {
		if (HostInfoList.isEmpty()) {
			return null;
		}
		return HostInfoList.get(s);
	}

	public void addHostInfo(HostInfo s) {
		HostInfoList.put(s.getHostname(), s);
		saveHistory();
	}

	public HashMap<String, CnxHistory> getHistoryList() {
		return HistoryList;
	}

	public CnxHistory getHistory(String s) {
		if (HistoryList.isEmpty()) {
			return null;
		}
		return HistoryList.get(s);
	}

	public void addHistory(CnxHistory s) {
		CnxHistory tmp = HistoryList.get(s.getLink());
		if (tmp != null) {
			Iterator<String> ite = s.getCommandList().iterator();
			while (ite.hasNext()) {
				tmp.addCommand(ite.next());
			}
		} else {
			HistoryList.put(s.getLink(), s);
		}
		saveHistory();
	}

	public void saveHistory() {
		File tmpfile = null;
		BufferedWriter tmpfile_out = null;

		if (HistoryList.isEmpty() && HostInfoList.isEmpty()) {
			System.out.println("list is null");
			return;
		}

		try {
			tmpfile = new File(
					Config.getInstance().getConfigFolder().getAbsolutePath() + File.separator + "History.xmltemp");

			if (tmpfile.exists()) {
				tmpfile.delete();
			}
			if (tmpfile.createNewFile() && tmpfile.canWrite()) {
				tmpfile_out = new BufferedWriter(new FileWriter(tmpfile));
			}
			// xml header
			tmpfile_out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<ConfiG>\n");
			tmpfile_out.write("\t<History>\n");
			Iterator<String> ite = HistoryList.keySet().iterator();
			while (ite.hasNext()) {
				CnxHistory tmp = HistoryList.get(ite.next());
				tmpfile_out.write(tmp.save());
			}
			// xml footer
			tmpfile_out.write("\t</History>\n");
			tmpfile_out.write("\t<HostInfo>\n");
			Iterator<String> ite2 = HostInfoList.keySet().iterator();
			while (ite2.hasNext()) {
				HostInfo tmp = HostInfoList.get(ite2.next());
				tmpfile_out.write(tmp.save());
			}
			// xml footer
			tmpfile_out.write("\t</HostInfo>\n");
			tmpfile_out.write("</ConfiG>\n");
			tmpfile_out.flush();
			tmpfile_out.close();
			File oldfile = new File(
					Config.getInstance().getConfigFolder().getAbsolutePath() + File.separator + "History.xml");
			oldfile.delete();
			tmpfile.renameTo(oldfile);

		} catch (IOException ex) {
			Logger.getLogger(GlobalOptions.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

}
