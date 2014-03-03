package edu.buffalo.cse.cse486586.groupmessenger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Resources {
	static final String REMOTE_PORT0 = "11108";
	static final String REMOTE_PORT1 = "11112";
	static final String REMOTE_PORT2 = "11116";
	static final String REMOTE_PORT3 = "11120";
	static final String REMOTE_PORT4 = "11124";

	static final String[] remotePorts = { REMOTE_PORT0, REMOTE_PORT1,
			REMOTE_PORT2, REMOTE_PORT3, REMOTE_PORT4 };

	static int messageCount = 0;

	public static int getMessageCount() {
		return messageCount;
	}

	public static void setMessageCount(int messageCount) {
		Resources.messageCount = messageCount;
	}

	public static int getProviderCount() {
		return providerCount;
	}

	public static void setProviderCount(int providerCount) {
		Resources.providerCount = providerCount;
	}

	static String myPort;

	static int localCounter;
	static int globalCounter;

	static int providerCount = 0;

	public static int getLocalCounter() {
		return localCounter;
	}

	public static void setLocalCounter(int localCounter) {
		Resources.localCounter = localCounter;
	}

	public static int getGlobalCounter() {
		return globalCounter;
	}

	public static void setGlobalCounter(int globalCounter) {
		Resources.globalCounter = globalCounter;
	}

	static Map<Integer, Message> holdBackMap = new HashMap<Integer, Message>();

	public static String getMyPort() {
		return myPort;
	}

	public static void setMyPort(String myPort) {
		Resources.myPort = myPort;
	}
}
