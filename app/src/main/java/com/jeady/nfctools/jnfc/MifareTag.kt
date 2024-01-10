package com.jeady.nfctools.jnfc

import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.util.Log
import com.jeady.nfctools.ui.nfcInfo.closeTag
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException


object MifareTag {
    private const val TAG = "[TAG_Mifare]"
    fun read(tag: Tag, onParsed: (MifareTagInfo)->Unit){
        var status = "ready"
        val mifareClassic = MifareClassic.get(tag)
        val typeString = when(mifareClassic.type){
            MifareClassic.TYPE_CLASSIC->"TYPE_CLASSIC"
            MifareClassic.TYPE_PLUS->"TYPE_PLUS";
            MifareClassic.TYPE_PRO->"TYPE_PRO";
            MifareClassic.TYPE_UNKNOWN->"TYPE_UNKNOWN";
            else->"-"
        }
//        resBundle.putString("typeString", typeString)
//        Log.d(TAG, "handleTag: mifare classic, $resBundle")
        runBlocking {
            var bytesBlock = 0
            var blocksSector = 0
            var maxTransceiveLength = 0
            var timeout = 0
            val sectors = mutableListOf<ByteArray>()
            launch {
                try {
                    mifareClassic.connect()
                    maxTransceiveLength = mifareClassic.maxTransceiveLength
                    timeout = mifareClassic.timeout
                    for (sectorIdx in 0 until mifareClassic.sectorCount) {
                        // try authenticate a sector with default key A and default key B.
                        if (mifareClassic.authenticateSectorWithKeyA(
                                sectorIdx,
                                MifareClassic.KEY_DEFAULT
                            ) ||
                            mifareClassic.authenticateSectorWithKeyB(
                                sectorIdx,
                                MifareClassic.KEY_DEFAULT
                            )
                        ) {
                            blocksSector = mifareClassic.getBlockCountInSector(sectorIdx)
                            val blockIndex = mifareClassic.sectorToBlock(sectorIdx)
                            for (i in 0 until blocksSector) {
                                val data = mifareClassic.readBlock(blockIndex + i)
                                bytesBlock = data.size
                                sectors.add(data)
                            }
                        } else {
                            Log.w(TAG, "read: verify fail")
                            break
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "handleTag: Exception - $e")
                    status = "error"
                } finally {
                    closeTag(mifareClassic)
                    onParsed(
                        MifareTagInfo(
                            status,
                            mifareClassic.blockCount,
                            mifareClassic.sectorCount,
                            blocksSector,
                            bytesBlock,
                            maxTransceiveLength,
                            mifareClassic.size,
                            timeout,
                            mifareClassic.type,
                            typeString,
                            sectors
                        )
                    )
                }
            }
        }
    }
}

data class MifareTagInfo(
    val status: String = "ready", // reading ready
    val blockCount: Int = 0,
    val sectorCount: Int = 0,
    val blocksInSector: Int = 0,
    val bytesInBlock: Int = 0,
    val maxTransceiveLength: Int = 0,
    val size: Int = 0,
    val timeout: Int = 0,
    val type: Int = 0,
    val typeString: String="",
    val blocks: List<ByteArray> = listOf()
)