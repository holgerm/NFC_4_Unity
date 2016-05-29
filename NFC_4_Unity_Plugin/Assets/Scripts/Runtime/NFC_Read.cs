using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using com.questmill.unity.nfc;

public class NFC_Read : MonoBehaviour
{
	[NFCReadTextProperty]
	public Text textReadFromNFC;

	void Awake ()
	{
	}

	// Use this for initialization
	void Start ()
	{
		if (textReadFromNFC == null)
			setTextRead ();
		Debug.Log ("start()");
	}

	void setTextRead ()
	{
		Text myText = gameObject.GetComponent<Text> ();
		if (myText != null) {
			textReadFromNFC = myText;
		} else {
			textReadFromNFC = (Text)FindObjectOfType (typeof(Text));
		}
	}
	
	// Update is called once per frame
	void Update ()
	{
		#if UNITY_ANDROID 
		if (RuntimePlatform.Android != Application.platform)
			return;
		if (textReadFromNFC != null) {
			textReadFromNFC.text = NFC.lastReadContent;
		}
		#endif
	}

	void NFCRead (string textRead)
	{
		#if UNITY_ANDROID 
//		if (RuntimePlatform.Android != Application.platform)
//			return;
//		if (textReadFromNFC != null) {
//			textReadFromNFC.text = textRead;
//		}
		Debug.Log ("NFCRead()");
		#endif

	}
}

