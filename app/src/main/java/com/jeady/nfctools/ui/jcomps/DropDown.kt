package com.jeady.nfctools.ui.jcomps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DropDown(lst: List<String>, text: String="click", onSelect: (String?)->Unit) {
    var expand by remember{ mutableStateOf(false) }
    Box{
        OutlinedButton(
            onClick = {
                expand = !expand
            },
            shape = RoundedCornerShape(5.dp)
        ) {
            Text(text = text)
        }
        DropdownMenu(expanded = expand, onDismissRequest = {
            onSelect(null)
            expand = false
        },
            Modifier
                .height(200.dp)
                .background(Color.White)
        ) {
            lst.forEach{
                DropdownMenuItem(text = {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = it)
                    }
                }, onClick = {
                    onSelect(it)
                    expand = false
                }
                )
            }
        }
    }
}