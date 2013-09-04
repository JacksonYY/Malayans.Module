package iot.mike.malayans.client;

import java.rmi.RemoteException;

import iot.mike.malayans.module.client.MalayansClient;
import junit.framework.TestCase;

public class MalayansClientTest extends TestCase{
	
	public void test() {
		MalayansClient client = new MalayansClient();
		try {
			System.out.println(client.getStatus());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
