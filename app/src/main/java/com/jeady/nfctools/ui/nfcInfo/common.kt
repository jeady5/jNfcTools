package com.jeady.nfctools.ui.nfcInfo

import android.nfc.tech.TagTechnology
import android.util.Log
import com.jeady.nfctools.techListState

private const val TAG = "[TAG_CARD_COMMON]"
/**
 * update local tag read status
 */
fun updateState(index: Int, state: String?){
    if(index!=-1){
        val status = techListState[index]
        techListState[index] = status.first to state.toString()
    }
}

fun closeTag(tag: TagTechnology){
    try {
        tag.close()
    }catch (e: Exception){
        Log.e(TAG, "closeTag: close exception $tag $e")
    }
}