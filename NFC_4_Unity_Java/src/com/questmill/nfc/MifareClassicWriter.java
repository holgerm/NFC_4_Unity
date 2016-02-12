package com.questmill.nfc;

import android.nfc.tech.TagTechnology;
import android.util.Log;

public class MifareClassicWriter extends NFCWriter {

	public MifareClassicWriter(TagTechnology tag) {
		super(tag);
	}

	@Override
	public String doWrite(String text) {
		Log.i(this.getClass().toString(),
				"Writing to a MIFARE CLASSIC NFC TAG.");
		return "OK";
	}

}
