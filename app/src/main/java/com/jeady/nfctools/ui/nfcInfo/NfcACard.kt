package com.jeady.nfctools.ui.nfcInfo

import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.jeady.nfctools.jnfc.NfcATag
import com.jeady.nfctools.jnfc.NfcATagInfo
import com.jeady.nfctools.makeToast
import com.jeady.nfctools.tagDetected
import com.jeady.nfctools.ui.jcomps.ButtonText
import com.jeady.nfctools.ui.jcomps.TextBlock
import com.jeady.nfctools.ui.jcomps.TitleSmall
import com.jeady.nfctools.ui.jcomps.showToast


@OptIn(ExperimentalStdlibApi::class)
@Composable
fun NfcACard(visible: Boolean=false) {
    val context = LocalContext.current
    val TAG = "[TAG_NFCA_CARD"
    var tagInfo by remember{ mutableStateOf(NfcATagInfo()) }
    AnimatedVisibility(visible) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                TitleSmall("id : ${tagInfo.id}")
                TitleSmall("atqa : ${tagInfo.atqaHexString}")
                TitleSmall("sak : ${tagInfo.sak}")
                TitleSmall("timeout : ${tagInfo.timeout}")
                TitleSmall("maxTrans : ${tagInfo.maxTransceiveLength}")
                TitleSmall("content : ${tagInfo.content.toHexString()}")
            }
            item{
                Divider()
            }
            item {
                var cmd by remember{ mutableStateOf("") }
                Row {
                    var cmdByteArr = byteArrayOf()
                    fun sendCmd(){
                        tagDetected?.let {tag->
                            NfcATag.get(tag, cmdByteArr){response->
                                tagInfo = tagInfo.copy(testResponse = response)
                            }
                        }
                    }
                    OutlinedTextField(value = cmd,
                        onValueChange = {value->
                            if(value.endsWith('\n')){
                                sendCmd()
                            }else{
                                cmd = value
                            }
                        },
                        placeholder = { Text("请输入十六进制指令") },
                        isError = try {
                            cmdByteArr = cmd.hexToByteArray()
                            false
                        } catch (ex: NumberFormatException) {
                            true
                        },
                        singleLine = true
                    )
                    ButtonText(text = "发送") {
                        sendCmd()
                    }
                }
            }
            item{
                TitleSmall("response : ${tagInfo.testResponse.toHexString()}")
            }
        }
    }
    LaunchedEffect(tagDetected) {
        tagDetected?.let {
            NfcATag.read(it) {info->
                showToast(context, "NfcA Done")
                tagInfo = info ?: NfcATagInfo()
            }
        }
    }
}