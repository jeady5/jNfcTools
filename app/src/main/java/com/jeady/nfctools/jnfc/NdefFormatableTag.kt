package com.jeady.nfctools.jnfc

import android.nfc.FormatException
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.TagLostException
import android.nfc.tech.NdefFormatable
import android.os.Bundle
import android.util.Log
import java.io.IOException

object NdefFormatableTag{
    private const val TAG = "[TAG_NdefFormatable]"
    @OptIn(ExperimentalStdlibApi::class)
    fun read(tag: Tag, onParsed: (Bundle)->Unit){
        Log.d(TAG, "handleTag: NdefFormatable")
        val ndef = NdefFormatable.get(tag)
        Log.i(TAG, "handleTag: ndef=$ndef ${ndef.isConnected}")
        try {
            ndef.connect()
        }catch (lostEx: TagLostException){
            Log.e(TAG, "handleTag: lost exception", )
        }catch (ioEx: IOException){
            Log.e(TAG, "handleTag: io exception", )
        }catch (formatEx: FormatException){
            Log.e(TAG, "handleTag: format exception", )
        } finally {
            Log.i(TAG, "handleTag: close ndef formatable ")
            ndef.close()
        }
    }
}