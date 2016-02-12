package com.questmill.nfc;

import android.util.Log;

public class MifareUltralightWriter extends NFCWriter {

	@Override
	public String write(String text) {
		Log.i(NFCPlugin.class.toString(), "Writing to a MIFARE ULTRALIGHT NFC TAG.");
		return "OK";
	}

}
