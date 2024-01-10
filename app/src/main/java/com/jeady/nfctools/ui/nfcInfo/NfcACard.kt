package com.jeady.nfctools.ui.nfcInfo

import android.util.Log
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jeady.nfctools.R
import com.jeady.nfctools.jnfc.NfcATag
import com.jeady.nfctools.jnfc.NfcATagInfo
import com.jeady.nfctools.tagDetected
import com.jeady.nfctools.ui.jcomps.ButtonText
import com.jeady.nfctools.ui.jcomps.HexASCIIBlock
import com.jeady.nfctools.ui.jcomps.TableKeyValue
import com.jeady.nfctools.ui.jcomps.TitleSmall


@OptIn(ExperimentalStdlibApi::class)
@Composable
fun NfcACard(visible: Boolean, myIndex: Int=-1) {
    val context = LocalContext.current
    val TAG = "[TAG_NFCA_CARD]"
    var tagInfo by remember{ mutableStateOf(NfcATagInfo()) }
    AnimatedVisibility(visible) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                TableKeyValue(listOf(
                    "Id" to tagInfo.id,
                    "Atqa" to tagInfo.atqaHexString,
                    "Sak" to tagInfo.sak,
                    "Timeout" to tagInfo.timeout,
                    "MaxTrans" to tagInfo.maxTransceiveLength,
                    "Content" to tagInfo.content.toHexString()
                ))
            }
            item{
                Divider()
            }
            item {
                var cmd by remember{ mutableStateOf("") }
                Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
                    var cmdByteArr = byteArrayOf()
                    fun sendCmd(){
                        tagDetected?.let {tag->
                            NfcATag.get(tag, cmdByteArr){response->
                                Log.d(TAG, "sendCmd: get response ${response.toHexString()}")
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
                        placeholder = { Text(stringResource(R.string.input_hex_text)) },
                        isError = try {
                            cmdByteArr = cmd.hexToByteArray()
                            false
                        } catch (ex: NumberFormatException) {
                            true
                        },
                        singleLine = true
                    )
                    ButtonText(text = stringResource(R.string.send_text)) {
                        sendCmd()
                    }
                }
                if(tagInfo.testResponse.isNotEmpty()) {
                    TableKeyValue(listOf("Response" to tagInfo.testResponse), monoSpace = false)
                }
            }
        }
    }
    LaunchedEffect(tagDetected) {
        tagDetected?.let {
            NfcATag.read(it) {info->
                tagInfo = info
                updateState(myIndex, info.status)
            }
        }
    }
}