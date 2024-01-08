package com.jeady.nfctools.ui.jcomps

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextBlock(text: String, modifier: Modifier=Modifier) {
    Surface(modifier.padding(10.dp, 5.dp, 5.dp, 5.dp)) {
        Text(text = text, fontFamily = FontFamily.SansSerif)
    }
}

@Composable
fun TextWideBlock(text: String, modifier: Modifier=Modifier) {
    Surface(modifier.padding(10.dp, 5.dp, 5.dp, 5.dp)) {
        Text(text = text, fontFamily = FontFamily.Monospace, fontSize = 15.sp)
    }
}