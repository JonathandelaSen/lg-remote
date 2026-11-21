package io.github.jonathandelasen.remoto.ir

/**
 * Protocolo de infrarrojos NEC, el que usan los mandos de LG.
 *
 * Un fotograma son 4 bytes (dirección, dirección invertida, orden, orden invertida),
 * cada uno enviado con el bit menos significativo primero. Los tiempos van en
 * microsegundos, que es lo que espera `ConsumerIrManager.transmit`.
 */
object Nec {
    const val CARRIER_HZ = 38_000

    private const val LEADER_MARK = 9_000
    private const val LEADER_SPACE = 4_500
    private const val BIT_MARK = 560
    private const val ZERO_SPACE = 560
    private const val ONE_SPACE = 1_690

    /** Los 4 bytes del fotograma, en orden de envío. */
    fun frameBytes(address: Int, command: Int): IntArray {
        require(address in 0..0xFF) { "Dirección fuera de rango: $address" }
        require(command in 0..0xFF) { "Orden fuera de rango: $command" }
        return intArrayOf(address, address.inv() and 0xFF, command, command.inv() and 0xFF)
    }

    /** Patrón marca/espacio alterno (empieza y termina en marca) listo para el emisor. */
    fun pattern(address: Int, command: Int): IntArray {
        val out = IntArray(2 + 32 * 2 + 1)
        var i = 0
        out[i++] = LEADER_MARK
        out[i++] = LEADER_SPACE
        for (byte in frameBytes(address, command)) {
            for (bit in 0 until 8) {
                out[i++] = BIT_MARK
                out[i++] = if ((byte shr bit) and 1 == 1) ONE_SPACE else ZERO_SPACE
            }
        }
        out[i] = BIT_MARK
        return out
    }

    /**
     * El código de 32 bits tal y como se publica en las tablas de mandos
     * (p. ej. `0x20DF10EF` para encender una LG): los bits en orden de envío.
     */
    fun code32(address: Int, command: Int): Long {
        var value = 0L
        for (byte in frameBytes(address, command)) {
            for (bit in 0 until 8) {
                value = (value shl 1) or ((byte shr bit) and 1).toLong()
            }
        }
        return value
    }

    /** Lee un patrón y devuelve su código de 32 bits. Sirve para comprobar lo que se emite. */
    fun decode(pattern: IntArray): Long {
        require(pattern.size == 67) { "Un fotograma NEC tiene 67 tramos, no ${pattern.size}" }
        require(pattern[0] == LEADER_MARK && pattern[1] == LEADER_SPACE) { "Falta la cabecera NEC" }
        var value = 0L
        for (bit in 0 until 32) {
            val space = pattern[3 + bit * 2]
            value = (value shl 1) or if (space > (ZERO_SPACE + ONE_SPACE) / 2) 1L else 0L
        }
        return value
    }
}
