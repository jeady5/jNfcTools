package com.jeady.nfctools.ui.nfcInfo

import android.nfc.Tag
import android.os.Bundle
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.jeady.nfctools.jnfc.NfcATag
import com.jeady.nfctools.jnfc.NfcATagInfo
import com.jeady.nfctools.tagDetected
import com.jeady.nfctools.ui.jcomps.TextBlock
import com.jeady.nfctools.ui.jcomps.TitleSmall
import com.jeady.nfctools.ui.jcomps.showToast

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun NfcACard(visible: Boolean=false) {
    val context = LocalContext.current
    var tagInfo by remember{ mutableStateOf(NfcATagInfo()) }
    AnimatedVisibility(visible) {
        LazyColumn {
            item {
                TitleSmall("NfcA : ${tagInfo.atqaHexString}")
                TitleSmall("NfcA : ${tagInfo.timeout}")
                TitleSmall("NfcA : ${tagInfo.maxTransceiveLength}")
                TitleSmall("NfcA : ${tagInfo.content.toHexString()}")
            }
        }
    }
    LaunchedEffect(context){
        tagDetected?.let {
            NfcATag.read(it){
                tagInfo = it
                showToast(context, "NfcA Done")
            }
        }
    }
}