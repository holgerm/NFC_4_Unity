package com.questmill.test.nfc;

import com.questmill.nfc.wrapper.INFCIntent;

public class MockNFCIntent implements INFCIntent {

	private String id;
	private String payload;
	private String techs;

	public MockNFCIntent(String id, String payload, String[] techs) {
		setID(id);
		setPayload(payload);
		for (int i = 0; i < techs.length; i++) {
			addTech(techs[i]);
		}
	}

	public MockNFCIntent() {
	}

	void setPayload(String payload) {
		this.payload = payload;
	}

	void addTech(String tech) {
		if (this.techs == null)
			this.techs = new String(tech);
		else
			this.techs += "," + tech;
	}

	void setID(String id) {
		this.id = id;
	}
	
	public MockNFCIntent(String marshalledContent) {
		char[] receivedChars = marshalledContent.toCharArray ();

		int curIndex = 0;
		char key = '\0';
		StringBuilder valueBuilder;

		while (curIndex <= receivedChars.length - 2) {
			// index must leave a rest of at least two chars: the key and the ':'

			// parse key:
			key = receivedChars [curIndex];
			curIndex += 2; // skip the key char and the ':'

			// parse value:
			valueBuilder = new StringBuilder ();

			while (curIndex <= receivedChars.length - 1) {

				if (receivedChars [curIndex] != DELIMITER) {
					// ordinary content:
					valueBuilder.append (receivedChars [curIndex]);
					curIndex++;
					continue; // do NOT store key-value but proceed to gather the value
				}

				if (curIndex == receivedChars.length - 1) {
					// the current is a ',' and the last in the array we interpret it as an empty value
					// e.g. [i:,] => id = ""
					// we proceed one char and do not have to do anything since the while loop will terminate
					curIndex++;
				} else {
					// now we look at the char after the first ',':
					curIndex++;

					if (receivedChars [curIndex] == DELIMITER) {
						// we found a double ',,' which is just a ',' within the content
						// hence we add just one ',' ignore the second and go on parsing the value further
						// e.g. [p:me,, you and him] -> payload = "me, you and him"
						valueBuilder.append (receivedChars [curIndex]);
						curIndex++; // remember this is the second increase!
						continue; // ready to step one char further
					} else {
						// we found a single ',' which signifies the end of the value
						// we keep the index pointing at the next key and finish with this value
						// e.g. [i:123,p:hello] -> id = "123"; payload = "hello"
						break; // leaving value gathering and go on with next key-value pair
					}
				}
			}
			// end of KV pair reached: store it
			storeKV (key, valueBuilder.toString ());
		}

	}
	

	private void storeKV (char key, String value)
	{
		switch (key) {
		case KEY_ID:
			setID(value);
			break;
		case KEY_PAYLOAD:
			payload = value;
			break;
		case KEY_TECHS:
			techs = value;
			break;
		default:
			// unknown key parsed: invalid NFC_Info
			break;
		}
	}


	public String readID() {
		return id;
	}

	public String readPayload() {
		return payload;
	}

	public String readTech() {
		return techs;
	}

	public String[] readTechs() {
		if (techs == null || techs.equals("")) {
			return new String[] {};
		}
		return techs.split(",");
	}

}
