package com.jeady.nfctools.ui.jcomps

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * @param monoSpace
 * true: 每列均等宽度
 * false: value 列占最大宽度
 */
@Composable
fun TableKeyValue(data: List<Pair<String, *>>,
                  modifier: Modifier=Modifier,
                  monoSpace: Boolean=true,
){
    Surface(shadowElevation = 1.dp) {
        Column(modifier) {
            data.forEach{info->
                Row(horizontalArrangement = Arrangement.SpaceEvenly){
                    TextKey(info.first, if(monoSpace) Modifier.weight(1f) else Modifier)
                    when(info.second){
                        is ByteArray->HexASCIIBlock(info.second as ByteArray, Modifier.weight(1f)) {}
                        else->TextValue(info.second.toString(), Modifier.weight(1f))
                    }
                }
                Divider()
            }
        }
    }
}