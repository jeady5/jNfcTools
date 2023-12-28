package com.jeady.nfctools

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.NfcAdapter.FLAG_READER_NFC_A
import android.nfc.NfcAdapter.ReaderCallback
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.nfc.tech.NfcA
import android.nfc.tech.NfcB
import android.nfc.tech.NfcBarcode
import android.nfc.tech.NfcF
import android.nfc.tech.NfcV
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jeady.nfctools.ui.jcomps.ButtonText
import com.jeady.nfctools.ui.jcomps.TextBlock
import com.jeady.nfctools.ui.jcomps.TitleBig
import com.jeady.nfctools.ui.jcomps.TitleSmall
import com.jeady.nfctools.ui.theme.NFCToolsTheme


class NFCReaderActivity: ComponentActivity(), ReaderCallback {
    val TAG = "[JNFCReader]"
    private lateinit var nfcAdapter: NfcAdapter

    // variable used to filter action
    private val ndefFilter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply { addDataType("*/*") }
    private val techFilter = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED).apply {
        addCategory("android.intent.category.DEFAULT")
    }
    private val tagFilter = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
    private val intentFiltersArray = arrayOf(ndefFilter, techFilter, tagFilter)
    private val techListsArray = arrayOf(
        arrayOf<String>(MifareClassic::class.java.name),
        arrayOf<String>(NdefFormatable::class.java.name),
        arrayOf<String>(NfcF::class.java.name),
        arrayOf<String>(NfcA::class.java.name),
        arrayOf<String>(NfcB::class.java.name),
        arrayOf<String>(NfcV::class.java.name),
        arrayOf<String>(IsoDep::class.java.name),
        arrayOf<String>(MifareUltralight::class.java.name),
        arrayOf<String>(NfcBarcode::class.java.name),
        arrayOf<String>(Ndef::class.java.name),
    )

    // variable will shown on screen
    private var action: String? by mutableStateOf(null)
    private var cardId by mutableStateOf("-")
    private var techList by mutableStateOf(listOf("-"))

    // variable global control
    private enum class ReadTagMode{
        /**
         * use enableReaderMode to read card
         * @see enableForegroundReader
         * @see disableReader
         */
        Reader,
        /**
         * use enableForegroundDispatch to read card
         * @see enableForegroundDispatcher
         * @see disableForegroundDispatcher
         */
        Dispatcher
    }
    private var readTagMode: ReadTagMode by mutableStateOf(ReadTagMode.Reader)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate: $this $intent")
        handleIntent(intent)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        setContent{
            NFCToolsTheme{
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    TitleBig(stringResource(R.string.mode_read), Modifier.align(Alignment.TopCenter))
                    ButtonText("${stringResource(R.string.change_read_method)}\n$readTagMode", Modifier.align(Alignment.TopEnd)) {
                        if(readTagMode == ReadTagMode.Reader){
                            readTagMode = ReadTagMode.Dispatcher
                            disableReader()
                            enableForegroundDispatcher()
                        }else if(readTagMode == ReadTagMode.Dispatcher){
                            readTagMode = ReadTagMode.Reader
                            disableForegroundDispatcher()
                            enableForegroundReader()
                        }
                        makeToast(getString(R.string.change_finish))
                    }
                    Surface(shadowElevation = 2.dp) {
                        Column(
                            Modifier
                                .fillMaxWidth(0.9f)
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            TitleSmall("Action:")
                            TextBlock(action?:"-")

                            TitleSmall("TagId:")
                            TextBlock(cardId)

                            TitleSmall("Tech list:")
                            Column {
                                techList.forEach {
                                    TextBlock(it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    public override fun onPause() {
        Log.i(TAG, "onPause")
        super.onPause()
        if(readTagMode == ReadTagMode.Reader){
            disableReader()
        }else if(readTagMode == ReadTagMode.Dispatcher){
            disableForegroundDispatcher()
        }
    }

    public override fun onResume() {
        Log.i(TAG, "onResume:")
        super.onResume()
        if(readTagMode == ReadTagMode.Reader){
            enableForegroundReader()
        }else if(readTagMode == ReadTagMode.Dispatcher){
            enableForegroundDispatcher()
        }
    }

    public override fun onNewIntent(intent: Intent) {
        Log.i(TAG, "onNewIntent: action ${intent.action}")
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun handleTag(tag: Tag?){
        tag?.run {
            Log.i(TAG, "handleTag: ${tag.id.toHexString()}")
            cardId = id.toHexString()
            this@NFCReaderActivity.techList = techList.toList()
            Log.i(TAG, "handleTag: id $cardId")
            Log.i(TAG, "handleTag: tech list ${techList.toList()}")
        }?:{
            action = "-"
            cardId = "-"
            techList = listOf("-")
        }
    }

    private fun enableForegroundReader(){
        Log.i(TAG, "enableForegroundReader: ")
        nfcAdapter.enableReaderMode(this, this, FLAG_READER_NFC_A,null)
    }
    private fun disableReader(){
        Log.i(TAG, "disableReader: ")
        nfcAdapter.disableReaderMode(this)
    }
    override fun onTagDiscovered(tag: Tag?) {
        Log.i(TAG, "onTagDiscovered: $tag")
        action = "-"
        handleTag(tag)
    }

    private fun enableForegroundDispatcher(){
        Log.i(TAG, "enableForegroundDispatcher: ")
        val intent = Intent(this, NFCReaderActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray)
    }
    private fun disableForegroundDispatcher(){
        Log.i(TAG, "disableForegroundDispatcher: ")
        nfcAdapter.disableForegroundDispatch(this)
    }
    private fun handleIntent(intent: Intent){
        Log.i(TAG, "handleIntent: $intent")
        action = intent.action
        action?.let{
            handleTag(intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java))
        }
    }
    private fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}