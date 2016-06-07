package com.questmill.test.nfc;

import org.junit.Assert;
import org.junit.Test;

import com.questmill.nfc.NFCInfo;
import com.questmill.nfc.wrapper.INFCIntent;

public class TestNFCInfo {

	INFCIntent mockNFCIntent;

	@Test
	public void simpleCase() throws Exception {

		// Arrange:
		INFCIntent mockNFCIntent = new MockNFCIntent(
				"007",
				"hello world!",
				new String[] { "nfc.tech.NfcA", "nfc.tech.Ndef" });
		NFCInfo info = new NFCInfo(mockNFCIntent);

		// Act:
		String marshalledContent = info.marshall();

		// Assert:
		Assert.assertEquals(
				"i:007,p:hello world!,t:nfc.tech.NfcA,,nfc.tech.Ndef",
				marshalledContent);
	}

	@Test
	public void commasInPayload() throws Exception {

		// Arrange:
		INFCIntent mockNFCIntent = new MockNFCIntent("001",
				"Hallo, lieber Freund, wir können auch Kommas!",
				new String[] { "android.nfc.tech.NfcA" });
		NFCInfo info = new NFCInfo(mockNFCIntent);

		// Act:
		String marshalledContent = info.marshall();

		// Assert:
		Assert.assertEquals(
				"i:001,p:Hallo,, lieber Freund,, wir können auch Kommas!,t:android.nfc.tech.NfcA",
				marshalledContent);
	}

	@Test
	public void onlyPayloadGiven() throws Exception {

		// Arrange:
		MockNFCIntent mockNFCIntent = new MockNFCIntent();
		mockNFCIntent.setPayload("Hallo, lieber Freund, wir können auch Kommas!");
		NFCInfo info = new NFCInfo(mockNFCIntent);

		// Act:
		String marshalledContent = info.marshall();

		// Assert:
		Assert.assertEquals(
				"p:Hallo,, lieber Freund,, wir können auch Kommas!",
				marshalledContent);
	}

	@Test
	public void emptyIDWillBeIgnored() throws Exception {

		// Arrange:
		MockNFCIntent mockNFCIntent = new MockNFCIntent();
		mockNFCIntent.setID("");
		mockNFCIntent.setPayload("Hallo");

		NFCInfo info = new NFCInfo(mockNFCIntent);

		// Act:
		String marshalledContent = info.marshall();

		// Assert:
		Assert.assertEquals(
				"p:Hallo",
				marshalledContent);
		
		// TODO this content is invalid
	}
	
	@Test
	public void emptyPayloadWillBeIgnored() throws Exception {

		// Arrange:
		MockNFCIntent mockNFCIntent = new MockNFCIntent();
		mockNFCIntent.setID("007");
		mockNFCIntent.setPayload("");

		NFCInfo info = new NFCInfo(mockNFCIntent);

		// Act:
		String marshalledContent = info.marshall();

		// Assert:
		Assert.assertEquals(
				"i:007",
				marshalledContent);
		
		// TODO this content is invalid
	}
	
	@Test
	public void emptyTechlistWillBeIgnored() throws Exception {

		// Arrange:
		MockNFCIntent mockNFCIntent = new MockNFCIntent();
		mockNFCIntent.setID("007");
		mockNFCIntent.setPayload("hallo");
		
		NFCInfo info = new NFCInfo(mockNFCIntent);

		// Act:
		String marshalledContent = info.marshall();

		// Assert:
		Assert.assertEquals(
				"i:007,p:hallo",
				marshalledContent);
	}
}
