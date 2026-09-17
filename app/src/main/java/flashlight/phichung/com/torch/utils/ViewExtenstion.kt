package flashlight.phichung.com.torch.utils

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.core.app.ActivityCompat
import flashlight.phichung.com.torch.R

fun hexToColor(hex: String): Color {
    // Remove '#' symbol if present
    val hexWithoutHash = if (hex.startsWith("#")) hex.substring(1) else hex

    // Convert hexadecimal to integer value
    val colorInt = hexWithoutHash.toInt(16)

    // Add full opacity (0xFF) to the color
    val colorWithAlpha = 0xFF000000.toInt() or colorInt

    // Create Color object from integer value
    return Color(colorWithAlpha)
}


fun Activity.getVersionApp(): String {
    try {
        val manager: PackageManager = this
            .packageManager
        var info: PackageInfo? = null
        try {
            info = manager.getPackageInfo(
                this
                    .packageName,
                0
            )
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        assert(info != null)
        return info!!.versionName.toString()

    } catch (_: Exception) {
    }
    return ""
}

fun Activity.shareApp() {
    val sendIntent = Intent(Intent.ACTION_SEND)
    sendIntent.type = "text/plain"
    sendIntent.putExtra(
        Intent.EXTRA_TEXT,
        """ Hey, look this app ! 
 https://play.google.com/store/apps/details?id=${this.packageName}"""
    )
    val shareIntent =
        Intent.createChooser(sendIntent, "Share this app with your friends !")
    startActivity(shareIntent)
}


fun Activity.feedback() {


    val deviceInfo = buildString {
        append("\n")
        append("\n")
        append("\n")
        append("\n")
        append("\n")
        append("\n")
        append("\n")
        append("\n")
        append("\n")
        append("\n")
        append("\n")
        append("\n")
        append("\n")
        append("\nSystem Info:")
        append("\nAndroid Device: " + Build.MODEL + " (" + Build.MANUFACTURER + ")")
        append("\nAndroid version: " + Build.VERSION.RELEASE)
        append("\n Device: " + Build.DEVICE)
        append("\n OS API Level: " + Build.VERSION.SDK_INT)
        append("\n App version: " + getVersionApp())

    }

    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:") // only email apps should handle this
    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_support)))
    intent.putExtra(
        Intent.EXTRA_SUBJECT,
        getString(R.string.email_subject_format, getString(R.string.app_name))
    )
    intent.putExtra(Intent.EXTRA_TEXT, deviceInfo)
    startActivity(Intent.createChooser(intent, "Send email..."))


}


fun Float.toRotationInDegrees(): Int = (this.toInt() + 360) % 360
fun Double.toRotationInDegrees(): Int = (this.toInt() + 360) % 360

fun Activity.arePermissionNeverAskAgain(permissions: Array<String>): Boolean =
    permissions.any {
        !ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            it
        )
    }

fun makeLocation(lat: Double, long: Double, provider: String = "") = Location(provider).apply {
    longitude = long
    latitude = lat
}

fun String.isProperLatitude(): Boolean {
    toDoubleOrNull()?.let {
        return it <= 90.0 && it >= -90.0
    } ?: return false
}

fun String.isProperLongitude(): Boolean {
    toDoubleOrNull()?.let {
        return it <= 180.0 && it >= -180.0
    } ?: return false
}

@Composable
fun TextStyle.sizeInDp(): Dp {
    with(LocalDensity.current) {
        return this@sizeInDp.fontSize.toDp()
    }
}

