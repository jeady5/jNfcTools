package com.jeady.nfctools.ui.jcomps

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.random.Random
import kotlin.random.nextUInt

@Composable
fun ButtonBlock(text: String, modifier: Modifier=Modifier, onClick: ()->Unit){
    Surface(modifier.clickable{ onClick() }, shadowElevation = 3.dp, color = Color(0xffd0d0f0)) {
        Box(contentAlignment = Alignment.Center){
            Text(text)
        }
    }
}

fun randomColor(alpha: Int=255): Color{
    return Color(Random.nextInt(0, 256), Random.nextInt(0, 256), Random.nextInt(0, 256), alpha)
}