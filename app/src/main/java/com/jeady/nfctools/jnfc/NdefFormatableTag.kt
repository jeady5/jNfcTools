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
    val TAG = "[TAG_NdefFormatable]"
    @OptIn(ExperimentalStdlibApi::class)
    fun read(tag: Tag, onParsed: (Bundle)->Unit){
        Log.d(TAG, "handleTag: NdefFormatable")
        val ndef = NdefFormatable.get(tag)
        Log.i(TAG, "handleTag: ndef=$ndef ${ndef.isConnected}")
        val newMsg = NdefMessage(NdefRecord.createUri("hello"))
        ndef.connect()
        Log.i(TAG, "handleTag: ndef3=$ndef ${ndef.isConnected}")
        try {
            Log.i(TAG, "handleTag: ndef2=$ndef ${ndef.isConnected}")
            ndef.format(newMsg)
            Log.i(TAG, "handleTag: ndef msg=$newMsg")
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
        Log.i(TAG, "handleTag: ndef byteArrayLength=${newMsg.byteArrayLength}")
        Log.i(TAG, "handleTag: ndef toByteArray=${newMsg.toByteArray().toHexString()}")
        newMsg.records.forEach {record->
            Log.w(TAG, "handleTag: record $record")
            Log.i(TAG, "handleTag: id ${record.id.toHexString()}")
            Log.i(TAG, "handleTag: tnf ${record.tnf}")
            Log.i(TAG, "handleTag: payload ${record.payload.toHexString()}")
            Log.i(TAG, "handleTag: type ${record.type.toHexString()}")
            Log.i(TAG, "handleTag: toUri ${record.toUri()}")
            Log.i(TAG, "handleTag: toMimeType ${record.toMimeType()}")
        }
    }
}