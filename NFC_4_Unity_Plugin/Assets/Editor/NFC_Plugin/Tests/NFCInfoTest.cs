using UnityEngine;
using UnityEditor;
using NUnit.Framework;

public class NFCInfoTest
{

	[Test]
	public void TypicalCase ()
	{
		//Act
		NFC_Info nfcInfo = new NFC_Info ("i:007,p:hello world!,t:nfc.tech.NfcA,,nfc.tech.Ndef");

		//Assert
		Assert.That (nfcInfo.Valid);
		Assert.That (nfcInfo.Id.Equals ("007"));
		Assert.That (nfcInfo.Payload.Equals ("hello world!"));
		Assert.That (nfcInfo.Techs.Length == 2);
		Assert.That (nfcInfo.Techs [0].Equals ("nfc.tech.NfcA"));
		Assert.That (nfcInfo.Techs [1].Equals ("nfc.tech.Ndef"));
	}

	[Test]
	public void CommaUnmasking ()
	{
		// Act:
		NFC_Info nfcInfo = new NFC_Info ("i:001,p:This text,, as an example,, contains multiple masked commas,, like this one.,t:some.tech");

		// Assert:
		nfcInfo.Payload.Equals ("This text, as an example, contains multiple masked commas, like this one.");
	}

	[Test]
	public void Compare ()
	{
		// Act:
		NFC_Info empty1 = new NFC_Info ("");
		NFC_Info empty2 = new NFC_Info ("");
		NFC_Info filled1 = new NFC_Info ("i:001,p:Content,t:some.tech,,some.other.tech");
		NFC_Info filled2 = new NFC_Info ("i:001,p:Content,t:some.tech,,some.other.tech");

		// Assert:
		Assert.That (empty1 != empty2);
		Assert.That (empty1.Equals (empty2));
		Assert.That (!empty1.Equals (filled1));
		Assert.That (filled1 != filled2);
		Assert.That (filled1.Equals (filled2));
	}

	[Test]
	public void Empty ()
	{
		//Act
		//Try to rename the GameObject
		NFC_Info nfcInfo = new NFC_Info ("");

		//Assert
		//The object has a new name
		Assert.That (nfcInfo != null);
		Assert.That (!nfcInfo.Valid);
		Assert.That (nfcInfo.Id == null);
		Assert.That (nfcInfo.Payload == null);
		Assert.That (nfcInfo.Techs == null);
	}

	[Test]
	public void NoId_PayloadSet ()
	{
		// Act:
		NFC_Info nfcInfo_NoId_PayloadSet = new NFC_Info ("p:some content");

		// Assert:
		Assert.That (nfcInfo_NoId_PayloadSet != null);
		Assert.That (!nfcInfo_NoId_PayloadSet.Valid);
	}

	[Test]
	public void IdEmpty_PayloadSet ()
	{
		// Act:
		NFC_Info nfcInfo_IdEmpty_PayloadSet = new NFC_Info ("i:,p:some content");

		// Assert:
		Assert.That (nfcInfo_IdEmpty_PayloadSet != null);
		Assert.That (!nfcInfo_IdEmpty_PayloadSet.Valid);
	}

	[Test]
	public void IdSet_NoPayload ()
	{
		// Act:
		NFC_Info nfcInfo_IdSet_NoPayload = new NFC_Info ("i:1");

		// Assert:
		Assert.That (nfcInfo_IdSet_NoPayload != null);
		Assert.That (!nfcInfo_IdSet_NoPayload.Valid);
	}

	[Test]
	public void IdSet_PayloadEmpty ()
	{
		// Act:
		NFC_Info nfcInfo_IdSet_PayloadEmpty = new NFC_Info ("i:1,p:");

		// Assert:
		Assert.That (nfcInfo_IdSet_PayloadEmpty != null);
		Assert.That (nfcInfo_IdSet_PayloadEmpty.Valid);
	}

	[Test]
	public void WrongContent ()
	{
		// Act:
		NFC_Info nfcInfoUndefinedKeys = new NFC_Info ("q:q is undefined,b:b ia also undefined,n:even n is not yet defined as key");
		NFC_Info nfcInfoUndefinedKey = new NFC_Info ("q:single undefined key");
		NFC_Info nfcInfoGarbish = new NFC_Info ("this is garbish content");

		// Assert:
		Assert.That (nfcInfoUndefinedKeys != null);
		Assert.That (!nfcInfoUndefinedKeys.Valid);

		Assert.That (nfcInfoUndefinedKey != null);
		Assert.That (!nfcInfoUndefinedKey.Valid);

		Assert.That (nfcInfoGarbish != null);
		Assert.That (!nfcInfoGarbish.Valid);
	}

	[Test]
	public void MarshallTypicalCase ()
	{
		// Arrange:
		NFC_Info info = new NFC_Info ("001", "hallo", new string[] { "nfc.tech.NDEF", "nfc.tech.nfcA" });

		// Act:
		string marshalled = info.marshall ();

		// Assert:
		Assert.That (marshalled.Equals ("i:001,p:hallo,t:nfc.tech.NDEF,,nfc.tech.nfcA"));
	}

	[Test]
	public void MarshallCommasInPayload ()
	{
		// Arrange:
		NFC_Info info = new NFC_Info ("001", "Hi, we can deal with commas, like this, too.", new string[] {
			"nfc.tech.NDEF",
			"nfc.tech.nfcA"
		});

		// Act:
		string marshalled = info.marshall ();

		// Assert:
		Assert.That (marshalled.Equals ("i:001,p:Hi,, we can deal with commas,, like this,, too.,t:nfc.tech.NDEF,,nfc.tech.nfcA"));
	}

	[Test]
	public void MarshallWithoutTechs ()
	{
		// Arrange:
		NFC_Info info = new NFC_Info ("001", "Hi, we can deal with commas, like this, too.");

		// Act:
		string marshalled = info.marshall ();

		// Assert:
		Assert.That (marshalled.Equals ("i:001,p:Hi,, we can deal with commas,, like this,, too."));
	}

	// TODO what if id or payload are empty?

}
