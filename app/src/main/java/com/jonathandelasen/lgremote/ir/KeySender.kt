package com.jonathandelasen.lgremote.ir

import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicInteger

/**
 * Pone en cola los envíos al emisor de infrarrojos, que bloquea mientras emite (~70 ms).
 *
 * Las pulsaciones sueltas siempre se envían. Las repeticiones por mantener pulsado
 * se descartan si aún queda algo por emitir, para que al soltar el botón no siga
 * subiendo el volumen con lo que se había acumulado.
 */
class KeySender(
    private val transmit: (IntArray) -> Unit,
    private val executor: Executor,
    private val onError: (Throwable) -> Unit = {},
) {
    private val pending = AtomicInteger(0)

    /** Devuelve `true` si el envío se ha aceptado. */
    fun send(key: LgKey, isRepeat: Boolean = false): Boolean {
        if (isRepeat) {
            if (!pending.compareAndSet(0, 1)) return false
        } else {
            pending.incrementAndGet()
        }
        executor.execute {
            try {
                transmit(key.pattern)
            } catch (e: Exception) {
                onError(e)
            } finally {
                pending.decrementAndGet()
            }
        }
        return true
    }
}
