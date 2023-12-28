package com.jeady.nfctools.ui.jcomps

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun TitleBig(text: String, modifier: Modifier=Modifier) {
    Text(text, modifier, fontWeight = FontWeight.Bold, fontSize = 30.sp, fontFamily = FontFamily.SansSerif)
}
@Composable
fun TitleSmall(text: String) {
    Text(text, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
}