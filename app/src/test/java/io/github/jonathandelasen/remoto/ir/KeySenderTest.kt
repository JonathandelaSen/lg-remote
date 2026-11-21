package io.github.jonathandelasen.remoto.ir

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.Executor

class KeySenderTest {

    /** Ejecutor que guarda las tareas para lanzarlas cuando el test quiera. */
    private class ManualExecutor : Executor {
        val tasks = ArrayDeque<Runnable>()
        override fun execute(command: Runnable) {
            tasks.addLast(command)
        }
        fun runAll() {
            while (tasks.isNotEmpty()) tasks.removeFirst().run()
        }
    }

    @Test
    fun `emite el patrón de la tecla`() {
        val sent = mutableListOf<IntArray>()
        val sender = KeySender(transmit = { sent += it }, executor = { it.run() })

        assertTrue(sender.send(LgKey.POWER))

        assertEquals(1, sent.size)
        assertArrayEquals(LgKey.POWER.pattern, sent[0])
    }

    @Test
    fun `las pulsaciones sueltas se encolan aunque se esté emitiendo`() {
        val executor = ManualExecutor()
        val sent = mutableListOf<Long>()
        val sender = KeySender(transmit = { sent += Nec.decode(it) }, executor = executor)

        assertTrue(sender.send(LgKey.NUM_1))
        assertTrue(sender.send(LgKey.NUM_2))
        executor.runAll()

        assertEquals(listOf(Nec.decode(LgKey.NUM_1.pattern), Nec.decode(LgKey.NUM_2.pattern)), sent)
    }

    @Test
    fun `las repeticiones se descartan si queda algo por emitir`() {
        val executor = ManualExecutor()
        var count = 0
        val sender = KeySender(transmit = { count++ }, executor = executor)

        assertTrue(sender.send(LgKey.VOL_UP))
        assertFalse(sender.send(LgKey.VOL_UP, isRepeat = true))
        executor.runAll()
        assertTrue(sender.send(LgKey.VOL_UP, isRepeat = true))
        executor.runAll()

        assertEquals(2, count)
    }

    @Test
    fun `un fallo del emisor se avisa y no bloquea los siguientes envíos`() {
        val executor = ManualExecutor()
        val errors = mutableListOf<Throwable>()
        var fail = true
        var count = 0
        val sender = KeySender(
            transmit = { if (fail) error("emisor ocupado") else count++ },
            executor = executor,
            onError = { errors += it },
        )

        sender.send(LgKey.POWER)
        executor.runAll()
        fail = false
        assertTrue(sender.send(LgKey.VOL_UP, isRepeat = true))
        executor.runAll()

        assertEquals(1, errors.size)
        assertEquals(1, count)
    }
}
