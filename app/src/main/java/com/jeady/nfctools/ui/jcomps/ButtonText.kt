package com.jeady.nfctools.ui.jcomps

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ButtonText(text: String, modifier: Modifier=Modifier, onClick: ()->Unit) {
    Button(onClick, modifier) {
        Text(text = text, textAlign = TextAlign.Center)
    }
}