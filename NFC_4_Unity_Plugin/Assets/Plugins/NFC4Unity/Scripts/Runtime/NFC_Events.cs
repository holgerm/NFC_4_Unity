using UnityEngine;
using UnityEngine.Events;


[System.Serializable]
public class NFCReadPayloadEvent : UnityEvent<string>
{
}

[System.Serializable]
public class NFCReadDetailsEvent : UnityEvent<NFC_Info>
{
}
