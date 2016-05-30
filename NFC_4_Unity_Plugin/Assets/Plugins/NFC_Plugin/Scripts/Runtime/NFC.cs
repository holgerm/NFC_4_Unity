using UnityEngine;
using System.Collections;

namespace com.questmill.unity.nfc
{
	public class NFC
	{
		private static AndroidJavaClass javaNFC;

		public static AndroidJavaClass JavaNFC {
			get {
				if (javaNFC == null)
					javaNFC = new AndroidJavaClass ("com.questmill.nfc.NFCPlugin");
				return javaNFC;
			}
		}

		public static string lastReadContent {
			get {
				if (javaNFC == null) {
					javaNFC = new AndroidJavaClass ("com.questmill.nfc.NFCPlugin");
				}
				return javaNFC.CallStatic<string> ("getValue");
			}
		}
	}

}
