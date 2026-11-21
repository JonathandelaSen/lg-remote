package com.jonathandelasen.lgremote

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import com.jonathandelasen.lgremote.ir.IrRemote
import com.jonathandelasen.lgremote.ui.Palette
import com.jonathandelasen.lgremote.ui.RemoteScreen

class MainActivity : ComponentActivity() {
    private lateinit var remote: IrRemote

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        remote = IrRemote(this)
        // Para las capturas del README: el emulador no tiene infrarrojos y saldría el aviso.
        val screenshotMode = BuildConfig.DEBUG && intent.getBooleanExtra(EXTRA_SCREENSHOT, false)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(background = Palette.Background, surface = Palette.Background)) {
                RemoteScreen(irAvailable = remote.available || screenshotMode, onKey = remote::send)
            }
        }
    }

    override fun onDestroy() {
        remote.close()
        super.onDestroy()
    }

    private companion object {
        const val EXTRA_SCREENSHOT = "screenshot"
    }
}
