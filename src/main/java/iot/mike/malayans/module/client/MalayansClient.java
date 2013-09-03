package iot.mike.malayans.module.client;

import iot.mike.malayans.module.setting.Setting;
import iot.mike.malayans.module.setting.SettingManager;
import iot.mike.malayans.rmimanager.interfaces.ModuleInterface;
import iot.mike.malayans.rmimanager.ports.Port;
import iot.mike.malayans.rmimanager.register.ModuleInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

public class MalayansClient implements ModuleInterface{
	
	private ModuleInfo 			moduleInfo					= null;
	private Thread 				mainThread					= null;
	private SettingManager 		settingManager				= null;
	private Logger 				logger						= null;
	private Port 				port						= null;
	private Runnable			taskRunnable				= null;
	
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
				clientSocket = new Socket(
						Setting.MalayansIP, 
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
		return null;
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
		malayansClient.start();
		malayansClient.stop();
		malayansClient.start();
	}
}
