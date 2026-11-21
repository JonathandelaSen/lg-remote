package io.github.jonathandelasen.remoto.ir

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NecTest {

    @Test
    fun `el fotograma lleva dirección y orden con sus inversos`() {
        assertArrayEquals(intArrayOf(0x04, 0xFB, 0x08, 0xF7), Nec.frameBytes(0x04, 0x08))
    }

    @Test
    fun `el código de encender una LG es el publicado`() {
        assertEquals(0x20DF10EFL, Nec.code32(0x04, 0x08))
    }

    @Test
    fun `el patrón tiene cabecera, 32 bits y marca final`() {
        val p = Nec.pattern(0x04, 0x08)
        assertEquals(67, p.size)
        assertEquals(9_000, p[0])
        assertEquals(4_500, p[1])
        assertEquals(560, p.last())
        // Las posiciones pares son marcas: todas iguales salvo la cabecera.
        for (i in 2 until p.size step 2) assertEquals(560, p[i])
    }

    @Test
    fun `el patrón dura lo que un fotograma NEC`() {
        val totalMicros = Nec.pattern(0x04, 0x08).sum()
        // 9 + 4,5 ms de cabecera + 32 bits (16 unos y 16 ceros, siempre) + marca final ≈ 67,7 ms
        assertTrue("dura $totalMicros µs", totalMicros in 67_000..68_500)
    }

    @Test
    fun `decodificar el patrón devuelve el mismo código para cualquier orden`() {
        for (command in 0..0xFF) {
            assertEquals(Nec.code32(0x04, command), Nec.decode(Nec.pattern(0x04, command)))
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rechaza órdenes de más de un byte`() {
        Nec.pattern(0x04, 0x100)
    }
}
