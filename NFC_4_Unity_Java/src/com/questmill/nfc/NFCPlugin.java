package com.questmill.nfc;

import java.nio.charset.Charset;
import java.util.Arrays;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcF;
import android.nfc.tech.TagTechnology;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.primitives.Bytes;
import com.unity3d.player.UnityPlayerActivity;

public class NFCPlugin extends UnityPlayerActivity {

	public static final String MIME_TEXT_PLAIN = "text/plain";

	@SuppressWarnings("rawtypes")
	private static final BiMap URI_PREFIX_MAP = ImmutableBiMap.builder()
			.put((byte) 0x00, "").put((byte) 0x01, "http://www.")
			.put((byte) 0x02, "https://www.").put((byte) 0x03, "http://")
			.put((byte) 0x04, "https://").put((byte) 0x05, "tel:")
			.put((byte) 0x06, "mailto:")
			.put((byte) 0x07, "ftp://anonymous:anonymous@")
			.put((byte) 0x08, "ftp://ftp.").put((byte) 0x09, "ftps://")
			.put((byte) 0x0A, "sftp://").put((byte) 0x0B, "smb://")
			.put((byte) 0x0C, "nfs://").put((byte) 0x0D, "ftp://")
			.put((byte) 0x0E, "dav://").put((byte) 0x0F, "news:")
			.put((byte) 0x10, "telnet://").put((byte) 0x11, "imap:")
			.put((byte) 0x12, "rtsp://").put((byte) 0x13, "urn:")
			.put((byte) 0x14, "pop:").put((byte) 0x15, "sip:")
			.put((byte) 0x16, "sips:").put((byte) 0x17, "tftp:")
			.put((byte) 0x18, "btspp://").put((byte) 0x19, "btl2cap://")
			.put((byte) 0x1A, "btgoep://").put((byte) 0x1B, "tcpobex://")
			.put((byte) 0x1C, "irdaobex://").put((byte) 0x1D, "file://")
			.put((byte) 0x1E, "urn:epc:id:").put((byte) 0x1F, "urn:epc:tag:")
			.put((byte) 0x20, "urn:epc:pat:").put((byte) 0x21, "urn:epc:raw:")
			.put((byte) 0x22, "urn:epc:").put((byte) 0x23, "urn:nfc:").build();

	private static final String[] MIFARE_CLASSIC_TYPES = { "TYPE_UNKNOWN",
			"TYPE_CLASSIC", "TYPE_PLUS", "TYPE_PRO" };
	private static final String[] MIFARE_UNLTRALIGHT_TYPES = { "TYPE_UNKNOWN",
			"TYPE_ULTRALIGHT", "TYPE_ULTRALIGHT_C" };
	private static final String[] MIFARE_UNLTRALIGHT_SIZES = { "unknown", "48",
			"144" };

	private NfcAdapter mNfcAdapter;

	private PendingIntent pendingIntent;
	private static NFCWriter writer;

	IntentFilter[] mIntentFilter;

	String[][] techListsArray;

	private static String value = "";
	private static String info = "";
	private static Tag tag;
	private static TagTechnology currentTag = null;

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

		Log.i(NFCPlugin.class.toString(), "onNewIntent(" + intent + ")");
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		Log.i(NFCPlugin.class.toString(), "handleIntent(" + intent + ")");
		tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		info = readTagTech(intent);
		value = read(intent);
		// Log.i(NFCPlugin.class.toString(),
		// "read: " + value + "; tag = " + tag.toString());
	}

	private String readTagTech(Intent intent) {
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

		for (String tech : tag.getTechList()) {
			// Log.i(NFCPlugin.class.toString(), "Techlist contains: <" + tech
			// + ">");
			if ("android.nfc.tech.MifareClassic".equals(tech)) {
				currentTag = MifareClassic.get(tag);
			}
			if ("android.nfc.tech.MifareUltralight".equals(tech)) {
				currentTag = MifareUltralight.get(tag);
			}
			writer = NFCWriter.create(currentTag);
			return logDetails(currentTag);
		}
		return "Unknown type of NFC Tag found.";
	}

	private String logDetails(TagTechnology tag) {
		String logString = "";
		if (MifareClassic.class.isAssignableFrom(tag.getClass())) {
			logString += logDetails((MifareClassic) tag);
			return logString;
		}
		if (MifareUltralight.class.isAssignableFrom(tag.getClass())) {
			logString += logDetails((MifareUltralight) tag);
			return logString;
		}
		// DEFAULT:
		logString = "Unknown type of NFC Tag found.";
		// Log.i(NFCPlugin.class.toString(), logString);
		return logString;
	}

	private String logDetails(MifareClassic mifareClassic) {
		String logString = "MifareClassic with size: "
				+ mifareClassic.getSize();
		// Log.i(NFCPlugin.class.toString(), logString);
		int typeIndex = mifareClassic.getType() + 1;
		if (typeIndex < 0 || typeIndex >= MIFARE_CLASSIC_TYPES.length)
			typeIndex = 0;
		String logString2 = "MifareClassic of type: "
				+ MIFARE_CLASSIC_TYPES[typeIndex];
		// Log.i(NFCPlugin.class.toString(), logString2);
		return logString + "\n" + logString2;
	}

	private String logDetails(MifareUltralight mifareUltralight) {
		int typeIndex = mifareUltralight.getType() + 1;
		if (typeIndex < 0 || typeIndex >= MIFARE_UNLTRALIGHT_TYPES.length)
			typeIndex = 0;
		String logString = "MifareUltralight with size: "
				+ MIFARE_UNLTRALIGHT_SIZES[typeIndex];
		// Log.i(NFCPlugin.class.toString(), logString);
		String logString2 = "MifareUltralight of type: "
				+ MIFARE_UNLTRALIGHT_TYPES[typeIndex];
		// Log.i(NFCPlugin.class.toString(), logString2);
		return logString + "\n" + logString2;
	}

	private String read(Intent intent) {
		if (tag == null) {
			return "ERROR: NO TAG";
		} else {
			// Parses through all NDEF messages and their records and picks text
			// and uri type.
			Parcelable[] data = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			String s = "";
			if (data != null) {
				try {
					for (int i = 0; i < data.length; i++) {
						NdefRecord[] recs = ((NdefMessage) data[i])
								.getRecords();
						for (int j = 0; j < recs.length; j++) {
							if (recs[j].getTnf() == NdefRecord.TNF_WELL_KNOWN
									&& Arrays.equals(recs[j].getType(),
											NdefRecord.RTD_TEXT)) {
								/*
								 * See NFC forum specification for
								 * "Text Record Type Definition" at 3.2.1
								 * 
								 * http://www.nfc-forum.org/specs/
								 * 
								 * bit_7 defines encoding bit_6 reserved for
								 * future use, must be 0 bit_5..0 length of IANA
								 * language code
								 */
								byte[] payload = recs[j].getPayload();
								String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8"
										: "UTF-16";
								int langCodeLen = payload[0] & 0077;
								s += new String(payload, langCodeLen + 1,
										payload.length - langCodeLen - 1,
										textEncoding);
							} else if (recs[j].getTnf() == NdefRecord.TNF_WELL_KNOWN
									&& Arrays.equals(recs[j].getType(),
											NdefRecord.RTD_URI)) {
								/*
								 * See NFC forum specification for
								 * "URI Record Type Definition" at 3.2.2
								 * 
								 * http://www.nfc-forum.org/specs/
								 * 
								 * payload[0] contains the URI Identifier Code
								 * payload[1]...payload[payload.length - 1]
								 * contains the rest of the URI.
								 */
								byte[] payload = recs[j].getPayload();
								String prefix = (String) URI_PREFIX_MAP
										.get(payload[0]);
								byte[] fullUri = Bytes.concat(prefix
										.getBytes(Charset.forName("UTF-8")),
										Arrays.copyOfRange(payload, 1,
												payload.length));
								s += new String(fullUri,
										Charset.forName("UTF-8"));
							}
						}
					}
				} catch (Exception e) {
					value = e.getMessage();
					Log.e(NFCPlugin.class.toString(), e.getMessage());
				}
			}
			return s;
		}
	}

	/**
	 * Accessible from Unity Plugin.
	 * 
	 * @return The content of the last scanned NFC tag.
	 */
	public static String getValue() {
		return "Info:\n" + info + "\n\nValue: " + value;
	}

	/**
	 * Accessible from Unity Plugin.
	 * 
	 * @return The feedback of the last call to the writer.
	 */
	public static String writeTestContent() {
		Log.i(NFCPlugin.class.toString(), "We will write test content.");
		if (writer != null) {
			writer.write("Test text");
		} else {
			Log.i(NFCPlugin.class.toString(), "WRITER NOT INITIALIZED!");
		}

		return "OK";
	}
}
