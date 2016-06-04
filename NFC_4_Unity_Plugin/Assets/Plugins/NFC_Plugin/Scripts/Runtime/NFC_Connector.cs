using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using UnityEngine.Events;

public class NFC_Connector : MonoBehaviour
{
	public Text NFCPayloadText;
	public NFCReadPayloadEvent OnNFCReadPayload;

	public Text NFCIdText;

	public NFCReadDetailsEvent OnNFCReadDetails;

	public NFC_Info myInfo;

	void Awake ()
	{
	}

	// Use this for initialization
	void Start ()
	{
		if (NFCPayloadText == null)
			setTextRead ();
	}

	void setTextRead ()
	{
		Text myText = gameObject.GetComponent<Text> ();
		if (myText != null) {
			NFCPayloadText = myText;
		} else {
			NFCPayloadText = (Text)FindObjectOfType (typeof(Text));
		}
	}
	
	// Update is called once per frame
	void Update ()
	{
	}

	/// <summary>
	/// Called by Android Java Plugin when an NFC Tag is read. The read payload is given as parameter.
	/// </summary>
	/// <param name="payload">Payload.</param>
	public void NFCReadPayload (string payload)
	{
		#if UNITY_ANDROID 
		if (NFCPayloadText != null) {
			NFCPayloadText.text = payload;
		}
		if (OnNFCReadPayload != null) {
			OnNFCReadPayload.Invoke (payload);
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

		if (NFCIdText != null) {
			NFCIdText.text = info.Id;
		}
		if (NFCPayloadText != null) {
			NFCPayloadText.text = info.Payload;
		}
		if (OnNFCReadDetails != null)
			OnNFCReadDetails.Invoke (info);
		#elif UNITY_EDITOR
		Debug.LogWarning ("NFC Plugin only works on Android Platform.");
		#endif
	}
}

[System.Serializable]
public class NFCReadPayloadEvent : UnityEvent<string>
{
}

[System.Serializable]
public class NFCReadDetailsEvent : UnityEvent<NFC_Info>
{
}




