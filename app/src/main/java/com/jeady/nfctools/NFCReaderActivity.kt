package com.jeady.nfctools

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.FormatException
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcAdapter.FLAG_READER_NFC_A
import android.nfc.NfcAdapter.ReaderCallback
import android.nfc.Tag
import android.nfc.TagLostException
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
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jeady.nfctools.jnfc.MifareTag
import com.jeady.nfctools.jnfc.NdefFormatableTag
import com.jeady.nfctools.ui.jcomps.ButtonText
import com.jeady.nfctools.ui.jcomps.TextBlock
import com.jeady.nfctools.ui.jcomps.TitleBig
import com.jeady.nfctools.ui.jcomps.TitleSmall
import com.jeady.nfctools.ui.theme.NFCToolsTheme
import com.jeady.nfctools.jnfc.NdefTag
import com.jeady.nfctools.jnfc.NfcATag
import kotlinx.coroutines.runBlocking
import java.io.IOException


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
    private var records by mutableStateOf<List<Bundle>>(listOf())

    private var contentInput by mutableStateOf("")

    enum class ReadWriteMode{
        Read, Write
    }
    private var readWriteMode by mutableStateOf(ReadWriteMode.Read)

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
                val context = LocalContext.current
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    TitleBig(stringResource(R.string.mode_read), Modifier.align(Alignment.TopCenter))
                    TitleBig(if(readWriteMode == ReadWriteMode.Write) "读写模式" else "读模式", Modifier.align(Alignment.TopCenter))

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
                        makeToast(context, getString(R.string.change_finish))
                    }
                    Surface(shadowElevation = 2.dp) {
                        ReaderUI()
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

    private fun enableForegroundReader(){
        Log.i(TAG, "enableForegroundReader: ")
        nfcAdapter.enableReaderMode(this, this, FLAG_READER_NFC_A,null)
    }
    private fun disableReader(){
        Log.i(TAG, "disableReader: ")
        nfcAdapter.disableReaderMode(this)
    }
    override fun onTagDiscovered(tag: Tag) {
        Log.i(TAG, "onTagDiscovered: $tag ${tag.id}")
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
            val extraId = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_ID)
            Log.i(TAG, "handleIntent: get extra id $extraId")
        }
    }
    @OptIn(ExperimentalStdlibApi::class)
    fun handleTag(tag: Tag?){
        tag?.let {
            cardId = tag.id.toHexString()
            techList = tag.techList.toList()
            Log.i(TAG, "handleTag: techList=$techList")
            when{
                Ndef::class.java.name in techList-> NdefTag.read(tag){
                    Log.d(TAG, "handleTag: Ndef parse $it")
                    records = it.getParcelableArray("records", Bundle::class.java)?.toList()?: listOf()
                    if(readWriteMode == ReadWriteMode.Write){
//                        NdefTag.writeUri(tag, "WIFI:S:ssid;T:WPA;P:password;H:false;")
                        NdefTag.writeUri(tag, contentInput)
                    }
                }
                MifareClassic::class.java.name in techList->MifareTag.read(tag){
                    Log.d(TAG, "handleTag: MifareClassic parse $it")
                }
                NfcA::class.java.name in techList->NfcATag.read(tag){

                }
                NdefFormatable::class.java.name in techList->NdefFormatableTag.read(tag){

                }
            }
            techList.forEach { tec ->
                when (tec) {
                    NfcF::class.java.name -> {
                    }
                    MifareClassic::class.java.name -> {
                    }
                    NfcF::class.java.name -> {

                    }
                }
            }

        }
    }
    @Composable
    fun ReaderUI() {
        Column(
            Modifier
                .fillMaxWidth(0.9f)
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            ButtonText("切换读写模式") {
                if(readWriteMode == ReadWriteMode.Read){
                    readWriteMode = ReadWriteMode.Write
                }else if(readWriteMode == ReadWriteMode.Write){
                    readWriteMode = ReadWriteMode.Read
                }
            }
            if(readWriteMode == ReadWriteMode.Write){
                OutlinedTextField(value = contentInput,
                    onValueChange = {
                        contentInput = it
                    },
                    singleLine = true
                )
            }

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

            TitleSmall("Ndef records:")
            Column {
                records.forEach {
                    TextBlock(it.getString("payloadString", "-"))
                }
            }
        }
    }
}