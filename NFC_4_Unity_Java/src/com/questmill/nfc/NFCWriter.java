package com.questmill.nfc;

import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.TagTechnology;

public abstract class NFCWriter {

	public static NFCWriter create(TagTechnology tag) {
		if (MifareClassic.class.isAssignableFrom(tag.getClass())) {
			return new MifareClassicWriter();
		}
		if (MifareUltralight.class.isAssignableFrom(tag.getClass())) {
			return new MifareUltralightWriter();
		}
		throw new IllegalArgumentException("Illegal tag type scanned");
	}

	public abstract String write(String text);

}
