package com.jonathandelasen.lgremote.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jonathandelasen.lgremote.R
import com.jonathandelasen.lgremote.ir.LgKey

private typealias OnKey = (LgKey, Boolean) -> Unit

private val Gap = 10.dp
private val RockerWidth = 68.dp

@Composable
fun RemoteScreen(irAvailable: Boolean, onKey: OnKey) {
    // El mando no se refleja en idiomas de derecha a izquierda (árabe): la flecha izquierda
    // tiene que seguir a la izquierda, como en el mando físico. Los textos sí van en su sentido.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        RemoteLayout(irAvailable, onKey)
    }
}

@Composable
private fun RemoteLayout(irAvailable: Boolean, onKey: OnKey) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier.fillMaxSize().background(Palette.Background),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .widthIn(max = 440.dp)
                .fillMaxWidth()
                // Las barras del sistema fuera del scroll: así los botones no pasan por debajo de la hora.
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            if (!irAvailable) NoIrBanner()
            TopRow(onKey)
            Cluster(onKey)
            Row(horizontalArrangement = Arrangement.spacedBy(Gap)) {
                IconTextKey(stringResource(R.string.back), RemoteIcons.Back, LgKey.BACK, onKey)
                IconTextKey(stringResource(R.string.menu), RemoteIcons.Home, LgKey.MENU, onKey)
                IconTextKey(stringResource(R.string.exit), RemoteIcons.Exit, LgKey.EXIT, onKey)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Gap)) {
                TextKey("Q.MENU", LgKey.Q_MENU, onKey)
                IconTextKey("INFO", RemoteIcons.Info, LgKey.INFO, onKey, height = 44.dp)
                TextKey("GUIDE", LgKey.GUIDE, onKey)
            }
            ColorRow(onKey)

            SectionTitle(stringResource(R.string.numbers))
            NumberPad(onKey)

            SectionTitle(stringResource(R.string.more))
            Row(horizontalArrangement = Arrangement.spacedBy(Gap)) {
                TextKey("FAV", LgKey.FAV, onKey)
                TextKey("RATIO", LgKey.RATIO, onKey)
                TextKey("3D", LgKey.THREE_D, onKey)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Gap)) {
                TextKey("AV MODE", LgKey.AV_MODE, onKey)
                TextKey("SUBT.", LgKey.SUBTITLE, onKey)
                TextKey("TEXT", LgKey.TEXT, onKey)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Gap)) {
                IconKey(stringResource(R.string.rewind), RemoteIcons.Rewind, LgKey.REWIND, onKey)
                IconKey(stringResource(R.string.play), RemoteIcons.Play, LgKey.PLAY, onKey)
                IconKey(stringResource(R.string.pause), RemoteIcons.Pause, LgKey.PAUSE, onKey)
                IconKey(stringResource(R.string.stop), RemoteIcons.Stop, LgKey.STOP, onKey)
                IconKey(stringResource(R.string.fast_forward), RemoteIcons.FastForward, LgKey.FAST_FORWARD, onKey)
            }
        }
    }
}

@Composable
private fun NoIrBanner() {
    Text(
        text = stringResource(R.string.no_ir_emitter),
        color = Palette.OnKey,
        fontSize = 14.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Palette.Warning)
            .padding(12.dp),
    )
}

@Composable
private fun TopRow(onKey: OnKey) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Gap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RemoteButton(
            description = stringResource(R.string.power),
            onPress = { onKey(LgKey.POWER, it) },
            shape = CircleShape,
            color = Palette.Power,
            modifier = Modifier.size(60.dp),
        ) { KeyIcon(RemoteIcons.Power, tint = Color.White, size = 28.dp) }
        IconTextKey("INPUT", RemoteIcons.Input, LgKey.INPUT, onKey, height = 60.dp)
        TextKey("TV/RAD", LgKey.TV_RADIO, onKey, height = 60.dp)
        RemoteButton(
            description = stringResource(R.string.mute),
            onPress = { onKey(LgKey.MUTE, it) },
            shape = CircleShape,
            modifier = Modifier.size(60.dp),
        ) { KeyIcon(RemoteIcons.Mute) }
    }
}

/** Volumen a la izquierda, cruceta en el centro y canal a la derecha, como en el mando. */
@Composable
private fun Cluster(onKey: OnKey) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val padSize = (maxWidth - RockerWidth * 2 - Gap * 2).coerceAtMost(240.dp)
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Rocker(
                "VOL", padSize,
                LgKey.VOL_UP, stringResource(R.string.volume_up), RemoteIcons.Plus,
                LgKey.VOL_DOWN, stringResource(R.string.volume_down), RemoteIcons.Minus,
                onKey,
            )
            DPad(padSize, onKey)
            Rocker(
                "P", padSize,
                LgKey.CH_UP, stringResource(R.string.channel_up), RemoteIcons.Up,
                LgKey.CH_DOWN, stringResource(R.string.channel_down), RemoteIcons.Down,
                onKey,
            )
        }
    }
}

@Composable
private fun Rocker(
    label: String,
    height: Dp,
    upKey: LgKey,
    upDescription: String,
    upIcon: ImageVector,
    downKey: LgKey,
    downDescription: String,
    downIcon: ImageVector,
    onKey: OnKey,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(RockerWidth)
            .height(height)
            .clip(RoundedCornerShape(RockerWidth / 2))
            .background(Palette.Key),
    ) {
        RemoteButton(
            description = upDescription,
            onPress = { onKey(upKey, it) },
            shape = RectangleShape,
            repeats = upKey.repeats,
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) { KeyIcon(upIcon, size = 30.dp) }
        Text(label, color = Palette.Muted, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        RemoteButton(
            description = downDescription,
            onPress = { onKey(downKey, it) },
            shape = RectangleShape,
            repeats = downKey.repeats,
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) { KeyIcon(downIcon, size = 30.dp) }
    }
}

@Composable
private fun DPad(size: Dp, onKey: OnKey) {
    val cell = size / 3
    Box(Modifier.size(size).clip(CircleShape).background(Palette.Panel)) {
        val arrows = listOf(
            Triple(LgKey.UP, RemoteIcons.Up, Alignment.TopCenter),
            Triple(LgKey.DOWN, RemoteIcons.Down, Alignment.BottomCenter),
            Triple(LgKey.LEFT, RemoteIcons.Left, Alignment.CenterStart),
            Triple(LgKey.RIGHT, RemoteIcons.Right, Alignment.CenterEnd),
        )
        val names = mapOf(
            LgKey.UP to stringResource(R.string.up),
            LgKey.DOWN to stringResource(R.string.down),
            LgKey.LEFT to stringResource(R.string.left),
            LgKey.RIGHT to stringResource(R.string.right),
        )
        for ((key, icon, alignment) in arrows) {
            RemoteButton(
                description = names.getValue(key),
                onPress = { onKey(key, it) },
                shape = CircleShape,
                color = Color.Transparent,
                repeats = key.repeats,
                modifier = Modifier.align(alignment).size(cell),
            ) { KeyIcon(icon, size = 36.dp) }
        }
        RemoteButton(
            description = "OK",
            onPress = { onKey(LgKey.OK, it) },
            shape = CircleShape,
            color = Palette.KeyStrong,
            modifier = Modifier.align(Alignment.Center).size(cell * 1.1f),
        ) { KeyLabel("OK", size = 18.sp) }
    }
}

@Composable
private fun ColorRow(onKey: OnKey) {
    Row(horizontalArrangement = Arrangement.spacedBy(Gap)) {
        val colors = listOf(
            Triple(LgKey.RED, Palette.Red, stringResource(R.string.red_button)),
            Triple(LgKey.GREEN, Palette.Green, stringResource(R.string.green_button)),
            Triple(LgKey.YELLOW, Palette.Yellow, stringResource(R.string.yellow_button)),
            Triple(LgKey.BLUE, Palette.Blue, stringResource(R.string.blue_button)),
        )
        for ((key, color, name) in colors) {
            RemoteButton(
                description = name,
                onPress = { onKey(key, it) },
                color = color,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f).height(30.dp),
            ) {}
        }
    }
}

@Composable
private fun NumberPad(onKey: OnKey) {
    val rows = listOf(
        listOf(LgKey.NUM_1, LgKey.NUM_2, LgKey.NUM_3),
        listOf(LgKey.NUM_4, LgKey.NUM_5, LgKey.NUM_6),
        listOf(LgKey.NUM_7, LgKey.NUM_8, LgKey.NUM_9),
    )
    Column(verticalArrangement = Arrangement.spacedBy(Gap)) {
        for (row in rows) {
            Row(horizontalArrangement = Arrangement.spacedBy(Gap)) {
                for (key in row) DigitKey(key, onKey)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Gap)) {
            TextKey("LIST", LgKey.LIST, onKey, height = 54.dp)
            DigitKey(LgKey.NUM_0, onKey)
            TextKey("Q.VIEW", LgKey.Q_VIEW, onKey, height = 54.dp)
        }
    }
}

@Composable
private fun RowScope.DigitKey(key: LgKey, onKey: OnKey) {
    val digit = LgKey.digits.indexOf(key).toString()
    RemoteButton(
        description = digit,
        onPress = { onKey(key, it) },
        modifier = Modifier.weight(1f).height(54.dp),
    ) { KeyLabel(digit, size = 24.sp) }
}

@Composable
private fun RowScope.TextKey(label: String, key: LgKey, onKey: OnKey, height: Dp = 44.dp) {
    RemoteButton(
        description = label,
        onPress = { onKey(key, it) },
        repeats = key.repeats,
        modifier = Modifier.weight(1f).height(height),
    ) { KeyLabel(label, size = 14.sp) }
}

@Composable
private fun RowScope.IconKey(description: String, icon: ImageVector, key: LgKey, onKey: OnKey) {
    RemoteButton(
        description = description,
        onPress = { onKey(key, it) },
        modifier = Modifier.weight(1f).height(48.dp),
    ) { KeyIcon(icon) }
}

@Composable
private fun RowScope.IconTextKey(
    label: String,
    icon: ImageVector,
    key: LgKey,
    onKey: OnKey,
    height: Dp = 52.dp,
) {
    RemoteButton(
        description = label,
        onPress = { onKey(key, it) },
        modifier = Modifier.weight(1f).height(height),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            KeyIcon(icon, size = 20.dp)
            Spacer(Modifier.width(6.dp))
            KeyLabel(label, size = 14.sp)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text.uppercase(),
        color = Palette.Muted,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(top = 6.dp),
    )
}

@Composable
fun KeyIcon(icon: ImageVector, tint: Color = Palette.OnKey, size: Dp = 24.dp) {
    Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(size))
}
