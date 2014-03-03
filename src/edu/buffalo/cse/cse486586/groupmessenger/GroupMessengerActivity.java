package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 * 
 * @author stevko
 * 
 */
public class GroupMessengerActivity extends Activity {

	static final String TAG = GroupMessengerActivity.class.getSimpleName();
	static final String SEQUENCER_PORT = "11108";

	static final int SERVER_PORT = 10000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_messenger);

		/*
		 * TODO: Use the TextView to display your messages. Though there is no
		 * grading component on how you display the messages, if you implement
		 * it, it'll make your debugging easier.
		 */
		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setMovementMethod(new ScrollingMovementMethod());

		/*
		 * Registers OnPTestClickListener for "button1" in the layout, which is
		 * the "PTest" button. OnPTestClickListener demonstrates how to access a
		 * ContentProvider.
		 */
		findViewById(R.id.button1).setOnClickListener(
				new OnPTestClickListener(tv, getContentResolver()));

		/*
		 * TODO: You need to register and implement an OnClickListener for the
		 * "Send" button. In your implementation you need to get the message
		 * from the input box (EditText) and send it to other AVDs in a
		 * total-causal order.
		 */

		TelephonyManager tel = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		String portStr = tel.getLine1Number().substring(
				tel.getLine1Number().length() - 4);

		final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));

		Resources.myPort = myPort;

		Resources.setMessageCount(0);
		Resources.setProviderCount(0);

		Resources.setLocalCounter(0);
		if (Resources.myPort.equals(SEQUENCER_PORT)) {
			Resources.setGlobalCounter(-1);
		}

		try {
			ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
			new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
					serverSocket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		findViewById(R.id.button4).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Create Telephony service and Server/Client Async threads
				Log.d(TAG, "####Button clicked");

				final EditText editText = (EditText) findViewById(R.id.editText1);
				String msg = editText.getText().toString();
				editText.setText("");

				new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
						"", msg, Resources.myPort,
						MessageType.NEW_MESSAGE.toString());

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
		return true;
	}

	private class ServerTask extends AsyncTask<ServerSocket, String, Void> {

		private static final String VALUE_FIELD = "value";
		private static final String KEY_FIELD = "key";

		@Override
		protected Void doInBackground(ServerSocket... sockets) {
			ServerSocket serverSocket = sockets[0];

			try {
				readMessage(serverSocket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		private void readMessage(ServerSocket serverSocket) throws IOException,
				ClassNotFoundException {
			Message message = null;
			while (true) {
				Socket socket = serverSocket.accept();
				ObjectInputStream objectInputStream = new ObjectInputStream(
						socket.getInputStream());
				message = (Message) objectInputStream.readObject();
				objectInputStream.close();

				if (null != message) {
					// At sequencer
					if (message.getMessageType()
							.equals(MessageType.NEW_MESSAGE)) {
						Log.d(TAG, "####Message Received:" + message.getMsg()
								+ ";Message Id:" + message.getMessageId()
								+ ";Message Type:" + message.getMessageType()
								+ ";From:" + message.getPortNumber()
								+ ";My port:" + Resources.myPort);

						// Save it in hold-back queue
						// Resources.holdBackMap.put(arg0, arg1)
						Resources.globalCounter++;
						int sequence = Resources.globalCounter;
						// if (Resources.myPort.equals(SEQUENCER_PORT)) {
						new ClientTask().executeOnExecutor(
								AsyncTask.SERIAL_EXECUTOR,
								message.getMessageId(), message.getMsg(),
								Resources.myPort, MessageType.ORDER.toString(),
								Integer.toString(sequence));
						// }

					} else if (message.getMessageType().equals(
							MessageType.ORDER)) {
						int S = message.getSequenceNumber();

						if (Resources.localCounter == S) {
							publishProgress(message.getMsg());
							Resources.localCounter = Resources.localCounter + 1;
							while (isMoreMessagePresent()) {
								publishProgress(getMessageFromMap(Resources.localCounter));
								Resources.localCounter++;
							}
						} else {
							// Keep in hold back map
							Resources.holdBackMap.put(S, message);
						}

					}
				}
			}

		}

		private boolean isMoreMessagePresent() {
			if (Resources.holdBackMap.containsKey(Resources.localCounter)) {
				return true;
			}
			return false;
		}

		private String getMessageFromMap(int sequenceNumber) {
			return Resources.holdBackMap.get(sequenceNumber).getMsg();
		}

		protected void onProgressUpdate(String... strings) {

			String msg = strings[0].trim();
			TextView remoteTextView = (TextView) findViewById(R.id.textView1);

			// Send to content provider
			ContentValues entry = new ContentValues();
			entry.put(KEY_FIELD, Resources.providerCount++);
			entry.put(VALUE_FIELD, msg);
			Log.d(TAG, "####Message:" + (Resources.providerCount - 1) + ":"
					+ msg);
			remoteTextView.append(Resources.providerCount - 1 + ":" + msg
					+ "\t\n");
			getContentResolver()
					.insert(buildUri("content",
							"edu.buffalo.cse.cse486586.groupmessenger.provider"),
							entry);
		}

		private Uri buildUri(String scheme, String authority) {
			Uri.Builder uriBuilder = new Uri.Builder();
			uriBuilder.authority(authority);
			uriBuilder.scheme(scheme);
			return uriBuilder.build();
		}

	}

	private class ClientTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... msgs) {
			Message message = new Message();
			String msgId = msgs[0];
			String msgToSend = msgs[1];
			String myPort = msgs[2];
			Socket socket = null;
			ObjectOutputStream objectOutputStream;
			message.setMsg(msgToSend);
			message.setMessageId(msgId);
			message.setPortNumber(myPort);
			if (msgs[3] == MessageType.NEW_MESSAGE.toString()) {
				message.setMessageType(MessageType.NEW_MESSAGE);
				Resources.messageCount++;
				msgId = myPort + Resources.messageCount;
				try {
					socket = new Socket(InetAddress.getByAddress(new byte[] {
							10, 0, 2, 2 }), Integer.parseInt(SEQUENCER_PORT));

					Log.d(TAG, "####Message Sent:" + message.getMsg()
							+ ";Message Id:" + message.getMessageId()
							+ ";Message Type:" + message.getMessageType()
							+ ";My port:" + message.getPortNumber()
							+ ";To port:" + SEQUENCER_PORT + ";Sequence:"
							+ message.getSequenceNumber());

					objectOutputStream = new ObjectOutputStream(
							socket.getOutputStream());
					objectOutputStream.writeObject(message);
					objectOutputStream.close();
					socket.close();
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else if (msgs[3] == MessageType.ORDER.toString()) {
				message.setMessageType(MessageType.ORDER);
				message.setSequenceNumber(Integer.parseInt(msgs[4]));

				for (int i = 0; i < Resources.remotePorts.length; i++) {
					try {
						socket = new Socket(
								InetAddress.getByAddress(new byte[] { 10, 0, 2,
										2 }),
								Integer.parseInt(Resources.remotePorts[i]));

						Log.d(TAG, "####Message Sent:" + message.getMsg()
								+ ";Message Id:" + message.getMessageId()
								+ ";Message Type:" + message.getMessageType()
								+ ";My port:" + message.getPortNumber()
								+ ";To port:" + Resources.remotePorts[i]
								+ ";Sequence:" + message.getSequenceNumber());

						objectOutputStream = new ObjectOutputStream(
								socket.getOutputStream());
						objectOutputStream.writeObject(message);
						objectOutputStream.close();
						socket.close();
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
			return null;

		}

	}

}
