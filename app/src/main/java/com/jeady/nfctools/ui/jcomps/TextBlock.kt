package com.jeady.nfctools.ui.jcomps

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TextBlock(text: String, modifier: Modifier=Modifier) {
    Surface(modifier.padding(10.dp, 5.dp, 5.dp, 5.dp)) {
        Text(text = text)
    }
}