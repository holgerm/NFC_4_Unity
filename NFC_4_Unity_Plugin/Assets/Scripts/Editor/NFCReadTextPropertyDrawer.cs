using UnityEngine;
using System.Collections;
using UnityEditor;
using UnityEngine.UI;

[CustomPropertyDrawer (typeof(NFCReadTextProperty))]
public class NFCReadTextPropertyDrawer : PropertyDrawer
{

	public override void OnGUI (Rect position, SerializedProperty property, GUIContent label)
	{
		NFC_Read myComponent = (NFC_Read)property.serializedObject.targetObject;
		Text text = myComponent.gameObject.GetComponent<Text> ();
		if (text != null && myComponent.textReadFromNFC == null) {
			myComponent.textReadFromNFC = text;
		}

		EditorGUI.PropertyField (position, property, label);
		property.serializedObject.ApplyModifiedProperties ();
	}

}
