package com.jeady.nfctools.ui.jcomps

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ButtonPlain(text: String, modifier: Modifier=Modifier, onClick: ()->Unit) {
    OutlinedButton(onClick = { onClick() }, modifier) {
        Text(text, textAlign = TextAlign.Center)
    }
}