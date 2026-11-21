package es.mandotele.ir

/**
 * Teclas del mando de LG (probado con la 42LW5500). Todas comparten la dirección NEC 0x04;
 * cada una lleva su número de orden. [repeats] marca las que se repiten al mantener pulsado.
 */
enum class LgKey(val command: Int, val repeats: Boolean = false) {
    POWER(0x08),
    INPUT(0x0B),
    TV_RADIO(0xF0),
    MUTE(0x09),

    VOL_UP(0x02, repeats = true),
    VOL_DOWN(0x03, repeats = true),
    CH_UP(0x00, repeats = true),
    CH_DOWN(0x01, repeats = true),

    UP(0x40, repeats = true),
    DOWN(0x41, repeats = true),
    LEFT(0x07, repeats = true),
    RIGHT(0x06, repeats = true),
    OK(0x44),
    BACK(0x28),
    EXIT(0x5B),
    MENU(0x43),
    Q_MENU(0x45),
    INFO(0xAA),
    GUIDE(0xAB),

    NUM_0(0x10),
    NUM_1(0x11),
    NUM_2(0x12),
    NUM_3(0x13),
    NUM_4(0x14),
    NUM_5(0x15),
    NUM_6(0x16),
    NUM_7(0x17),
    NUM_8(0x18),
    NUM_9(0x19),
    LIST(0x53),
    Q_VIEW(0x1A),

    RED(0x72),
    GREEN(0x71),
    YELLOW(0x63),
    BLUE(0x61),

    FAV(0x1E),
    RATIO(0x79),
    THREE_D(0xDC),
    AV_MODE(0x30),
    SUBTITLE(0x39),
    TEXT(0x20),

    REWIND(0x8F),
    PLAY(0xB0),
    PAUSE(0xBA),
    STOP(0xB1),
    FAST_FORWARD(0x8E);

    val pattern: IntArray get() = Nec.pattern(ADDRESS, command)

    companion object {
        const val ADDRESS = 0x04

        val digits = listOf(NUM_0, NUM_1, NUM_2, NUM_3, NUM_4, NUM_5, NUM_6, NUM_7, NUM_8, NUM_9)
    }
}
