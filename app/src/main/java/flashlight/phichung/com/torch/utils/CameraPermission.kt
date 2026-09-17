package flashlight.phichung.com.torch.utils

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import flashlight.phichung.com.torch.ui.theme.TextWhiteColor
import timber.log.Timber

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPermission(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val permissionsState = rememberMultiplePermissionsState(
        mutableListOf(
            android.Manifest.permission.CAMERA,
//            android.Manifest.permission.RECORD_AUDIO,
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                //  add(android.Manifest.permission.POST_NOTIFICATIONS)
                //  add(android.Manifest.permission.READ_MEDIA_AUDIO)
                //  add(android.Manifest.permission.READ_MEDIA_VIDEO)
                add(android.Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    )

    Timber.d("HAOHAO first ${permissionsState.shouldShowRationale}")

    if (permissionsState.allPermissionsGranted) {

        Timber.d("HAOHAO allPermissionsGranted")

        content()
    } else {
        Timber.d("HAOHAO DeniedSection")

        DeniedSection {
            Timber.d("HAOHAO ${permissionsState.shouldShowRationale}")

            if (permissionsState.shouldShowRationale) {
                permissionsState.launchMultiplePermissionRequest()

            } else {
                Timber.d("Permission denied by system")

                context.startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null),
                    ),
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        permissionsState.launchMultiplePermissionRequest()
    }
}

@Composable
private fun DeniedSection(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            modifier = Modifier.padding(15.dp),
            text = stringResource(
                id = flashlight.phichung.com.torch.R.string.str_camera_request_permission,
                stringResource(id = flashlight.phichung.com.torch.R.string.app_name),
            ).replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.bodyMedium,
            color = TextWhiteColor

        )
        Button(
            modifier = Modifier.width(120.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
            onClick = onClick
        ) {
            Text(stringResource(android.R.string.ok), color = Color.White)
        }
    }
}