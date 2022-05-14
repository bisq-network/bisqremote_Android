package com.joachimneumann.bisq.tests.ext

import com.joachimneumann.bisq.ext.capitalizeEachWord
import com.joachimneumann.bisq.ext.hexStringToByteArray
import org.junit.Assert.assertEquals
import org.junit.Test

class StringExtTest {

    @Test
    fun testHexStringToByteArray() {
        val hexString = "ace24f2c3e0848bd9e57f6b415ca08df6ec7c22692bd48a296fe4044759e5eff"
        val bytearray = (hexString).hexStringToByteArray()
        assertEquals(
            "[-84, -30, 79, 44, 62, 8, 72, -67, -98, 87, -10, -76, 21, -54, 8, " +
                    "-33, 110, -57, -62, 38, -110, -67, 72, -94, -106, -2, 64, 68, 117, -98, 94, -1]",
            bytearray.asList().toString()
        )
    }

    @Test
    fun testCapitalizeSingleWord() {
        val test = "testcapitalizestring".capitalizeEachWord()
        assertEquals("Testcapitalizestring", test)
    }

    @Test
    fun testCapitalizeMultipleWords() {
        val test = "test capitalize string".capitalizeEachWord()
        assertEquals("Test Capitalize String", test)
    }

    @Test
    fun testCapitalizeEmptyString() {
        val test = "".capitalizeEachWord()
        assertEquals("", test)
    }

}
