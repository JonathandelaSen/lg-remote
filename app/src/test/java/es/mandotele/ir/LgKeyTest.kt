package es.mandotele.ir

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LgKeyTest {

    /** Códigos de las tablas públicas de mandos LG (formato LIRC). */
    private val published = mapOf(
        LgKey.POWER to 0x20DF10EFL,
        LgKey.INPUT to 0x20DFD02FL,
        LgKey.MUTE to 0x20DF906FL,
        LgKey.VOL_UP to 0x20DF40BFL,
        LgKey.VOL_DOWN to 0x20DFC03FL,
        LgKey.CH_UP to 0x20DF00FFL,
        LgKey.CH_DOWN to 0x20DF807FL,
        LgKey.UP to 0x20DF02FDL,
        LgKey.DOWN to 0x20DF827DL,
        LgKey.LEFT to 0x20DFE01FL,
        LgKey.RIGHT to 0x20DF609FL,
        LgKey.OK to 0x20DF22DDL,
        LgKey.BACK to 0x20DF14EBL,
        LgKey.EXIT to 0x20DFDA25L,
        LgKey.MENU to 0x20DFC23DL,
        LgKey.NUM_0 to 0x20DF08F7L,
        LgKey.NUM_1 to 0x20DF8877L,
        LgKey.NUM_2 to 0x20DF48B7L,
        LgKey.NUM_3 to 0x20DFC837L,
        LgKey.NUM_9 to 0x20DF9867L,
        LgKey.RED to 0x20DF4EB1L,
        LgKey.GREEN to 0x20DF8E71L,
        LgKey.YELLOW to 0x20DFC639L,
        LgKey.BLUE to 0x20DF8679L,
    )

    @Test
    fun `las teclas emiten los códigos publicados de LG`() {
        for ((key, code) in published) {
            assertEquals("$key", code, Nec.decode(key.pattern))
        }
    }

    @Test
    fun `no hay dos teclas con la misma orden`() {
        val repeated = LgKey.entries.groupBy { it.command }.filterValues { it.size > 1 }
        assertTrue("Órdenes repetidas: $repeated", repeated.isEmpty())
    }

    @Test
    fun `los dígitos van del 0 al 9 en orden`() {
        assertEquals(10, LgKey.digits.size)
        LgKey.digits.forEachIndexed { n, key -> assertEquals(0x10 + n, key.command) }
    }

    @Test
    fun `solo se repiten volumen, canal y flechas`() {
        val repeating = LgKey.entries.filter { it.repeats }.toSet()
        assertEquals(
            setOf(
                LgKey.VOL_UP, LgKey.VOL_DOWN, LgKey.CH_UP, LgKey.CH_DOWN,
                LgKey.UP, LgKey.DOWN, LgKey.LEFT, LgKey.RIGHT,
            ),
            repeating,
        )
        assertFalse(LgKey.POWER.repeats)
    }
}
