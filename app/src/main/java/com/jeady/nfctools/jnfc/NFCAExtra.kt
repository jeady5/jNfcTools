package com.jeady.nfctools.jnfc

import java.math.BigInteger

data class NFCAExtraInfo(
    val manufacturer: String = "",
    val product: String = "",
    val atqa: ByteArray = byteArrayOf(),
    val sak: ByteArray = byteArrayOf(),
    val ats: ByteArray = byteArrayOf(), // ATS (called ATR for contact smartcards)
    val uidLen: Short = 0,
)
val category = listOf(
    NFCAExtraInfo(manufacturer = "NXP", product ="MIFARE Mini", atqa= byteArrayOf(0x00, 0x04), sak = byteArrayOf(0x09), ats= byteArrayOf(), uidLen = 4),
    NFCAExtraInfo(manufacturer ="NXP", product ="MIFARE Classic 1k", atqa= byteArrayOf(0x00, 0x04), sak = byteArrayOf(0x08), ats= byteArrayOf(), uidLen = 4),
    NFCAExtraInfo(manufacturer ="NXP", product ="MIFARE Classic 4k", atqa= byteArrayOf(0x00, 0x02), sak = byteArrayOf(0x18), ats= byteArrayOf(), uidLen = 4),
    NFCAExtraInfo(manufacturer ="NXP", product ="MIFARE Ultralight", atqa= byteArrayOf(0x00, 0x44), sak = byteArrayOf(0x00), ats= byteArrayOf(), uidLen = 7),
    NFCAExtraInfo(manufacturer ="NXP", product ="MIFARE DESFire", atqa= byteArrayOf(0x03, 0x44), sak = byteArrayOf(0x20), ats= byteArrayOf(), uidLen = 7),
    NFCAExtraInfo(manufacturer ="NXP", product ="MIFARE DESFire EV1", atqa= byteArrayOf(0x03, 0x44), sak = byteArrayOf(0x20), ats= byteArrayOf(), uidLen = 7),
    NFCAExtraInfo(manufacturer ="IBM", product ="JCOP31", atqa= byteArrayOf(0x03, 0x04), sak = byteArrayOf(0x28), ats= byteArrayOf(), uidLen = 4),
    NFCAExtraInfo(manufacturer ="IBM", product ="JCOP31 v2.4.1", atqa= byteArrayOf(0x00, 0x48), sak = byteArrayOf(0x20), ats= byteArrayOf(), uidLen = 7),
    NFCAExtraInfo(manufacturer ="IBM", product ="JCOP41 v2.2", atqa= byteArrayOf(0x00, 0x48), sak = byteArrayOf(0x20), ats= byteArrayOf(), uidLen = 7),
    NFCAExtraInfo(manufacturer ="IBM", product ="JCOP41 v2.3.1", atqa= byteArrayOf(0x00, 0x04), sak = byteArrayOf(0x28), ats= byteArrayOf(), uidLen = 4),
    NFCAExtraInfo(manufacturer ="Infineon", product ="MIFARE Classic 1k", atqa= byteArrayOf(0x00, 0x04), sak = byteArrayOf(BigInteger.valueOf(0x88).toByte()), ats= byteArrayOf(), uidLen = 4),
    NFCAExtraInfo(manufacturer ="Gemplus", product ="MPCOS", atqa= byteArrayOf(0x00, 0x02), sak = byteArrayOf(BigInteger.valueOf(0x98).toByte()), ats= byteArrayOf(), uidLen = 0),
    NFCAExtraInfo(manufacturer ="Innovision R&T", product ="Jewel", atqa= byteArrayOf(0x0C, 0x00), sak = byteArrayOf(), ats= byteArrayOf(), uidLen =  0),
    NFCAExtraInfo(manufacturer ="Nokia", product ="MIFARE Classic 4k - emulated (6212 Classic)", atqa= byteArrayOf(0x00, 0x02), sak = byteArrayOf(0x38), ats= byteArrayOf(), uidLen = 4),
    NFCAExtraInfo(manufacturer ="Nokia", product ="MIFARE Classic 4k - emulated (6131 NFC)", atqa= byteArrayOf(0x00, 0x08), sak = byteArrayOf(0x38), ats= byteArrayOf(), uidLen = 4),
)

//
//    | NXP            | MIFARE Mini                                  | 00, 04 | 09    |                                           | 4
//    | NXP            | MIFARE Classic 1k                            | 00, 04 | 08    |                                           | 4
//    | NXP            | MIFARE Classic 4k                            | 00, 02 | 18    |                                           | 4
//    | NXP            | MIFARE Ultralight                            | 00, 44 | 00    |                                           | 7
//    | NXP            | MIFARE DESFire                               | 03, 44 | 20    | 75, 77, 81, 02, 80                            | 7
//    | NXP            | MIFARE DESFire EV1                           | 03, 44 | 20    | 75, 77, 81, 02, 80                            | 7
//    | IBM            | JCOP31                                       | 03, 04 | 28    | 38, 77, b1, 4a, 43, 4f, 50, 33, 31                | 4
//    | IBM            | JCOP31 v2.4.1                                | 00, 48 | 20    | 78, 77, b1, 02, 4a, 43, 4f, 50, 76, 32, 34, 31       | 7
//    | IBM            | JCOP41 v2.2                                  | 00, 48 | 20    | 38, 33, b1, 4a, 43, 4f, 50, 34, 31, 56, 32, 32       | 7
//    | IBM            | JCOP41 v2.3.1                                | 00, 04 | 28    | 38, 33, b1, 4a, 43, 4f, 50, 34, 31, 56, 32, 33, 31    | 4
//    | Infineon       | MIFARE Classic 1k                            | 00, 04 | 88    |                                           | 4
//    | Gemplus        | MPCOS                                        | 00, 02 | 98
//    | Innovision R&T | Jewel                                        | 0C 00  |
//    | Nokia          | MIFARE Classic 4k - emulated (6212 Classic)  | 00, 02 | 38    |                                           | 4
//    | Nokia          | MIFARE Classic 4k - emulated (6131 NFC)      | 00, 08 | 38    |                                           | 4