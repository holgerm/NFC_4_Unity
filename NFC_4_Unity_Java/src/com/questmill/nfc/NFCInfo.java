package com.questmill.nfc;

import com.questmill.nfc.wrapper.INFCIntent;

public class NFCInfo {

	private static final char DELIMITER = ',';
	private static final char KEY_VALUE_DELIMITER = ':';
	private static final char KEY_ID = 'i';
	private static final char KEY_PAYLOAD = 'p';
	private static final char KEY_TECH = 't';

	private String id;
	private String tech;
	private String payload;

	// private Tag tag;

	public NFCInfo(INFCIntent nfcIntent) {
		this.id = nfcIntent.readID();
		this.tech = nfcIntent.readTech();
		this.payload = nfcIntent.readPayload();
	}

	public String getPayload() {
		return payload;
	}

	/**
	 * In order to be transmitted between Java and C# the data is mashalled
	 * using this format:
	 * 
	 * key:value,key:value,key:value etc.
	 * 
	 * Where key is a char that can be i for id, p for payload, t for techlist.
	 * And value is the original content from the NFC chip, except for
	 * occurrences of the DELIMITER character which are doubled. I.e. every
	 * occurrence of ',' in the content has been replaced by ',,'. Therefore the
	 * unmarshall method searches for double DELIMITERs and replaces them by one
	 * single DELIMITER again. Occurrences of single DELIMITER in the marshalled
	 * content will in contrast be interpreted as the end of the currently
	 * parsed content and ignored but trigger the switch to searching for the
	 * next key.
	 * 
	 * @return creates a transferable string containing the payload informations
	 *         of the NFC chip.
	 */
	public String marshall() {
		StringBuilder details = new StringBuilder();

		// id:
		if (id != null && !id.isEmpty()) {
			details.append("" + KEY_ID + KEY_VALUE_DELIMITER + maskCommas(id));
		}
		// payload:
		if (payload != null && !payload.isEmpty()) {
			if (details.length() > 0)
				details.append(DELIMITER);
			details.append("" + KEY_PAYLOAD + KEY_VALUE_DELIMITER
					+ maskCommas(payload));
		}
		// tech:
		if (tech != null && !tech.isEmpty()) {
			details.append(DELIMITER);
			details.append("" + KEY_TECH + KEY_VALUE_DELIMITER + maskCommas(tech));
		}

		return details.toString();
	}

	private String maskCommas(String originalContent) {
		if (originalContent == null || originalContent.isEmpty())
			return "";
		return originalContent.replaceAll(",", ",,");
	}

	// private String readTech(Intent intent) {
	// this.tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	//
	// String[] techs = tag.getTechList();
	// StringBuilder builder = new StringBuilder();
	//
	// for (int i = 0; i < techs.length; i++) {
	// builder.append(techs[i] + (i + 1 < techs.length ? "," : ""));
	// }
	//
	// return builder.toString();
	// }
	//
	// private String readID(Tag tag) {
	// String id;
	// try {
	// byte[] idBytes = tag.getId();
	// if (idBytes == null || idBytes.length == 0) {
	// id = "";
	// } else {
	// id = "";
	// for (int i = 0; i < idBytes.length; i++) {
	// id += new Integer(idBytes[i]).toString();
	// }
	// }
	// return id;
	// } catch (Exception exc) {
	// Log.e(NFCPlugin.class.toString(), exc.getMessage());
	// return INFCIntent.ERROR_NFC_TAG_ID_NOT_READABLE;
	// }
	// }

	// private String readPayload(Intent intent) {
	// if (tag == null) {
	// return "ERROR: NO TAG";
	// } else {
	// // Parses through all NDEF messages and their records and picks text
	// // and uri type.
	// Parcelable[] data = intent
	// .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
	// String s = "";
	// if (data != null) {
	// try {
	// for (int i = 0; i < data.length; i++) {
	// NdefRecord[] recs = ((NdefMessage) data[i])
	// .getRecords();
	// for (int j = 0; j < recs.length; j++) {
	// if (recs[j].getTnf() == NdefRecord.TNF_WELL_KNOWN
	// && Arrays.equals(recs[j].getType(),
	// NdefRecord.RTD_TEXT)) {
	// /*
	// * See NFC forum specification for
	// * "Text Record Type Definition" at 3.2.1
	// *
	// * http://www.nfc-forum.org/specs/
	// *
	// * bit_7 defines encoding bit_6 reserved for
	// * future use, must be 0 bit_5..0 length of IANA
	// * language code
	// */
	// byte[] payload = recs[j].getPayload();
	// String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8"
	// : "UTF-16";
	// int langCodeLen = payload[0] & 0077;
	// s += new String(payload, langCodeLen + 1,
	// payload.length - langCodeLen - 1,
	// textEncoding);
	// } else if (recs[j].getTnf() == NdefRecord.TNF_WELL_KNOWN
	// && Arrays.equals(recs[j].getType(),
	// NdefRecord.RTD_URI)) {
	// /*
	// * See NFC forum specification for
	// * "URI Record Type Definition" at 3.2.2
	// *
	// * http://www.nfc-forum.org/specs/
	// *
	// * payload[0] contains the URI Identifier Code
	// * payload[1]...payload[payload.length - 1]
	// * contains the rest of the URI.
	// */
	// byte[] payload = recs[j].getPayload();
	// String prefix = (String) INFCIntent.URI_PREFIX_MAP
	// .get(payload[0]);
	// byte[] fullUri = Bytes.concat(prefix
	// .getBytes(Charset.forName("UTF-8")),
	// Arrays.copyOfRange(payload, 1,
	// payload.length));
	// s += new String(fullUri,
	// Charset.forName("UTF-8"));
	// }
	// }
	// }
	// } catch (Exception e) {
	// payload = e.getMessage();
	// Log.e(NFCPlugin.class.toString(), e.getMessage());
	// }
	// }
	// return s;
	// }
	// }

}
