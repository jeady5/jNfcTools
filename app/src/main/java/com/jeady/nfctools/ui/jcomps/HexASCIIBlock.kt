package com.jeady.nfctools.ui.jcomps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.jeady.nfctools.ui.theme.colors

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun HexASCIIBlock(byte: Byte, modifier: Modifier=Modifier, onClick: ()->Unit){
    Column(
        modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            byte.toHexString(),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
        )
        Text(
            "${byte.toInt().toChar()}",
            fontFamily = FontFamily.Monospace,
            color = Color.DarkGray
        )
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HexASCIIBlock(bytes: ByteArray, modifier: Modifier=Modifier, cols: Int=16, onClick: (Int)->Unit){
    FlowRow(modifier, horizontalArrangement = Arrangement.SpaceEvenly, maxItemsInEachRow = cols) {
        bytes.forEachIndexed{idx, byte->
            HexASCIIBlock(byte, Modifier.background(colors[idx/cols])) {
                onClick(idx)
            }
        }
    }
}