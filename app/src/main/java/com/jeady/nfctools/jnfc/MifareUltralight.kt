package com.jeady.nfctools.jnfc

import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import android.util.Log
import java.io.IOException

object MifareUltralight {
    private const val TAG = "[TAG_MifareUltralight]"
    fun read(tag: Tag, onParsed: (MifareTagInfo?)->Unit) {
        val mifareUltralight = MifareUltralight.get(tag)
        try {
            mifareUltralight.connect()
            mifareUltralight.readPages(0)
            MifareUltralightInfo(
                mifareUltralight.type,
                mifareUltralight.type.toString(),
                mifareUltralight.timeout,
                mifareUltralight.maxTransceiveLength
            )
        }catch (ex: IOException){
            Log.e(TAG, "read: io exception $ex", )
            onParsed(null)
        }
    }
}
data class MifareUltralightInfo(
    val type: Int = 0,
    val typeString: String = "",
    val timeout: Int = 0,
    val maxTransceiveLength: Int = 0,
    
)