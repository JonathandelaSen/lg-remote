package com.jonathandelasen.lgremote

import org.junit.Assert.assertEquals
import org.junit.Test

class AppIdTest {

    /** Si cambia, Android la trata como otra app y las versiones nuevas no se instalan encima. */
    @Test
    fun `el identificador de la app no cambia`() {
        assertEquals("com.jonathandelasen.lgremote", BuildConfig.APPLICATION_ID)
    }
}
