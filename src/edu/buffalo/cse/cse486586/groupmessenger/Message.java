package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.Serializable;
import java.util.Map;

/**
 * POJO class used to pass message in input/output streams
 * 
 * @author ankitsul
 * 
 */

public class Message implements Serializable {

	private static final long serialVersionUID = 1L;
	private String mMsg;
	private String mPortNumber;
	private int mSequenceNumber;
	private MessageType mMessageType;
	private String mMessageId;

	private Map<String, Integer> mMessageCounterMap;

	public Map<String, Integer> getMessageCounterMap() {
		return mMessageCounterMap;
	}

	public void setMessageCounterMap(Map<String, Integer> messageCounterMap) {
		this.mMessageCounterMap = messageCounterMap;
	}

	public String getMsg() {
		return mMsg;
	}

	public void setMsg(String msg) {
		this.mMsg = msg;
	}

	public String getPortNumber() {
		return mPortNumber;
	}

	public void setPortNumber(String portNumber) {
		this.mPortNumber = portNumber;
	}

	public int getSequenceNumber() {
		return mSequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.mSequenceNumber = sequenceNumber;
	}

	public MessageType getMessageType() {
		return mMessageType;
	}

	public void setMessageType(MessageType messageType) {
		this.mMessageType = messageType;
	}

	public String getMessageId() {
		return mMessageId;
	}

	public void setMessageId(String messageId) {
		this.mMessageId = messageId;
	}

}
