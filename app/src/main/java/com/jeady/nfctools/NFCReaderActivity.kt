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
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jeady.nfctools.ui.pages.PageReader
import com.jeady.nfctools.ui.pages.ReadTagMode
import com.jeady.nfctools.ui.pages.readTagMode
import com.jeady.nfctools.ui.theme.NFCToolsTheme


var tagDetected: Tag? by mutableStateOf(null)
var lastTagDetected: Tag? by mutableStateOf(null)
var techListState = mutableStateListOf<Pair<String, String>>()
class NFCReaderActivity: ComponentActivity(), ReaderCallback {
    private val TAG = "[JNFCReader]"
    private lateinit var nfcAdapter: NfcAdapter

    // variable used to filter action
    private val ndefFilter =
        IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply { addDataType("*/*") }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate: $this $intent")
        handleIntent(intent)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        val vibrator = applicationContext.getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibrator.defaultVibrator.cancel()
        setContent {
            NFCToolsTheme {
                PageReader {
                    when{
                        it.readWay!=null->{
                            changeReaderModeTo(it.readWay)
                        }
                    }
                }
            }
        }
    }

    public override fun onPause() {
        Log.i(TAG, "onPause")
        super.onPause()
        if (readTagMode == ReadTagMode.Reader) {
            disableReader()
        } else if (readTagMode == ReadTagMode.Dispatcher) {
            disableForegroundDispatcher()
        }
    }

    public override fun onResume() {
        Log.i(TAG, "onResume:")
        super.onResume()
        if (readTagMode == ReadTagMode.Reader) {
            enableForegroundReader()
        } else if (readTagMode == ReadTagMode.Dispatcher) {
            enableForegroundDispatcher()
        }
    }

    public override fun onNewIntent(intent: Intent) {
        Log.i(TAG, "onNewIntent: action ${intent.action}")
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun enableForegroundReader() {
        Log.i(TAG, "enableForegroundReader: ")
        nfcAdapter.enableReaderMode(this, this, FLAG_READER_NFC_A, null)
    }

    private fun disableReader() {
        Log.i(TAG, "disableReader: ")
        nfcAdapter.disableReaderMode(this)
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun onTagDiscovered(tag: Tag) {
        Log.i(TAG, "TagDiscovered: $tag ${tag.id.toHexString()} / ${lastTagDetected?.id?.toHexString()}")
        techListState.clear()
        if(lastTagDetected==null || (lastTagDetected?.id?.toHexString() != tag.id.toHexString())) {
            lastTagDetected = tag
        }
        tagDetected = tag
        tag.techList.forEach {
            techListState.add(it to "reading")
        }
    }

    private fun enableForegroundDispatcher() {
        Log.i(TAG, "enableForegroundDispatcher: ")
        val intent = Intent(this, NFCReaderActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray)
    }

    private fun disableForegroundDispatcher() {
        Log.i(TAG, "disableForegroundDispatcher: ")
        nfcAdapter.disableForegroundDispatch(this)
    }

    private fun handleIntent(intent: Intent) {
        Log.i(TAG, "handleIntent: $intent")
        intent.action?.let {
            makeToast(this, it)
            tagDetected = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
        }
    }


    fun changeReaderMode(){
        if (readTagMode == ReadTagMode.Reader) {
            readTagMode = ReadTagMode.Dispatcher
            disableReader()
            enableForegroundDispatcher()
        } else if (readTagMode == ReadTagMode.Dispatcher) {
            readTagMode = ReadTagMode.Reader
            disableForegroundDispatcher()
            enableForegroundReader()
        }
    }
    private fun changeReaderModeTo(mode: ReadTagMode){
        if (mode == ReadTagMode.Dispatcher) {
            readTagMode = ReadTagMode.Dispatcher
            disableReader()
            enableForegroundDispatcher()
        } else if (mode == ReadTagMode.Reader) {
            readTagMode = ReadTagMode.Reader
            disableForegroundDispatcher()
            enableForegroundReader()
        }
        makeToast(this, getString(R.string.change_finish))
    }
}