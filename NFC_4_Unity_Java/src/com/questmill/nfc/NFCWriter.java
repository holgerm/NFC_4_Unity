package com.questmill.nfc;

import java.io.IOException;

import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.TagTechnology;
import android.util.Log;

public abstract class NFCWriter {

	TagTechnology tag;

	protected NFCWriter(TagTechnology tag) {
		this.tag = tag;
	}

	public static NFCWriter create(TagTechnology tag) {
		if (MifareClassic.class.isAssignableFrom(tag.getClass())) {
			return new MifareClassicWriter(tag);
		}
		if (MifareUltralight.class.isAssignableFrom(tag.getClass())) {
			return new MifareUltralightWriter(tag);
		}
		throw new IllegalArgumentException("Illegal tag type scanned");
	}

	abstract String doWrite(String text);

	/**
	 * Template Method (pattern) for writing to different tags which does the
	 * general things itself and lets the subclasses do the specialties.
	 * 
	 * @param text
	 * @return
	 */
	public String write(String text) {
		if (tag == null)
			return "ERROR: No tag given";
		try {
			tag.connect();
			Log.i(this.getClass().toString(), "Connect is ok.");
		} catch (IOException ioe) {
			Log.i(this.getClass().toString(), "ERROR: Connect is NOT ok.");
		}

		return doWrite(text);
		// TODO Auto-generated method stub

	}
}
