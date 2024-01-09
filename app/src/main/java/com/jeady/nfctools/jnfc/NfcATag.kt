package com.jeady.nfctools.jnfc

import android.nfc.Tag
import android.nfc.tech.NfcA
import android.os.Bundle
import android.util.Log
import java.io.IOException

object NfcATag {
    private const val TAG = "[TAG_NFCA]"
    @OptIn(ExperimentalStdlibApi::class)
    fun read(tag: Tag, onParsed: (NfcATagInfo?)->Unit){
        val nfcA = NfcA.get(tag)
        var cardId = byteArrayOf()
        var allByte = byteArrayOf()
        var response = byteArrayOf()
        try {
            nfcA.connect()
            val selectId = byteArrayOf(0x78)
            val selectAll = byteArrayOf(0x00, 0x00)
            val select8 = byteArrayOf(0x02, 0x00)
            val select1 = byteArrayOf(0x01, 0x00)
            val select16 = byteArrayOf(0x30, 0x00)
            val selectSeg = byteArrayOf(0x10, 0x00)
            val selectWrite1Erase = byteArrayOf(0x53)
            val selectWrite1NoErase = byteArrayOf(0x1a)
            val selectWrite8Erase = byteArrayOf(0x54)
            val selectWrite8NoErase = byteArrayOf(0x1b)
            cardId = nfcA.transceive(selectId);
            allByte = nfcA.transceive(selectAll);
            response = nfcA.transceive(select16);
        }catch (e: Exception){
            Log.e(TAG, "read: io exception $e", )
        }finally {
            try {
                onParsed(NfcATagInfo(
                    "ready",
                    cardId.toHexString(),
                    nfcA.atqa,
                    nfcA.atqa.toHexString(),
                    nfcA.atqa.decodeToString(),
                    nfcA.sak,
                    nfcA.maxTransceiveLength,
                    nfcA.timeout,
                    allByte,
                    response
                ))
                nfcA.close()
            }catch (e: Exception){
                Log.e(TAG, "read: close exception $e", )
            }
        }
    }
    @OptIn(ExperimentalStdlibApi::class)
    fun get(tag: Tag, cmd: ByteArray, onResponse: (ByteArray)->Unit){
        val nfcA = NfcA.get(tag)
        var response: ByteArray = byteArrayOf()
        try {
            nfcA.connect()
            response = nfcA.transceive(cmd)
        }catch (e: Exception){
            Log.e(TAG, "get: exception $e", )
        }finally {
            onResponse(response)
            Log.i(TAG, "get: send cmd ${cmd.toHexString()} and response ${response.toHexString()}")
            try {
                nfcA.close()
            }catch (e: Exception) {
                Log.e(TAG, "get: exception when close $e",)
            }
        }
    }
}
data class NfcATagInfo(
    val status: String = "-",
    val id: String = "",
    val atqa: ByteArray = byteArrayOf(),
    val atqaHexString: String = "",
    val atqaString: String = "",
    val sak: Short = 0,
    val maxTransceiveLength: Int = 0,
    val timeout: Int = 0,
    val content: ByteArray = byteArrayOf(),
    var testResponse: ByteArray = byteArrayOf(),
)