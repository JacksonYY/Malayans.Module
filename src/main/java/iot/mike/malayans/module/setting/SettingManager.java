package iot.mike.malayans.module.setting;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;
import java.util.logging.Logger;

public class SettingManager {
	private File settingFile									= null;//配置的文件
	private Properties properties								= null;//这是系统配置
	private Logger logger										= null;//日志输出器
	private boolean isInit 										= false;//是否初始化
	
	private static class SettingManagerHolder{
		public static SettingManager settingManager = new SettingManager();
	}
	
	/**
	 * 进行初始化
	 */
	public void init(){
		if (isInit) {
			return;
		}
		//-------------------------------
		try {
			if (settingFile.exists()) {
				logger.info("File Exsit....Reading");
				properties.load(new FileReader(settingFile));
				if (checkProperty(properties)) {
					logger.info("Property read complete!");
					this.updateSetting(properties);
				}else {
					properties = null;	//读取失败删除
					logger.warning("Property is wrong!");
					throw new Exception();
				}
			}else {
				logger.info("File NULL....Creating");
				//输出默认配置
				
				this.setProperties(properties);
				
				properties.store(new FileWriter(settingFile), "");
				logger.info("Default setting...\n"
						+ "if you want to change the setting, \n"
						+ "please edit the "
						+ "system.properties file");
			}
			isInit = true;
		} catch (Exception e) {
			// TODO: handle exception
			logger.warning("SetingFile can't be read!\n"
					+ "System exit!");
			System.exit(0);
		}
	}
	
	private SettingManager() {
		properties = new Properties();
		settingFile = new File("system.properties");
		logger = Logger.getLogger(SettingManager.class.toString());
	}
	
	public static SettingManager getInstance() {
		return SettingManagerHolder.settingManager;
	}
	
	/**
	 * 检查读取的设置是否正确
	 * @param properties 输入的设置
	 * @return boolean
	 */
	private boolean checkProperty(Properties properties) {
		if (properties != null 
				&& properties.get(Setting.DataInPort) != null
				&& properties.get(Setting.MalayansIP) != null
				&& properties.get(Setting.ModuleDescription) != null
				&& properties.get(Setting.ModuleID) != null) {
			return true;
		}
		return false;
	}
	
	/**
	 * 返回系统设置
	 * @return property
	 */
	public Properties getProperties() {
		if (checkProperty(properties)) {
			return (Properties) properties.clone();
		}
		return null;
	}
	
	private void setProperties(Properties properties) {
		properties.put(Setting.DataInPort, 
				String.valueOf(Setting.int_DataInPort));
		
		properties.put(Setting.MalayansIP, 
				Setting.str_MalayansIP);
		
		properties.put(Setting.ModuleID, 
				Setting.str_ModuleID);
		
		properties.put(Setting.ModuleDescription, 
				Setting.str_ModuleDescription);
		
		properties.put(Setting.TaskClass, 
				Setting.str_TaskClass);
	}
	
	private void updateSetting(Properties properties) {
		Setting.str_MalayansIP = 
				properties.getProperty(Setting.MalayansIP);
		Setting.str_ModuleDescription = 
				properties.getProperty(Setting.ModuleDescription);
		Setting.str_ModuleID = 
				properties.getProperty(Setting.ModuleID);
		Setting.int_DataInPort = 
				Integer.valueOf(properties.getProperty(Setting.DataInPort));
		Setting.str_TaskClass = 
				properties.getProperty(Setting.TaskClass);
	}
}
