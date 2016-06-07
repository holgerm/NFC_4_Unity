using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using System.Text;
using System;

public class NFCExample : MonoBehaviour
{

	public Text id;
	public Text payload;
	public Text techs;

	// Use this for initialization
	void Start ()
	{
	
	}
	
	// Update is called once per frame
	void Update ()
	{
	
	}

	public void process (NFC_Info info)
	{
		id.text = info.Id;
		payload.text = info.Payload;
		techs.text = String.Join (",", info.Techs);
	}
}
