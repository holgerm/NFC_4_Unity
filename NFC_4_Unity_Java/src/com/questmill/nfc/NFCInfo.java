package com.questmill.nfc;

import com.questmill.nfc.wrapper.INFCIntent;

public class NFCInfo {

	private static final char KEY_VALUE_DELIMITER = ':';
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
			details.append("" + INFCIntent.KEY_ID + KEY_VALUE_DELIMITER + maskCommas(id));
		}
		// payload:
		if (payload != null && !payload.isEmpty()) {
			if (details.length() > 0)
				details.append(INFCIntent.DELIMITER);
			details.append("" + INFCIntent.KEY_PAYLOAD + KEY_VALUE_DELIMITER
					+ maskCommas(payload));
		}
		// tech:
		if (tech != null && !tech.isEmpty()) {
			details.append(INFCIntent.DELIMITER);
			details.append("" + INFCIntent.KEY_TECHS + KEY_VALUE_DELIMITER + maskCommas(tech));
		}

		return details.toString();
	}

	private String maskCommas(String originalContent) {
		if (originalContent == null || originalContent.isEmpty())
			return "";
		return originalContent.replaceAll(",", ",,");
	}

}
