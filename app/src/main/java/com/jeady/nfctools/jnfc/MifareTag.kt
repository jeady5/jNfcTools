package com.jeady.nfctools.jnfc

import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.runBlocking
import java.io.IOException


object MifareTag {
    @OptIn(ExperimentalStdlibApi::class)
    fun read(tag: Tag, onParsed: (Bundle)->Unit){
        val TAG = "[TAG_Mifare]"
        val resBundle = Bundle()
        val mifareClassic = MifareClassic.get(tag)
        resBundle.putInt("blockCount", mifareClassic.blockCount)
        resBundle.putInt("sectorCount", mifareClassic.sectorCount)
        resBundle.putInt("maxTransceiveLength", mifareClassic.maxTransceiveLength)
        resBundle.putInt("size", mifareClassic.size)
        resBundle.putInt("timeout", mifareClassic.timeout)
        resBundle.putInt("type", mifareClassic.type)
        val typeString = when(mifareClassic.type){
            MifareClassic.TYPE_CLASSIC->"TYPE_CLASSIC"
            MifareClassic.TYPE_PLUS->"TYPE_PLUS";
            MifareClassic.TYPE_PRO->"TYPE_PRO";
            MifareClassic.TYPE_UNKNOWN->"TYPE_UNKNOWN";
            else->"-"
        }
        resBundle.putString("typeString", typeString)
        Log.d(TAG, "handleTag: mifare classic, $resBundle")
        mifareClassic.connect()
        runBlocking {
            try{
                val sectors = mutableListOf<String>()
                for (sectorIdx in 0 until mifareClassic.sectorCount) {
                    // try authenticate a sector with default key A and default key B.
                    if(mifareClassic.authenticateSectorWithKeyA(sectorIdx, MifareClassic.KEY_DEFAULT) ||
                        mifareClassic.authenticateSectorWithKeyB(sectorIdx, MifareClassic.KEY_DEFAULT)
                    ){
                        val blockCount = mifareClassic.getBlockCountInSector(sectorIdx)
                        val blockIndex = mifareClassic.sectorToBlock(sectorIdx)
                        val sectorData = mutableListOf<Byte>()
                        for (i in 0 until blockCount) {
                            val data = mifareClassic.readBlock(blockIndex+i)
                            sectorData.addAll(data.toList())
                        }
                        sectors.add(sectorData.toByteArray().toHexString())
                    }else{
                        Log.w(TAG, "read: 验证失败")
                        break
                    }
                }
                resBundle.putStringArray("sectors", sectors.toTypedArray())
                onParsed(resBundle)
            }catch (ex: IOException){
                Log.e(TAG, "handleTag: exception - $ex")
            }finally {
                Log.i(TAG, "handleTag: close mifareClassic")
                mifareClassic.close()
            }
        }
    }
//    fun readTagClassic(tag: Tag?): String? {
//        var auth = false
//        val mfc = MifareClassic.get(tag)
//        // 读取TAG
//        try {
//            var metaInfo = ""
//            val type = mfc!!.type // 获取TAG的类型
//            val sectorCount = mfc.sectorCount // 获取TAG中包含的扇区数
//            var typeS = ""
//            when (type) {
//                MifareClassic.TYPE_CLASSIC -> typeS = "TYPE_CLASSIC"
//                MifareClassic.TYPE_PLUS -> typeS = "TYPE_PLUS"
//                MifareClassic.TYPE_PRO -> typeS = "TYPE_PRO"
//                MifareClassic.TYPE_UNKNOWN -> typeS = "TYPE_UNKNOWN"
//            }
//            metaInfo += """
//            卡片类型：$typeS
//            共${sectorCount}个扇区
//            共${mfc.blockCount}个块
//            存储空间: ${mfc.size}B
//
//            """.trimIndent()
//            for (j in 0 until sectorCount) {
//                // Authenticate a sector with key A.
//                auth = mfc.authenticateSectorWithKeyA(
//                    j,
//                    MifareClassic.KEY_DEFAULT
//                )
//                var bCount: Int
//                var bIndex: Int
//                if (auth) {
//                    metaInfo += "Sector $j:验证成功\n"
//                    // 读取扇区中的块
//                    bCount = mfc.getBlockCountInSector(j)
//                    bIndex = mfc.sectorToBlock(j)
//                    for (i in 0 until bCount) {
//                        val data = mfc.readBlock(bIndex)
//                        metaInfo += ("Block " + bIndex + " : "
//                                + ByteArrayToHexString(data)) + "\n"
//                        bIndex++
//                    }
//                } else {
//                    metaInfo += "Sector $j:验证失败\n"
//                }
//            }
//            return metaInfo
//        } catch (e: Exception) {
//            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
//            e.printStackTrace()
//        } finally {
//            if (mfc != null) {
//                try {
//                    mfc.close()
//                } catch (e: IOException) {
//                    Toast.makeText(this, e.message, Toast.LENGTH_LONG)
//                        .show()
//                }
//            }
//        }
//        return null
//    }

}