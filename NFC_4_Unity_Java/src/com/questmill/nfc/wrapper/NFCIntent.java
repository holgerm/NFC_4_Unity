package com.questmill.nfc.wrapper;

import java.nio.charset.Charset;
import java.util.Arrays;

import com.google.common.primitives.Bytes;
import com.questmill.nfc.NFCPlugin;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;
import android.util.Log;

public class NFCIntent implements INFCIntent {

	private Tag tag;
	private Intent intent;

	public NFCIntent(Intent intent) {
		this.tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		this.intent = intent;
	}

	public String readID() {
		String id;
		try {
			byte[] idBytes = tag.getId();
			if (idBytes == null || idBytes.length == 0) {
				id = "";
			} else {
				id = "";
				for (int i = 0; i < idBytes.length; i++) {
					id += new Integer(idBytes[i]).toString();
				}
			}
			return id;
		} catch (Exception exc) {
			Log.e(NFCPlugin.class.toString(), exc.getMessage());
			return ERROR_NFC_TAG_ID_NOT_READABLE;
		}
	}

	public String readPayload() {
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
					// TODO set invalid
					Log.e(NFCPlugin.class.toString(), e.getMessage());
				}
			}
			return s;
		}
	}

	public String readTech() {
		this.tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

		String[] techs = tag.getTechList();
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < techs.length; i++) {
			builder.append(techs[i] + (i + 1 < techs.length ? "," : ""));
		}

		return builder.toString();
	}

}
