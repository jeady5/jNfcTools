package com.jeady.nfctools.jnfc

import android.net.Uri
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import com.jeady.nfctools.jnfc.exception.ResultException

object NdefTag {
    private const val TAG = "[TAG_NDEF]"
    @OptIn(ExperimentalStdlibApi::class)
    fun read(tag: Tag, onParsed: (NdefTagInfo?)->Unit){
        val ndef = Ndef.get(tag)
        val records = mutableListOf<Bundle>()
        ndef.cachedNdefMessage?.records?.forEach {
            val record = Bundle()
            // human tnf type
            val tnfInt = it.tnf.toInt()
            val tnf = when(tnfInt) {
                3 -> "TNF_ABSOLUTE_URI"
                0 -> "TNF_EMPTY"
                4 -> "TNF_EXTERNAL_TYPE"
                2 -> "TNF_MIME_MEDIA"
                6 -> "TNF_UNCHANGED"
                5 -> "TNF_UNKNOWN"
                1 -> "TNF_WELL_KNOWN"
                else -> "UNKNOWN"
            }
            record.putInt("tnf", tnfInt)
            record.putString("tnfString", tnf)

            // type
            val type = it.type
//            NdefRecord.RTD_URI
            record.putByteArray("type", type)
            record.putString("typeString", type.toHexString())

            // id
            record.putByteArray("id", it.id)
            record.putString("idHexString", it.id.toHexString())

            // payload
            record.putByteArray("payload", it.payload)
            record.putString("payloadHexString", it.payload.toHexString())
            record.putString("payloadString", it.payload.decodeToString())

            records.add(record)
        }
        val tagInfo = try {
            NdefTagInfo(
                ndef.type,
                ndef.canMakeReadOnly(),
                ndef.isWritable,
                ndef.maxSize,
                records
            )
        }catch (e: Exception){
            null
        }
        onParsed(tagInfo)
    }
    fun writeApp(tag: Tag, packageName: String){
        val newRecord = NdefRecord.createApplicationRecord(packageName)
        write(tag, newRecord)
    }
    fun writeApp(tag: Tag, packageNames: List<String>){
        write(tag, packageNames.map{NdefRecord.createApplicationRecord(it)})
    }
    fun writeText(tag: Tag, text: String){
        val newRecord = NdefRecord.createTextRecord(null, text)
        write(tag, newRecord)
    }
    fun writeText(tag: Tag, texts: List<String>){
        write(tag, texts.map{NdefRecord.createTextRecord(null, it)})
    }
    fun writeUri(tag: Tag, uri: String){
        write(tag, NdefRecord.createUri(Uri.parse(uri)))
    }
    fun writeUri(tag: Tag, uris: List<String>){
        write(tag, uris.map{NdefRecord.createUri(Uri.parse(it))})
    }
    fun write(tag: Tag, record: NdefRecord){
        val newMsg = NdefMessage(record)
        write(tag, newMsg)
    }
    fun write(tag: Tag, records: List<NdefRecord>){
        val newMsg = NdefMessage(records.toTypedArray())
        write(tag, newMsg)
    }
    fun write(tag: Tag, msg: NdefMessage){
        val ndef = Ndef.get(tag)
        try {
            ndef.connect()
            ndef.writeNdefMessage(msg)
            ndef.close()
            throw ResultException("success")
        }catch (e: ResultException){
            throw ResultException("success")
        } catch (e: Exception){
            Log.e(TAG, "writeMsg: exception - $e")
            throw ResultException(e.localizedMessage)
        }
    }
}

data class NdefTagInfo(
    val ndefType: String = "",
    val canMakeReadOnly: Boolean = false,
    val writable: Boolean = false,
    val maxSize: Int = 0,
    val records: List<Bundle> = listOf()
)