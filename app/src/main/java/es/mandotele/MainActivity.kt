package es.mandotele

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import es.mandotele.ir.IrRemote
import es.mandotele.ui.Palette
import es.mandotele.ui.RemoteScreen

class MainActivity : ComponentActivity() {
    private lateinit var remote: IrRemote

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        remote = IrRemote(this)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(background = Palette.Background, surface = Palette.Background)) {
                RemoteScreen(irAvailable = remote.available, onKey = remote::send)
            }
        }
    }

    override fun onDestroy() {
        remote.close()
        super.onDestroy()
    }
}
