package com.jeady.nfctools.jnfc

import android.nfc.FormatException
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.TagLostException
import android.nfc.tech.NdefFormatable
import android.os.Bundle
import android.util.Log
import com.jeady.nfctools.ui.nfcInfo.closeTag
import java.io.IOException

object NdefFormatableTag{
    private const val TAG = "[TAG_NdefFormatable]"
    fun format(tag: Tag, text: String="Ndef format.", readOnly: Boolean=false, onResult: (Boolean)->Unit){
        val ndef = NdefFormatable.get(tag)
        Log.i(TAG, "format: ndef=$ndef")
        try {
            ndef.connect()
            if(readOnly){
                ndef.formatReadOnly(NdefMessage(text.toByteArray()))
            }else{
                ndef.format(NdefMessage(text.toByteArray()))
            }
            onResult(true)
        }catch(e: Exception){
            Log.e(TAG, "format: exception $e", )
            onResult(false)
        } finally {
            closeTag(ndef)
        }
    }
}