package es.mandotele.ir

import android.content.Context
import android.hardware.ConsumerIrManager
import android.util.Log
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/** Emisor de infrarrojos del móvil. Si no tiene, [available] es `false` y no envía nada. */
class IrRemote(context: Context) {
    private val ir: ConsumerIrManager? = context.getSystemService(ConsumerIrManager::class.java)
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    val available: Boolean = ir?.hasIrEmitter() == true

    private val sender = KeySender(
        transmit = { pattern -> ir?.transmit(Nec.CARRIER_HZ, pattern) },
        executor = executor,
        onError = { Log.w(TAG, "No se pudo emitir", it) },
    )

    fun send(key: LgKey, isRepeat: Boolean = false) {
        if (available) sender.send(key, isRepeat)
    }

    fun close() = executor.shutdown()

    private companion object {
        const val TAG = "IrRemote"
    }
}
