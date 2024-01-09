package com.jeady.nfctools.ui.nfcInfo

import android.os.Bundle
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.jeady.nfctools.R
import com.jeady.nfctools.ui.jcomps.DropDown
import com.jeady.nfctools.jnfc.NdefTag
import com.jeady.nfctools.jnfc.NdefTagInfo
import com.jeady.nfctools.jnfc.exception.ResultException
import com.jeady.nfctools.tagDetected
import com.jeady.nfctools.ui.jcomps.ButtonText
import com.jeady.nfctools.ui.jcomps.TextBlock
import com.jeady.nfctools.ui.jcomps.TitleSmall
import com.jeady.nfctools.ui.jcomps.showToast

@Composable
fun NdefCard(visible: Boolean=false) {
    val context = LocalContext.current
    val TAG = "[NdefCard]"
    var tagInfo by remember{ mutableStateOf(NdefTagInfo()) }
    AnimatedVisibility(visible) {
        LazyColumn {
            item{
                var contentInput by remember{ mutableStateOf("") }
                val recordTypes = listOf("text", "uri")
                var currentMode by remember { mutableStateOf(recordTypes[0]) }
                Column {
                    Row {
                        DropDown(recordTypes, currentMode) {
                            it?.let{
                                currentMode = it
                            }
                        }
                        OutlinedTextField(
                            contentInput,
                            onValueChange = { contentInput = it },
                            placeholder = { Text("要写入的数据,每行一个") }
                        )
                    }
                    ButtonText("写入") {
                        tagDetected?.let {tag->
                            when(currentMode){
                                "text"->{
                                    try{
                                        NdefTag.writeText(tag, contentInput.split("\n"))
                                    }catch (e: ResultException){
                                        Log.e(TAG, "NdefCard: get text result $e", )
                                        showToast(context,
                                            if(e.message=="success") context.getString(R.string.wrote_text)
                                            else e.message?:context.getString(R.string.write_fail_text)
                                        )
                                    }
                                }
                                "uri"->{
                                    try{
                                        NdefTag.writeUri(tag, contentInput.split("\n"))
                                    }catch (e: ResultException){
                                        Log.e(TAG, "NdefCard: get uri result $e", )
                                        showToast(context,
                                            if(e.message=="success") context.getString(R.string.wrote_text)
                                            else e.message?:context.getString(R.string.write_fail_text)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item{
                Column {
                    TextBlock("type:\t${tagInfo.ndefType}")
                    TextBlock("maxSize:\t${tagInfo.maxSize}")
                    TextBlock("canReadOnly:\t${tagInfo.canMakeReadOnly}")
                    TextBlock("writable:\t${tagInfo.writable}")
                }
            }
            item {
                TitleSmall("Ndef records:")
            }
            items(tagInfo.records) {
                TextBlock(it.getString("payloadString", "-"))
            }
        }
    }
    LaunchedEffect(tagDetected){
        tagDetected?.let {
            NdefTag.read(it) { info ->
                info?.let{
                    showToast(context, "Ndef Done")
                    tagInfo = it
                }
            }
        }
    }
}