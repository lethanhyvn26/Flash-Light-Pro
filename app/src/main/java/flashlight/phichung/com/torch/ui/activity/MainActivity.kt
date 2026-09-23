package flashlight.phichung.com.torch.ui.activity

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import flashlight.phichung.com.torch.FlashlightApp
import flashlight.phichung.com.torch.R
import flashlight.phichung.com.torch.ui.theme.FlashlightTheme


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val barColor = getColor(R.color.color_app)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(barColor),
            navigationBarStyle = SystemBarStyle.dark(barColor),
        )
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        setContent {
            FlashlightTheme {
                // System bars are the same color as the window. Inset the content once
                // so it stays where it was before edge-to-edge enforcement.
                Box(Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()) {
                    FlashlightApp()
                }
            }
        }
    }


}



