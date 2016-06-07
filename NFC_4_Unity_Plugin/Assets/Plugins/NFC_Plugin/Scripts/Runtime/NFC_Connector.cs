﻿using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using UnityEngine.Events;
using System.Collections.Generic;

public class NFC_Connector : MonoBehaviour
{
	// TODO Extract Observer into interface and test it
	// TODO Extract Singleton into interface and test it

	public const string NAME = "NFC_Connector";

	private List<NFC_ReaderUI> registeredReaderUIs;

	private static NFC_Connector _connector;

	public static NFC_Connector Connector {
		get {
			if (_connector == null) {
				GameObject go = new GameObject (NAME);
				go.AddComponent<NFC_Connector> ();
				_connector = go.GetComponent<NFC_Connector> ();
			}
			return _connector;
		}
	}

	void Awake ()
	{
		registeredReaderUIs = new List<NFC_ReaderUI> ();
	}

	public bool registerReaderUI (NFC_ReaderUI newNFCReaderUI)
	{
		if (registeredReaderUIs.Contains (newNFCReaderUI))
			return false;
		else {
			registeredReaderUIs.Add (newNFCReaderUI);
			return true;
		}
	}

	public bool unregisterReaderUI (NFC_ReaderUI nfcReaderUI)
	{
		return registeredReaderUIs.Remove (nfcReaderUI);
	}

	/// <summary>
	/// Called by Android Java Plugin when an NFC Tag is read. The read payload is given as parameter. 
	/// </summary>
	/// <param name="payload">Payload.</param>
	public void NFCReadPayload (string payload)
	{
		#if UNITY_ANDROID 
		foreach (NFC_ReaderUI reader in registeredReaderUIs) {
			reader.onNFCPayloadRead (payload);
		}
		#elif UNITY_EDITOR
		Debug.LogWarning ("NFC Plugin only works on Android Platform.");
		#endif
	}

	/// <summary>
	/// Called by Android Java Plugin when an NFC Tag is read.
	/// The read details are given as parameter and are unmarshalled first. 
	/// Then the different contents are made available to the game by triggering an event.
	/// </summary>
	/// <param name="id">ID.</param>
	public void NFCReadDetails (string marshalledContent)
	{
		#if UNITY_ANDROID 
		NFC_Info info = new NFC_Info (marshalledContent);

		foreach (NFC_ReaderUI reader in registeredReaderUIs) {
			reader.onNFCDetailsRead (info);
		}
		#elif UNITY_EDITOR
		Debug.LogWarning ("NFC Plugin only works on Android Platform.");
		#endif
	}


}




