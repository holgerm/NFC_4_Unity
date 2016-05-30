package com.questmill.nfc;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.util.Log;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

public class NFCPlugin extends UnityPlayerActivity {

	/**
	 * The name of the game object in the Unity scene that receives calls from
	 * us.
	 */
	private static final String RECEIVER_GAME_OBJECT_NAME = "NFCReceiver";

	/**
	 * The name of the value receiver method on the game object.
	 */
	private static final String RECEIVER_METHOD_NAME_PAYLOAD = "NFCReadPayload";

	/**
	 * The name of the ID receiver method on the game object.
	 */
	private static final String RECEIVER_METHOD_NAME_DETAILS = "NFCReadDetails";

	private static boolean simpleMode = true;

	public static void setSimpleMode(boolean simpleMode) {
		simpleMode = simpleMode;
	}

	private void sendMessage(String messageType, String message) {
		UnityPlayer.UnitySendMessage(RECEIVER_GAME_OBJECT_NAME, messageType,
				message);
	}

	public static final String MIME_TEXT_PLAIN = "text/plain";

	private NfcAdapter mNfcAdapter;

	private PendingIntent pendingIntent;
	private static NFCWriter writer;

	IntentFilter[] mIntentFilter;

	String[][] techListsArray;

	private static Tag tag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(NFCPlugin.class.toString(), "onCreate()");

		// Foreground Dispatch: 1. Creates a PendingIntent object so the Android
		// system can populate it with the details of the tag when it is
		// scanned.
		pendingIntent = PendingIntent.getActivity(NFCPlugin.this, 0,
				new Intent(NFCPlugin.this, getClass())
						.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		// Foreground Dispatch: 2. Declare intent filters to handle the intents
		// that you want to intercept
		mIntentFilter = new IntentFilter[] { new IntentFilter(
				NfcAdapter.ACTION_TAG_DISCOVERED) };
		// Foreground Dispatch: 3. Set up an array of tag technologies that your
		// application wants to handle.
		techListsArray = new String[][] { new String[] { NfcF.class.getName() } };

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mNfcAdapter == null) {
			Log.e(NFCPlugin.class.toString(),
					"This device doesn't support NFC.");
			finish();
			return;
		}
		if (!mNfcAdapter.isEnabled()) {
			Log.e(NFCPlugin.class.toString(), "NFC is disabled.");
		} else {
			Log.i(NFCPlugin.class.toString(), "NFC reader initialized.");
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i(NFCPlugin.class.toString(), "onResume()");
		// Enables the foreground dispatch when the activity regains focus.
		mNfcAdapter.enableForegroundDispatch(this, pendingIntent,
				mIntentFilter, techListsArray);
		if (tag == null) {
			Log.i(NFCPlugin.class.toString(), "tag = null");
		} else {
			Log.i(NFCPlugin.class.toString(), "tag = " + tag.toString());
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i(NFCPlugin.class.toString(), "onPause()");
		// Disables the foreground dispatch when the activity loses focus.
		mNfcAdapter.disableForegroundDispatch(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(NFCPlugin.class.toString(), "onDestroy()");
		mNfcAdapter = null;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		// TODO extend NFCWriter to NFCAccess and initialize it here!

		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {

		if (!intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
			// Not a compatible NFC Tag. Ignore it.
			return;
		}

		NFCInfo info = new NFCInfo(intent);

		if (simpleMode)
			sendMessage(RECEIVER_METHOD_NAME_PAYLOAD, info.getPayload());
		else
			sendMessage(RECEIVER_METHOD_NAME_DETAILS, info.getDetails());
	}

	/**
	 * Accessible from Unity Plugin.
	 * 
	 * @return The feedback of the last call to the writer.
	 */
	public static String writeTestContent() {
		if (writer != null) {
			Log.i(NFCPlugin.class.toString(),
					"Writer ok. We will write test content.");
			return writer.write("Test text");
		} else {
			Log.i(NFCPlugin.class.toString(), "WRITER NOT INITIALIZED!");
			return "WRITER NOT INITIALIZED!";
		}

	}
}
