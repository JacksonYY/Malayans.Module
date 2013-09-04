package iot.mike.malayans.module.client;

import iot.mike.malayans.module.setting.Setting;
import iot.mike.malayans.module.setting.SettingManager;
import iot.mike.malayans.rmimanager.interfaces.ModuleInterface;
import iot.mike.malayans.rmimanager.ports.Port;
import iot.mike.malayans.rmimanager.register.ModuleInfo;
import iot.mike.malayans.rmimanager.register.ModuleStatus;
import iot.mike.malayans.rmimanager.register.ModuleStatusEntry;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

public class MalayansClient implements ModuleInterface{
	
	private ModuleInfo 			moduleInfo					= null;
	private Runnable			taskRunnable				= null;
	private Thread 				mainThread					= null;
	private SettingManager 		settingManager				= null;
	private Logger 				logger						= null;
	private Port 				port						= null;
	
	public MalayansClient() {
		settingManager = SettingManager.getInstance();
		settingManager.init();
		
		logger = Logger.getLogger(MalayansClient.class.toString());
		
		if (moduleInfo == null) {
			moduleInfo = new ModuleInfo();
			moduleInfo.setDescription(Setting.str_ModuleDescription);
			moduleInfo.setId(Setting.str_ModuleID);
		}
		
		try {
			Class<?> taskClass = Class.forName(Setting.str_TaskClass);
			taskRunnable = (Runnable)taskClass.newInstance();
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
	}
	
	@SuppressWarnings("resource")
	public final void registModule() {
		try {
			Socket 				clientSocket			= null;
			DataInputStream 	reader					= null;
			DataOutputStream 	writer					= null;	
			
			try {
				logger.info("正在连接..." 
						+ Setting.str_MalayansIP + ":"
						+ Setting.int_DataInPort);
				clientSocket = new Socket(
						Setting.str_MalayansIP, 
						Setting.int_DataInPort);
				reader = new DataInputStream(clientSocket.getInputStream());
				writer = new DataOutputStream(clientSocket.getOutputStream());
			} catch (Exception e) {
				logger.warning("链接创建失败！");
				return;
			}
			
			String jsonmodule = JSON.toJSONString(moduleInfo);
			writer.writeUTF(jsonmodule);
			writer.flush();
			
			String jsonport = reader.readUTF();
			port = JSON.parseObject(jsonport, Port.class);
			this.registRMI();
			
			writer.writeUTF("ok!");
			writer.flush();
		} catch (JSONException e){
			logger.warning("模块注册失败！");
			return;
		} catch (RemoteException e) {
			logger.warning("模块注册失败！");
			return;
		} catch (Exception e) {
			logger.info("模块注册成功！");
			return;
		}
	}


	public final ModuleInfo getModuleInfo() {
		return moduleInfo;
	}


	public final void setModuleInfo(ModuleInfo moduleInfo) {
		this.moduleInfo = moduleInfo;
	}
	
	public void init() throws RemoteException {
		
	}


	public void start() throws RemoteException {
		if (mainThread != null) {
			return;
		}
		mainThread = new Thread(taskRunnable);
		mainThread.start();
	}


	public void stop() throws RemoteException {
		mainThread.interrupt();
		mainThread = null;
	}


	public String getStatus() throws RemoteException {
		Runtime rt 						= 			Runtime.getRuntime();
		Set<ModuleStatusEntry> 
			moduleStatusEntries			= 			new HashSet<ModuleStatusEntry>();
		
		long freeMemory 				= 			rt.freeMemory();
		long totalMemory 				=			rt.totalMemory();
		long maxMemory 					=			rt.maxMemory();
		long availableProcessors 		= 			rt.availableProcessors();
		
		ModuleStatus moduleStatus = new ModuleStatus();
		
		ModuleStatusEntry entryFreeMemory = new ModuleStatusEntry();
		entryFreeMemory.setKey("空闲内存(MB)");
		entryFreeMemory.setValue(String.valueOf(freeMemory / 1024 / 1024));
		
		ModuleStatusEntry entryTotalMemory = new ModuleStatusEntry();
		entryTotalMemory.setKey("总内存内存(MB)");
		entryTotalMemory.setValue(String.valueOf(totalMemory / 1024 / 1024));
		
		ModuleStatusEntry entryMaxMemory = new ModuleStatusEntry();
		entryMaxMemory.setKey("最大内存(MB)");
		entryMaxMemory.setValue(String.valueOf(maxMemory / 1024 / 1024));
		
		ModuleStatusEntry entryAvailableProcessors = new ModuleStatusEntry();
		entryAvailableProcessors.setKey("可用处理器个数(个)");
		entryAvailableProcessors.setValue(String.valueOf(availableProcessors));
		
		moduleStatusEntries.add(entryAvailableProcessors);
		moduleStatusEntries.add(entryMaxMemory);
		moduleStatusEntries.add(entryTotalMemory);
		moduleStatusEntries.add(entryFreeMemory);
		
		moduleStatus.setModuleStatusEntries(moduleStatusEntries);
		
		String jsonStatus = ToolsUtil.getJsonOrder(moduleStatus);
		
		return jsonStatus;
	}


	public String doCommand(String command) throws RemoteException {
		return null;
	}


	public String getDescription() throws RemoteException {
		return null;
	}
	
	private void registRMI() throws RemoteException {
		if (System.getSecurityManager() == null) {
			 System.setSecurityManager(new SecurityManager());
		 }
		 MalayansClient server = new MalayansClient();
         ModuleInterface comp = 
        		 (ModuleInterface)UnicastRemoteObject.exportObject(server, 0);
         System.out.println(port.getPort());
         Registry registry = 
         		LocateRegistry.createRegistry(port.getPort());
         registry.rebind(moduleInfo.getId(), comp);
	}
	
	public static void main(String[] args) throws RemoteException {
		MalayansClient malayansClient = new MalayansClient();
		malayansClient.registModule();
	}
}
