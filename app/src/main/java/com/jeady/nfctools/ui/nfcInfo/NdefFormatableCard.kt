package com.jeady.nfctools.ui.nfcInfo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.jeady.nfctools.jnfc.NdefFormatableTag
import com.jeady.nfctools.tagDetected
import com.jeady.nfctools.techListState
import com.jeady.nfctools.ui.jcomps.ButtonText
import com.jeady.nfctools.ui.jcomps.TextTipBlock
import com.jeady.nfctools.ui.jcomps.showToast

@Composable
fun NdefFormatableCard(visible: Boolean, myIndex: Int=-1) {
    val context = LocalContext.current
    AnimatedVisibility(visible) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            var textInput by remember{ mutableStateOf("") }
            OutlinedTextField(value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("格式刷的同时要写入的文本")}
            )
            ButtonText("格式化") {
                tagDetected?.let {tag->
                    NdefFormatableTag.format(tag, textInput){success->
                        showToast(context, if(success) "成功" else "失败")
                    }
                }
            }
            TextTipBlock("将NdefFormatable格式转为Ndef格式。")
            Divider()
//            ButtonText("格式化为只读") {
//                tagDetected?.let {tag->
//                    NdefFormatableTag.format(tag, textInput, true){success->
//                        showToast(context, if(success) "成功2" else "失败2")
//                    }
//                }
//            }
        }
    }
    LaunchedEffect(context){
        updateState(myIndex, "ready")
    }
}