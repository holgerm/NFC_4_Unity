package com.questmill.test.nfc;

import com.questmill.nfc.wrapper.INFCIntent;

public class MockNFCIntent implements INFCIntent {

	private String id;
	private String payload;
	private String techs;

	MockNFCIntent(String id, String payload, String[] techs) {
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

	public String readID() {
		// TODO Auto-generated method stub
		return id;
	}

	public String readPayload() {
		// TODO Auto-generated method stub
		return payload;
	}

	public String readTech() {
		// TODO Auto-generated method stub
		return techs;
	}

}
