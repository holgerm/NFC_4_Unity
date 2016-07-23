package com.questmill.test.nfc;


import org.junit.Assert;
import org.junit.Test;

import com.questmill.nfc.NFCInfo;
import com.questmill.nfc.wrapper.INFCIntent;

public class TestNFCInfo {

	INFCIntent mockNFCIntent;

	@Test
	public void typicalCase() throws Exception {

		// Arrange:
		INFCIntent mockNFCIntent = new MockNFCIntent("007", "hello world!",
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
		mockNFCIntent
				.setPayload("Hallo, lieber Freund, wir können auch Kommas!");
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
		Assert.assertEquals("p:Hallo", marshalledContent);

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
		Assert.assertEquals("i:007", marshalledContent);

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
		Assert.assertEquals("i:007,p:hallo", marshalledContent);
	}

	@Test
	public void unmarshallTypicalCase() throws Exception {

		// Arrange & Act:
		MockNFCIntent mockNFCIntent = new MockNFCIntent(
				"i:007,p:hello world!,t:nfc.tech.NfcA,,nfc.tech.Ndef");

		// Assert:
		String id = mockNFCIntent.readID();
		Assert.assertEquals("007", id);
		String payload = mockNFCIntent.readPayload();
		Assert.assertEquals("hello world!", payload);
		String[] techs = mockNFCIntent.readTechs();
		Assert.assertEquals(techs.length, 2);
		Assert.assertEquals(techs[0], "nfc.tech.NfcA");
		Assert.assertEquals(techs[1], "nfc.tech.Ndef");

	}

	@Test
	public void unmarshallMaskedCommas() throws Exception {

		// Arrange & Act:
		MockNFCIntent mockNFCIntent = new MockNFCIntent(
				"i:001,p:This text,, as an example,, contains multiple masked commas,, like this one.,t:some.tech");

		// Assert:
		String id = mockNFCIntent.readID();
		Assert.assertEquals("001", id);
		String payload = mockNFCIntent.readPayload();
		Assert.assertEquals(
				"This text, as an example, contains multiple masked commas, like this one.",
				payload);
		String[] techs = mockNFCIntent.readTechs();
		Assert.assertEquals(techs.length, 1);
	}

	@Test
	public void unmarshallEmptyPayload() throws Exception {

		// Arrange & Act:
		MockNFCIntent mockNFCIntent = new MockNFCIntent("i:1,p:");

		// Assert:
		String id = mockNFCIntent.readID();
		Assert.assertEquals("1", id);
		String payload = mockNFCIntent.readPayload();
		Assert.assertNotNull(payload);
		Assert.assertEquals("", payload);
		String[] techs = mockNFCIntent.readTechs();
		Assert.assertNotNull(techs);
		Assert.assertEquals(techs.length, 0);

	}

	@Test
	public void unmarshallMissingPayload() throws Exception {

		// Arrange & Act:
		MockNFCIntent mockNFCIntent = new MockNFCIntent("i:1,p:");

		// Assert:
		String id = mockNFCIntent.readID();
		Assert.assertEquals("1", id);
		String payload = mockNFCIntent.readPayload();
		Assert.assertNotNull(payload);
		Assert.assertEquals("", payload);
		String[] techs = mockNFCIntent.readTechs();
		Assert.assertNotNull(techs);
		Assert.assertEquals(techs.length, 0);

	}

	@Test
	public void unmarshallEmptyTech() throws Exception {

		// Arrange & Act:
		MockNFCIntent mockNFCIntent = new MockNFCIntent("i:1,p:hello world!,t:");

		// Assert:
		String id = mockNFCIntent.readID();
		Assert.assertEquals("1", id);
		String payload = mockNFCIntent.readPayload();
		Assert.assertNotNull(payload);
		Assert.assertEquals("hello world!", payload);
		String[] techs = mockNFCIntent.readTechs();
		Assert.assertNotNull(techs);
		Assert.assertEquals(techs.length, 0);

	}

	@Test
	public void unmarshallMissingTechd() throws Exception {
		// Arrange & Act:
		MockNFCIntent mockNFCIntent = new MockNFCIntent("i:1,p:");

		// Assert:
		String id = mockNFCIntent.readID();
		Assert.assertEquals("1", id);
		String payload = mockNFCIntent.readPayload();
		Assert.assertNotNull(payload);
		Assert.assertEquals("", payload);
		String[] techs = mockNFCIntent.readTechs();
		Assert.assertNotNull(techs);
		Assert.assertEquals(techs.length, 0);

	}

}