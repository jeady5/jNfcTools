package com.jeady.nfctools.ui.nfcInfo

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jeady.nfctools.jnfc.MifareTag
import com.jeady.nfctools.jnfc.MifareTagInfo
import com.jeady.nfctools.tagDetected
import com.jeady.nfctools.ui.jcomps.TitleSmall
import com.jeady.nfctools.ui.jcomps.TitleSmaller
import com.jeady.nfctools.ui.jcomps.randomColor
import com.jeady.nfctools.ui.jcomps.showToast
import com.jeady.nfctools.ui.theme.colors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun MifareCard(visible: Boolean=false) {
    val TAG = "[MifareCard] "
    val context = LocalContext.current
    var tagInfo by remember{ mutableStateOf(MifareTagInfo()) }
    var delayToShow by remember(visible) { mutableStateOf(false) }
    if(visible) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TitleSmaller("Sectors: ${tagInfo.sectorCount}; Blocks: ${tagInfo.blockCount}; Bytes: ${tagInfo.size}")
            val cols = 16
            if(delayToShow) {
                LazyColumn(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val bytesInSector = tagInfo.bytesInBlock * tagInfo.blocksInSector
                    tagInfo.blocks.forEachIndexed{ idx,block ->
                        item(key = "s-$idx") {
                            val sectorIdx = idx / tagInfo.blocksInSector
                            val blockIdx = idx%tagInfo.blocksInSector
                            val currentAlpha = 1-((blockIdx+1).toFloat()/(tagInfo.blocksInSector+1))
                            val sectorByteBegin = sectorIdx * bytesInSector
                            Column{
                                if(blockIdx==0) {
                                    TitleSmall(
                                        "sector $sectorIdx; blocks $idx-${idx+tagInfo.blocksInSector}; bytes: $sectorByteBegin-${sectorByteBegin+bytesInSector-1}",
                                        Modifier
                                            .padding(top = 5.dp)
                                            .background(colors[sectorIdx])
                                    )
                                }
                                LazyVerticalGrid(columns = GridCells.Fixed(cols),
                                    Modifier
                                        .heightIn(0.dp, 1000.dp)
                                        .background(colors[sectorIdx].copy(currentAlpha))) {
                                    itemsIndexed(block.toList(), key={idx,_->sectorByteBegin+blockIdx*tagInfo.bytesInBlock+idx}){ idxByte, bItem ->
                                        val byteSectorIdx = blockIdx*tagInfo.bytesInBlock+idxByte
                                        Column(Modifier.clickable {
                                            showToast(context, "byte $byteSectorIdx/${sectorByteBegin + byteSectorIdx}")
                                            },
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                bItem.toHexString(),
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                "${bItem.toInt().toChar()}",
                                                fontFamily = FontFamily.Monospace,
                                                color = Color.DarkGray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }else{
                Text("扇区数据加载中")
            }
        }
        val coroutine = rememberCoroutineScope()
        LaunchedEffect(true){
            coroutine.launch {
                Log.e(TAG, "MifareCard: compose", )
                delay(1000)
                delayToShow = true
            }
        }
    }
    LaunchedEffect(tagDetected){
        Log.w(TAG, "MifareCard: ", )
        tagDetected?.let {
            MifareTag.read(it) {
                Log.d(TAG, "handleTag: MifareClassic parse $it")
                tagInfo = it
                showToast(context, "Mifare Done")
            }
        }
    }
}