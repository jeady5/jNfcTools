package com.jeady.nfctools.ui.jcomps

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun TextKeyValue(key: String, value: String) {
    Text(buildAnnotatedString {
        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)){
            append("$key:\t")
        }
        append(value)
    })
}
@Composable
fun TextKey(key: String, modifier: Modifier=Modifier) {
    Text(key, modifier.padding(10.dp, 10.dp, 10.dp, 5.dp), fontWeight = FontWeight.SemiBold)
}

@Composable
fun TextValue(key: String, modifier: Modifier=Modifier) {
    Text(key, modifier.padding(10.dp, 10.dp, 10.dp, 5.dp))
}