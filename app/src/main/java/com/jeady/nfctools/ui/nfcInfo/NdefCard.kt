package com.jeady.nfctools.ui.nfcInfo

import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.jeady.nfctools.jnfc.NdefTag
import com.jeady.nfctools.tagDetected
import com.jeady.nfctools.ui.jcomps.TextBlock
import com.jeady.nfctools.ui.jcomps.TitleSmall
import com.jeady.nfctools.ui.jcomps.showToast

private var ndefRecords by mutableStateOf<List<Bundle>>(listOf())
private const val TAG = "[NdefCard]"
@Composable
fun NdefCard(visible: Boolean=false) {
    val context = LocalContext.current
    AnimatedVisibility(visible) {
        LazyColumn {
            item {
                TitleSmall("Ndef records:")
            }
            items(ndefRecords) {
                TextBlock(it.getString("payloadString", "-"))
            }
        }
    }
    LaunchedEffect(tagDetected){
        Log.e(TAG, "NdefCard: NDEF $visible")
        tagDetected?.let {
            try {
                NdefTag.read(it) { info ->
                    ndefRecords =
                        info.getParcelableArray("records", Bundle::class.java)?.toList() ?: listOf()
                }
            }catch (securityEx: SecurityException){
                Log.e(TAG, "NdefCard: card removed")
            }finally {
                showToast(context, "Ndef Done")
            }
        }
    }
}