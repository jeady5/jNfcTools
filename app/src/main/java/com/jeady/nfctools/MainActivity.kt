package com.jeady.nfctools

import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jeady.nfctools.ui.jcomps.ButtonBlock
import com.jeady.nfctools.ui.theme.NFCToolsTheme

class MainActivity : ComponentActivity() {
    lateinit var nfcAdapter: NfcAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcAdapter?:{
            makeToast(this,"不支持NFC")
        }
        setContent {
            NFCToolsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Actions()
                }
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun Actions() {
        val context = LocalContext.current
        Box(contentAlignment = Alignment.Center){
            val margin = 10.dp
            FlowRow(Modifier.padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(margin),
                verticalArrangement = Arrangement.spacedBy(margin),
                maxItemsInEachRow = 2
            ) {
                ButtonBlock(stringResource(R.string.read_text),
                    Modifier
                        .weight(1f)
                        .height(50.dp)){
                    startActivity(Intent(this@MainActivity, NFCReaderActivity::class.java))
                }
                ButtonBlock(stringResource(R.string.more_text),
                    Modifier
                        .weight(1f)
                        .height(50.dp)){
                    Toast.makeText(context, getString(R.string.developing_text), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

fun makeToast(context: Context, text: String) {
    Log.d("[Tool] ", "makeToast() called with: context = $context, text = $text")
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}