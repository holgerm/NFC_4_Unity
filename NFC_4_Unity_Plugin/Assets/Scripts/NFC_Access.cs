using UnityEngine;
using System.Collections;
using UnityEngine.UI;

public class NFC_Access : MonoBehaviour
{
	public Text tag_output_text;
#if UNITY_ANDROID 
	AndroidJavaClass pluginTutorialActivityJavaClass;
#endif

	void Start ()
	{
#if UNITY_ANDROID 
		if (RuntimePlatform.Android != Application.platform) 
			return;
		AndroidJNI.AttachCurrentThread ();
		pluginTutorialActivityJavaClass = new AndroidJavaClass ("com.questmill.nfc.NFCPlugin");
#endif
	}

	void Update ()
	{
#if UNITY_ANDROID 
		if (RuntimePlatform.Android != Application.platform) 
			return;
		if (pluginTutorialActivityJavaClass != null) {
			tag_output_text.text = pluginTutorialActivityJavaClass.CallStatic<string> ("getValue");
		}
#endif
	}

	public void writeTestContentToTag ()
	{
		#if UNITY_ANDROID 
		if (pluginTutorialActivityJavaClass != null) {
			string feedback = pluginTutorialActivityJavaClass.CallStatic<string> ("writeTestContent");
			Debug.Log ("Test content has been written to NCF Tag. Feedback is: " + feedback);
		}
		#endif
	}
}
