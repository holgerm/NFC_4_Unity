using UnityEngine;
using System.Collections;
using UnityEngine.UI;

public class NFCTest : MonoBehaviour
{
	public Text tag_output_text;
	AndroidJavaClass pluginTutorialActivityJavaClass;
	
	void Start ()
	{
#if UNITY_ANDROID
		AndroidJNI.AttachCurrentThread ();
		pluginTutorialActivityJavaClass = new AndroidJavaClass ("com.questmill.nfc.NFCPlugin");
#endif
	}


	void Update ()
	{
#if UNITY_ANDROID
		if (pluginTutorialActivityJavaClass != null) {
			string value = pluginTutorialActivityJavaClass.CallStatic<string> ("getValue");
			tag_output_text.text = "Value:\n" + value;
		}
#endif
	}
}
