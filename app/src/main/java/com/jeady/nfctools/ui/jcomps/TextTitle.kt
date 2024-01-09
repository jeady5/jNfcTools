package com.jeady.nfctools.ui.jcomps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val paddingValue = PaddingValues(0.dp, 5.dp)
@Composable
fun TitleBig(text: String, modifier: Modifier=Modifier) {
    Row(modifier.padding(paddingValue), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            fontFamily = FontFamily.SansSerif
        )
    }
}
@Composable
fun TitleSmall(text: String, modifier: Modifier=Modifier) {
    Row(modifier.padding(paddingValue), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp
        )
    }
}

@Composable
fun TitleSmaller(text: String, modifier: Modifier=Modifier) {
    Row(modifier.padding(paddingValue), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text,
            modifier.padding(10.dp, 10.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 25.sp
        )
    }
}

@Composable
fun TitleFlag(modifier: Modifier=Modifier) {
    Box(modifier.background(Color.DarkGray, CircleShape)){

    }
}