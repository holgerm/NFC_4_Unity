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
		String feedback = "OK";
		if (tag == null) {
			feedback = "ERROR: No tag given";
			Log.i(this.getClass().toString(), feedback);
			return feedback;
		}
		try {
			if (!tag.isConnected()) {
				tag.connect();
			}

			feedback = doWrite(text);
		} catch (IOException ioe) {
			feedback = "ERROR: Connect is NOT ok.";
			Log.i(this.getClass().toString(), feedback);
			return feedback;
		} catch (Exception exc) {
			feedback = "ERROR: Exception thrown: " + exc.getClass() + ", "
					+ exc.getMessage();
			Log.i(this.getClass().toString(), feedback);
			return feedback;
		}
		return feedback;
	}
}
