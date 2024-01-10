package com.jeady.nfctools.ui.jcomps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TextButton(text: String, onClick: ()->Unit){
    val pressed by MutableInteractionSource().collectIsPressedAsState()
    Text(text, Modifier.padding(10.dp, 5.dp)
        .background(if(pressed) Color(0xff70b050) else Color.White)
        .clickable(pressed, null){ onClick() })
}