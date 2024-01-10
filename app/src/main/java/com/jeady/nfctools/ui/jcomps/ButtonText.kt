package com.jeady.nfctools.ui.jcomps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ButtonText(text: String, modifier: Modifier=Modifier, onClick: ()->Unit) {
    val interactionSource = MutableInteractionSource()
    Box(modifier
        .background(Color(0xFF8BC34A), RoundedCornerShape(5.dp))
        .padding(20.dp, 10.dp)
        .clickable{ onClick() }
    ) {
        Text(text = text, textAlign = TextAlign.Center)
    }
}