package com.jeady.nfctools.jnfc

import android.nfc.Tag
import android.nfc.tech.NfcA
import android.os.Bundle
import android.util.Log
import java.io.IOException

object NfcATag {
    private const val TAG = "[TAG_NFCA]"
    @OptIn(ExperimentalStdlibApi::class)
    fun read(tag: Tag, onParsed: (NfcATagInfo)->Unit){
        val nfcA = NfcA.get(tag)
        try {
            nfcA.connect()
            val select = byteArrayOf(0x30, 0x05)
            val response = nfcA.transceive(select);
            onParsed(NfcATagInfo(
                nfcA.atqa,
                nfcA.atqa.toHexString(),
                nfcA.atqa.decodeToString(),
                nfcA.maxTransceiveLength,
                nfcA.timeout,
                response
            ))
        }catch (e: IOException){
            Log.e(TAG, "read: $e", )
        }finally {
            nfcA.close()
        }
    }
}
data class NfcATagInfo(
    val atqa: ByteArray = byteArrayOf(),
    val atqaHexString: String = "",
    val atqaString: String = "",
    val maxTransceiveLength: Int = 0,
    val timeout: Int = 0,
    val content: ByteArray = byteArrayOf()
)