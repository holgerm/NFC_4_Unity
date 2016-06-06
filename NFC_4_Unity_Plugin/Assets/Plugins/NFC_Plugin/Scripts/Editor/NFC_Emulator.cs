using UnityEngine;
using System.Collections;
using UnityEditor;

public class NFC_Emulator : EditorWindow
{
	string id, payload, techs;

	[MenuItem ("Window/NFC Emulator")]
	public static void  ShowWindow ()
	{
		EditorWindow.GetWindow (typeof(NFC_Emulator));
	}

	void OnEnable ()
	{
		// ID:
		id = EditorPrefs.GetString ("nfc.id");
		if (id == null || id.Equals (""))
			id = "id goes here ...";

		// Payload:
		payload = EditorPrefs.GetString ("nfc.payload");
		if (payload == null || payload.Equals (""))
			payload = "payload goes here ...";

		// Techs: // TODO make list of string instead to prevent errors
		techs = EditorPrefs.GetString ("nfc.techs");
		if (techs == null)
			techs = "(optional techs)";
	}

	void OnGUI ()
	{
		GUILayout.Label ("NFC Emulation", EditorStyles.boldLabel);

		// ID: may not be empty
		string idOld = id;
		id = EditorGUILayout.TextField ("ID", id);
		if (id.Equals (""))
			id = idOld; // take old value if set empty.

		// Payload: may not be empty
		string payloadOld = payload;
		payload = EditorGUILayout.TextField ("Payload", payload);
		if (payload.Equals (""))
			payload = payloadOld; // take old value if set empty.

		// Techs: TODO should be a list of strings
		techs = EditorGUILayout.TextField ("Tech Descriptions", techs);

		// Read NFC Button:
		GUI.enabled = Application.isPlaying;
		if (GUILayout.Button ("Read NFC Chip")) {
			emulateNFCRead ();
		}
		GUI.enabled = true;
	}

	private void emulateNFCRead ()
	{
		// grab the nfc receiver: 
		GameObject nfcReceiver = GameObject.Find ("/NFC_Connector");
		if (nfcReceiver == null) {
			Debug.LogError ("NFCReceiver missing. The NFC Plugin does not find the NFCReceiver GameObject, but it needs it.");
			return;
		}
		NFC_Connector nfcConnector = nfcReceiver.GetComponent<NFC_Connector> ();
		if (nfcConnector == null) {
			Debug.LogError ("NFC_Connector missing. The NFC Plugin finds the NFCReceiver GameObject, but it lacks the NFC_Connector Script Component.");
			return;
		}

		// use simple mode first:
		nfcConnector.NFCReadPayload (payload);
	}

	void OnDisable ()
	{
		EditorPrefs.SetString ("nfc.id", id);
		EditorPrefs.SetString ("nfc.payload", payload);
		EditorPrefs.SetString ("nfc.techs", techs);
	}
}
