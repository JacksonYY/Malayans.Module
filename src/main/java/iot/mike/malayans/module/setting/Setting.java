package iot.mike.malayans.module.setting;

import java.util.ArrayList;
import java.util.Map;

public class Setting {
	public final static String DataInPort				= "DataInPort";
	public final static String MalayansIP				= "MalayansIP";
	public final static String ModuleID					= "ModuleID";
	public final static String ModuleDescription		= "ModuleDescription";
	public final static String TaskClass				= "TaskClass";
	//客户端注册端口
	public static int int_DataInPort 					= 9400;
	public static String str_MalayansIP					= "localhost";
	public static String str_ModuleID					= "Your Module Name";
	public static String str_ModuleDescription			= "Your Module Description(one line)";
	public static String str_TaskClass					= "iot.mike.malayans.module.client.ExampleTaskClass";
	
	public volatile static ArrayList<Map<String, String>> systemStatus 
								= new ArrayList<Map<String,String>>();
}
