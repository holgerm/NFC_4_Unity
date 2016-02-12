package com.questmill.nfc;

import android.nfc.tech.TagTechnology;
import android.util.Log;

public class MifareUltralightWriter extends NFCWriter {

	public MifareUltralightWriter(TagTechnology tag) {
		super(tag);
	}

	@Override
	public String doWrite(String text) {
		Log.i(this.getClass().toString(),
				"Writing to a MIFARE ULTRALIGHT NFC TAG.");
		return "OK";
	}

}
