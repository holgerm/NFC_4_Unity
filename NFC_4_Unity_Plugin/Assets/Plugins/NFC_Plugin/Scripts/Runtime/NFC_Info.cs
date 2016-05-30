using UnityEngine;
using System.Collections;
using System;

/// <summary>
/// Marshalled content has this format:
/// 
/// key:value,key:value,key:value etc.
/// 
/// Where key is a char that can be i for id, p for payload, t for techlist.
/// And value is the original content from the NFC chip, except for occurrences of the DELIMITER character which are doubled. 
/// I.e. every occurrence of ',' in the content has been replaced by ',,'. 
/// Therefore the unmarshall method searches for double DELIMITERs and replaces them by one single DELIMITER again. 
/// Occurrences of single DELIMITER in the marshalled content will in contrast be interpreted as the end of the 
/// currently parsed content and ignored but trigger the switch to searching for the next key.
/// </summary>
public struct NFC_Info
{
	private const char DELIMITER = ',';

	private string _id;

	public string Id {
		get {
			return _id;
		}
		set {
			_id = value;
		}
	}

	private string _payload;

	public string Payload {
		get {
			return _payload;
		}
		set {
			_payload = value;
		}
	}

	private string[] _techs;

	public string[] Techs {
		get {
			return _techs;
		}
		set {
			_techs = value;
		}
	}

	private bool _valid;

	public bool Valid {
		get {
			return _valid;
		}
		set {
			_valid = value;
		}
	}

	public NFC_Info (string receivedString)
	{
		Id = null;
		Payload = null;
		Techs = null;
		Valid = true;

		unmarshall (receivedString);
	}

	private void unmarshall (string receivedString)
	{
		char[] receivedChars = receivedString.ToCharArray ();

		int curIndex = 0;
		char key = '\0';
		System.Text.StringBuilder valueBuilder;

		while (curIndex <= receivedChars.Length - 2) {
			// index must leave a rest of at least two chars: the key and the ':'

			// parse key:
			key = receivedChars [curIndex];
			curIndex += 2; // skip the key char and the ':'

			// parse value:
			valueBuilder = new System.Text.StringBuilder ();
			while (curIndex <= receivedChars.Length - 1) {

				if (receivedChars [curIndex] != DELIMITER) {
					// ordinary content:
					valueBuilder.Append (receivedChars [curIndex]);
					curIndex++;
					continue; // ready to step one char further in gathering the value
				}

				if (curIndex == receivedChars.Length - 1) {
					// the current is a ',' and the last in the array we interpret it as an empty value
					// e.g. [i:,] => id = ""
					// we do not have to do anything since the while loop will terminate
				} else {
					// now we look at the char after the first ',':
					curIndex++;

					if (receivedChars [curIndex] == DELIMITER) {
						// we found a double ',,' which is just a ',' within the content
						// hence we add just one ',' ignore the second and go on parsing the value further
						// e.g. [p:me,, you and him] -> payload = "me, you and him"
						valueBuilder.Append (receivedChars [curIndex]);
						curIndex++; // remember this is the second increase!
						continue; // ready to step one char further
					} else {
						// we found a single ',' which signifies the end of the value
						// we keep the index pointing at the next key and finish with this value
						// e.g. [i:123,p:hello] -> id = "123"; payload = "hello"
						storeKV (key, valueBuilder.ToString ());
						break; // leaving value gathering and go on with next key-value pair
					}
				}
			}
		}
	}

	private void storeKV (char key, string value)
	{
		switch (key) {
		case 'i':
			Id = value;
			break;
		case 'p':
			Payload = value;
			break;
		case 't':
			Techs = value.Split (new string[] { "," }, System.StringSplitOptions.RemoveEmptyEntries);
			break;
		default:
			// unknown key parsed: invalid NFC_Info
			Valid = false;
			break;
		}
	}
}
