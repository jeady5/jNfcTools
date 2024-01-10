package com.jeady.nfctools.jnfc

import android.net.Uri
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import com.jeady.nfctools.jnfc.exception.ResultException
import com.jeady.nfctools.ui.nfcInfo.closeTag

object NdefTag {
    private const val TAG = "[TAG_NDEF]"
    @OptIn(ExperimentalStdlibApi::class)
    fun read(tag: Tag, onParsed: (NdefTagInfo)->Unit){
        val ndef = Ndef.get(tag)
        var status = "ready"
        val records = mutableListOf<RecordInfo>()
        var canMakeReadOnly = false
        try {
            ndef.connect()
            canMakeReadOnly = ndef.canMakeReadOnly()
            ndef.ndefMessage?.records?.forEach {
                val tnf = it.tnf
                val tnfString = when(tnf.toInt()) {
                    3 -> "TNF_ABSOLUTE_URI"
                    0 -> "TNF_EMPTY"
                    4 -> "TNF_EXTERNAL_TYPE"
                    2 -> "TNF_MIME_MEDIA"
                    6 -> "TNF_UNCHANGED"
                    5 -> "TNF_UNKNOWN"
                    1 -> "TNF_WELL_KNOWN"
                    else -> "UNKNOWN"
                }
                records.add(RecordInfo(
                    it.id.toHexString(),
                    tnf,
                    tnfString,
                    it.type,
                    it.payload,
                    it.payload.toHexString(),
                    it.payload.decodeToString()
                ))
            }
        }catch (e: Exception){
            Log.e(TAG, "read: exception $e")
            status = "error"
        }finally {
            onParsed(NdefTagInfo(
                status,
                ndef.type,
                canMakeReadOnly,
                ndef.isWritable,
                ndef.maxSize,
                records
            ))
            closeTag(ndef)
        }
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
            throw ResultException("success")
        }catch (e: ResultException){
            throw ResultException("success")
        } catch (e: Exception){
            Log.e(TAG, "writeMsg: exception - $e")
            throw ResultException(e.localizedMessage)
        }finally {
            closeTag(ndef)
        }
    }
}

data class NdefTagInfo(
    val status: String = "ready",
    val ndefType: String = "",
    val canMakeReadOnly: Boolean = false,
    val writable: Boolean = false,
    val maxSize: Int = 0,
    val records: List<RecordInfo> = listOf()
)
data class RecordInfo(
    val id: String = "",
    val tnf: Short = 0,
    val tnfString: String = "",
    val type: ByteArray = byteArrayOf(),
    val payload: ByteArray = byteArrayOf(),
    val payloadHexString: String = "",
    val payloadString: String = "",
)