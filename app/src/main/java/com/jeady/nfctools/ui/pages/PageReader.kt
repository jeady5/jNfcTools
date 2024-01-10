package com.jeady.nfctools.ui.pages

import android.nfc.tech.MifareClassic
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.nfc.tech.NfcA
import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeady.nfctools.NFCReaderActivity
import com.jeady.nfctools.R
import com.jeady.nfctools.common.EventData
import com.jeady.nfctools.lastTagDetected
import com.jeady.nfctools.makeToast
import com.jeady.nfctools.tagDetected
import com.jeady.nfctools.techListState
import com.jeady.nfctools.ui.jcomps.ButtonPlain
import com.jeady.nfctools.ui.jcomps.ButtonText
import com.jeady.nfctools.ui.jcomps.TextBigTipBlock
import com.jeady.nfctools.ui.jcomps.TextBlock
import com.jeady.nfctools.ui.jcomps.TextTipBlock
import com.jeady.nfctools.ui.jcomps.TitleBig
import com.jeady.nfctools.ui.nfcInfo.MifareCard
import com.jeady.nfctools.ui.nfcInfo.NdefCard
import com.jeady.nfctools.ui.nfcInfo.NdefFormatableCard
import com.jeady.nfctools.ui.nfcInfo.NfcACard


var readWriteMode by mutableStateOf(ReadWriteMode.ReadOnly)
var readTagMode: ReadTagMode by mutableStateOf(ReadTagMode.Reader)
@OptIn(ExperimentalStdlibApi::class)
@Composable
fun PageReader(onEvent: (EventData)->Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CardInfo()
        TitleBig(
            tagDetected?.id?.toHexString()?:"-",
            Modifier.align(Alignment.TopCenter)
        )
        ChangeWary(onEvent)
        ChangeReadWrite()
    }
}

@Composable
private fun CardInfo() {
    val TAG = "[CardInfo]"
    Surface(shadowElevation = 2.dp) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(10.dp, 50.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.Start
        ){
            var currentTabIdx by remember(lastTagDetected) {
                mutableIntStateOf(0)
            }
            LazyRow(
                Modifier
                    .fillMaxWidth()
                    .background(Color(0x30709020))
                    .padding(5.dp, 0.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                itemsIndexed(techListState){ idx, info->
                    val isCurrent = idx==currentTabIdx
                    Text(
                        buildAnnotatedString { 
                            append(info.first.split('.').last())
                            withStyle(SpanStyle(fontSize = 12.sp)){
                                append("\n${info.second}")
                            }
                        },
                        Modifier
                            .background(if (isCurrent) Color(0xff709020) else Color(0xfff0f0f0))
                            .clickable {
                                currentTabIdx = idx
                            }
                            .padding(20.dp, 10.dp),
                        fontSize = if(isCurrent) 20.sp else 18.sp,
                        fontWeight = if(isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                        color = if(isCurrent) Color.White else Color.DarkGray,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Column {
                techListState.forEachIndexed { idx, info ->
                    val showCurrent by remember(currentTabIdx) { mutableStateOf(idx == currentTabIdx) }
                    when (info.first) {
                        Ndef::class.java.name -> {
                            NdefCard(showCurrent, idx)
                        }

                        NdefFormatable::class.java.name -> {
                            NdefFormatableCard(showCurrent, idx)
                        }

                        NfcA::class.java.name -> {
                            NfcACard(showCurrent, idx)
                        }

                        MifareClassic::class.java.name -> {
                            MifareCard(showCurrent, idx)
                        }
                    }
                }
                if(techListState.isEmpty()){
                    DetectCardTip()
                }
            }
        }
    }
}

@Composable
fun DetectCardTip(){
    TextBigTipBlock(stringResource(R.string.detect_card_text), Modifier.fillMaxSize())
}

@Composable
private fun BoxScope.ChangeWary(callback: (EventData)->Unit) {
    ButtonPlain(
        "${stringResource(R.string.change_read_method)}\n$readTagMode",
        Modifier.align(Alignment.TopEnd)
    ) {
        callback(EventData(if(readTagMode==ReadTagMode.Dispatcher) ReadTagMode.Reader else ReadTagMode.Dispatcher))
    }
}

@Composable
private fun BoxScope.ChangeReadWrite() {
    ButtonPlain(stringResource(R.string.read_or_write_text), Modifier.align(Alignment.TopStart)) {
        if (readWriteMode == ReadWriteMode.ReadOnly) {
            readWriteMode = ReadWriteMode.ReadWrite
        } else if (readWriteMode == ReadWriteMode.ReadWrite) {
            readWriteMode = ReadWriteMode.ReadOnly
        }
    }
}

enum class ReadWriteMode {
    ReadOnly, ReadWrite
}

// variable global control
enum class ReadTagMode {
    /**
     * use enableReaderMode to read card
     * @see enableForegroundReader
     * @see disableReader
     */
    Reader,

    /**
     * use enableForegroundDispatch to read card
     * @see enableForegroundDispatcher
     * @see disableForegroundDispatcher
     */
    Dispatcher
}