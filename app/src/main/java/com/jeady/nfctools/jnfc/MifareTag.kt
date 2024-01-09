package com.jeady.nfctools.jnfc

import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.util.Log
import kotlinx.coroutines.runBlocking
import java.io.IOException


object MifareTag {
    private const val TAG = "[TAG_Mifare]"
    fun read(tag: Tag, onParsed: (MifareTagInfo?)->Unit){
//        val resBundle = Bundle()
        val mifareClassic = MifareClassic.get(tag)
//        resBundle.putInt("blockCount", mifareClassic.blockCount)
//        resBundle.putInt("sectorCount", mifareClassic.sectorCount)
//        resBundle.putInt("maxTransceiveLength", mifareClassic.maxTransceiveLength)
//        resBundle.putInt("size", mifareClassic.size)
//        resBundle.putInt("timeout", mifareClassic.timeout)
//        resBundle.putInt("type", mifareClassic.type)
        val typeString = when(mifareClassic.type){
            MifareClassic.TYPE_CLASSIC->"TYPE_CLASSIC"
            MifareClassic.TYPE_PLUS->"TYPE_PLUS";
            MifareClassic.TYPE_PRO->"TYPE_PRO";
            MifareClassic.TYPE_UNKNOWN->"TYPE_UNKNOWN";
            else->"-"
        }
//        resBundle.putString("typeString", typeString)
//        Log.d(TAG, "handleTag: mifare classic, $resBundle")
        mifareClassic.connect()
        runBlocking {
            var bytesBlock = 0
            var blocksSector = 0
            val sectors = mutableListOf<ByteArray>()
            try{
                for (sectorIdx in 0 until mifareClassic.sectorCount) {
                    // try authenticate a sector with default key A and default key B.
                    if(mifareClassic.authenticateSectorWithKeyA(sectorIdx, MifareClassic.KEY_DEFAULT) ||
                        mifareClassic.authenticateSectorWithKeyB(sectorIdx, MifareClassic.KEY_DEFAULT)
                    ){
                        blocksSector = mifareClassic.getBlockCountInSector(sectorIdx)
                        val blockIndex = mifareClassic.sectorToBlock(sectorIdx)
                        for (i in 0 until blocksSector) {
                            val data = mifareClassic.readBlock(blockIndex+i)
                            bytesBlock = data.size
                            sectors.add(data)
                        }
                    }else{
                        Log.w(TAG, "read: 验证失败")
                        break
                    }
                }
            }catch (ex: IOException){
                Log.e(TAG, "handleTag: IOException - $ex")
                onParsed(null)
            }catch (ex: SecurityException){
                Log.e(TAG, "handleTag: SecurityException 卡片移除")
                onParsed(null)
            }finally {
                Log.i(TAG, "handleTag: close mifareClassic")
                onParsed(MifareTagInfo(
                    mifareClassic.blockCount,
                    mifareClassic.sectorCount,
                    blocksSector,
                    bytesBlock,
                    mifareClassic.maxTransceiveLength,
                    mifareClassic.size,
                    mifareClassic.timeout,
                    mifareClassic.type,
                    typeString,
                    sectors
                ))
                try {
                    mifareClassic.close()
                }catch (e: Exception){
                    Log.e(TAG, "read: close exception $e", )
                }
            }
        }
    }
}

data class MifareTagInfo(
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