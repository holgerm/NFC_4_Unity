package com.questmill.nfc;

import java.nio.charset.Charset;
import java.util.Arrays;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.primitives.Bytes;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;
import android.util.Log;

public class NFCInfo {

	private String id;
	private String tech;
	private String payload;
	private Tag tag;

	public NFCInfo(Intent intent) {
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		this.id = readID(tag);
		this.tech = readTech(intent);
		this.payload = readPayload(intent);
	}

	/**
	 * @return creates a transferable string containing the payload informations
	 *         of the NFC chip.
	 */
	private String marshall(String content) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPayload() {
		return payload;
	}

	public String getDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	private String readTech(Intent intent) {
		this.tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

		String[] techs = tag.getTechList();
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < techs.length; i++) {
			builder.append(techs[i] + (i + 1 < techs.length ? "," : ""));
		}

		return builder.toString();
	}

	private String readID(Tag tag) {
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

	private String readPayload(Intent intent) {
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
					payload = e.getMessage();
					Log.e(NFCPlugin.class.toString(), e.getMessage());
				}
			}
			return s;
		}
	}

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

	private static final String ERROR_NFC_TAG_ID_NOT_READABLE = "Error Ocurred: NFC Tag ID not readable";

}
