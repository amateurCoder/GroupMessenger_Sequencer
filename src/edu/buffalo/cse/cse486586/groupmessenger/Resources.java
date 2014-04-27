package edu.buffalo.cse.cse486586.groupmessenger;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to manage all the static variables used in
 * GroupMessengerActivity.java
 * 
 * 
 * @author ankitsul
 * 
 */
public class Resources {
	static final String REMOTE_PORT0 = "11108";
	static final String REMOTE_PORT1 = "11112";
	static final String REMOTE_PORT2 = "11116";
	static final String REMOTE_PORT3 = "11120";
	static final String REMOTE_PORT4 = "11124";

	static String[] sRemotePorts = { REMOTE_PORT0, REMOTE_PORT1, REMOTE_PORT2,
			REMOTE_PORT3, REMOTE_PORT4 };

	static int sMessageCount = 0;

	static String sMyPort;

	static int sLocalCounter;
	static int sGlobalCounter;

	static int sProviderCount = 0;

	static Map<Integer, Message> sHoldBackMap = new HashMap<Integer, Message>();

	public static int getMessageCount() {
		return sMessageCount;
	}

	public static void setMessageCount(int messageCount) {
		Resources.sMessageCount = messageCount;
	}

	public static int getProviderCount() {
		return sProviderCount;
	}

	public static void setProviderCount(int providerCount) {
		Resources.sProviderCount = providerCount;
	}

	public static int getLocalCounter() {
		return sLocalCounter;
	}

	public static void setLocalCounter(int localCounter) {
		Resources.sLocalCounter = localCounter;
	}

	public static int getGlobalCounter() {
		return sGlobalCounter;
	}

	public static void setGlobalCounter(int globalCounter) {
		Resources.sGlobalCounter = globalCounter;
	}

	public static String getMyPort() {
		return sMyPort;
	}

	public static void setMyPort(String myPort) {
		Resources.sMyPort = myPort;
	}
}
