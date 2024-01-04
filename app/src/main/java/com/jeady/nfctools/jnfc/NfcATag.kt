package com.jeady.nfctools.jnfc

import android.nfc.Tag
import android.nfc.tech.NfcA
import android.os.Bundle
import android.util.Log

object NfcATag {
    const val TAG = "[TAG_NFCA]"
    @OptIn(ExperimentalStdlibApi::class)
    fun read(tag: Tag, onParsed: (Bundle)->Unit){
        val retBundle = Bundle()
        val nfcA = NfcA.get(tag)
        Log.i(TAG, "read: ${nfcA.tag} ${nfcA.atqa.toHexString()} ${nfcA.sak} ${nfcA.transceive("".toByteArray())}")
        retBundle.putByteArray("atqa", nfcA.atqa)
        retBundle.putString("atqaHexString", nfcA.atqa.toHexString())
        retBundle.putString("atqaString", nfcA.atqa.decodeToString())
        retBundle.putInt("maxTransceiveLength", nfcA.maxTransceiveLength)
        retBundle.putInt("timeout", nfcA.timeout)
        onParsed(retBundle)
    }
}