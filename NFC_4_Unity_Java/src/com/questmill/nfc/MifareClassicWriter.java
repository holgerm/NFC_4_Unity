package com.questmill.nfc;

import android.util.Log;

public class MifareClassicWriter extends NFCWriter {

	@Override
	public String write(String text) {
		Log.i(NFCPlugin.class.toString(),
				"Writing to a MIFARE CLASSIC NFC TAG.");
		return "OK";
	}

}
