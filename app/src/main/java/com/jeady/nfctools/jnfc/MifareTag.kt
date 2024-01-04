package com.jeady.nfctools.jnfc

import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.runBlocking
import java.io.IOException

object MifareTag {
    @OptIn(ExperimentalStdlibApi::class)
    fun read(tag: Tag, onParsed: (Bundle)->Unit){
        val TAG = "[TAG_Mifare]"
        val mifareClassic = MifareClassic.get(tag)
        Log.d(TAG, "handleTag: mifare classic, ${mifareClassic.blockCount}")
        Log.d(TAG, "handleTag: mifare classic, ${mifareClassic.sectorCount}")
        Log.d(TAG, "handleTag: mifare classic, ${mifareClassic.maxTransceiveLength}")
        Log.d(TAG, "handleTag: mifare classic, ${mifareClassic.size}")
        Log.d(TAG, "handleTag: mifare classic, ${mifareClassic.timeout}")
        Log.d(TAG, "handleTag: mifare classic, ${mifareClassic.type}")
        mifareClassic.connect()
        runBlocking {
            try{
                val res = mifareClassic.readBlock(1).toHexString()
                Log.i(TAG, "handleTag: $res")
            }catch (ex: IOException){
                Log.e(TAG, "handleTag: exception - $ex", )
            }finally {
                Log.i(TAG, "handleTag: close mifareClassic")
                mifareClassic.close()
            }
        }
    }
}