package com.questmill.nfc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.questmill.nfc.wrapper.NFCIntent;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

public class NFCPlugin extends UnityPlayerActivity {

	/**
	 * The name of the game object in the Unity scene that receives calls from
	 * us.
	 */
	private static final String RECEIVER_GAME_OBJECT_NAME = "NFC_Connector";

	/**
	 * The name of the value receiver method on the game object.
	 */
	private static final String RECEIVER_METHOD_NAME_PAYLOAD = "NFCReadPayload";

	/**
	 * The name of the ID receiver method on the game object.
	 */
	private static final String RECEIVER_METHOD_NAME_DETAILS = "NFCReadDetails";

	private static boolean simpleMode = false;

	public static void setSimpleMode(boolean simpleMode) {
		NFCPlugin.simpleMode = simpleMode;
	}

	private void sendMessage(String messageType, String message) {
		UnityPlayer.UnitySendMessage(RECEIVER_GAME_OBJECT_NAME, messageType,
				message);
	}

	public static final String MIME_TEXT_PLAIN = "text/plain";

	private NfcAdapter mNfcAdapter;

	private PendingIntent pendingIntent;

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

		tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

		NFCInfo info = new NFCInfo(new NFCIntent(intent));

		if (simpleMode)
			sendMessage(RECEIVER_METHOD_NAME_PAYLOAD, info.getPayload());
		else
			sendMessage(RECEIVER_METHOD_NAME_DETAILS, info.marshall());
	}

	private static NdefRecord createRecord(String text)
			throws UnsupportedEncodingException {

		String lang = "en";
		byte[] textBytes = text.getBytes();
		byte[] langBytes = lang.getBytes("US-ASCII");
		int langLength = langBytes.length;
		int textLength = textBytes.length;

		byte[] payload = new byte[1 + langLength + textLength];
		payload[0] = (byte) langLength;

		// copy langbytes and textbytes into payload
		System.arraycopy(langBytes, 0, payload, 1, langLength);
		System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

		NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
				NdefRecord.RTD_TEXT, new byte[0], payload);
		return recordNFC;
	}

	public static void write(String text) {

		String wrote = "[[nothing written]]";

		Log.i("NFC UNITY", "Java side received call to write: " + text);

		try {
			NdefRecord[] records = { createRecord(text) };
			NdefMessage message = new NdefMessage(records);
			if (tag == null) {
				Log.e("NFC UNITY", "No Tag scanned recently.");
				return;
			}
			Ndef ndef = Ndef.get(tag);
			ndef.connect();
			ndef.writeNdefMessage(message);
			ndef.close();

			wrote = message.toString();
		} catch (IOException exc) {
			Log.e("NFC UNITY", "IOExc: " + exc.getMessage());
		} catch (FormatException exc) {
			Log.e("NFC UNITY", "FormatExc: " + exc.getMessage());

		}
		Log.i("NFC UNITY", "Java side wrote to tag: " + wrote);
	}

}
