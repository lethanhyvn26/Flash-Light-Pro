package flashlight.phichung.com.torch.ui.screen.camera

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.haohao.camposer.CameraPreview
import com.haohao.camposer.state.CamSelector
import com.haohao.camposer.state.CameraState
import com.haohao.camposer.state.CaptureMode
import com.haohao.camposer.state.rememberCameraState
import com.haohao.camposer.state.rememberFlashMode
import com.haohao.camposer.state.rememberTorch
import flashlight.phichung.com.torch.R
import flashlight.phichung.com.torch.ads.AdmobHelp
import flashlight.phichung.com.torch.ui.components.AdmobBanner
import flashlight.phichung.com.torch.ui.components.ButtonChild
import flashlight.phichung.com.torch.ui.components.TopAppBarApp
import flashlight.phichung.com.torch.utils.CameraPermission
import kotlinx.coroutines.delay
import java.io.File


object CameraNavigation {

    const val route = "camera"

}

@Composable
fun CameraScreen(
    viewModel: CameraViewModel = hiltViewModel<CameraViewModel>(),
    navController: NavHostController = rememberNavController(),
    openGalleryScreen: () -> Unit,

    ) {
    TopAppBarApp(
        navController = navController,
        title = stringResource(id = R.string.str_camera_title),
        content = {

            Column(
                modifier = Modifier
                    .padding(it),
            ) {


                Box(modifier = Modifier.weight(1f)) {
                    CameraPermission {
                        CameraScreenMain(
                            openGalleryScreen = openGalleryScreen
                        )
                    }
                }

                Spacer(modifier = Modifier.size(15.dp))
                AdmobBanner(cachePreferencesHelper = viewModel.getCachePreferencesHelper())


            }

        }
    )


}


@Composable
fun CameraScreenMain(
    viewModel: CameraViewModel = hiltViewModel<CameraViewModel>(),
    openGalleryScreen: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val activity = LocalContext.current as AppCompatActivity

    when (val result: CameraUiState = uiState) {
        is CameraUiState.Ready -> {
            val cameraState = rememberCameraState()
            val context = LocalContext.current
            CameraSection(
                cameraState = cameraState,
                usePinchToZoom = result.user.usePinchToZoom,
                useTapToFocus = result.user.useTapToFocus,
                lastPicture = result.lastPicture,
                onTakePicture = { viewModel.takePicture(cameraState) },
                onGalleryClick = {
                    AdmobHelp.instance?.showInterstitialAd(
                        activity,
                        object : AdmobHelp.AdCloseListener {
                            override fun onAdClosed() {
                                openGalleryScreen()
                            }
                        })
                },
            )

            LaunchedEffect(result.throwable) {
                if (result.throwable != null) {
                    Toast.makeText(context, result.throwable.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        CameraUiState.Initial -> Unit
    }

}


@Composable
fun CameraSection(
    cameraState: CameraState,
    usePinchToZoom: Boolean,
    useTapToFocus: Boolean,
    lastPicture: File?,
    onTakePicture: () -> Unit,
    onGalleryClick: () -> Unit,
) {
    var flashMode by cameraState.rememberFlashMode()
    var zoomRatio by rememberSaveable { mutableFloatStateOf(cameraState.minZoom) }
    var zoomHasChanged by rememberSaveable { mutableStateOf(false) }
    var enableTorch by cameraState.rememberTorch(initialTorch = false)

    CameraPreview(
        cameraState = cameraState,
        camSelector = CamSelector.Back,
        captureMode = CaptureMode.Image,
        enableTorch = enableTorch,
        flashMode = flashMode,
        zoomRatio = zoomRatio,
        isPinchToZoomEnabled = usePinchToZoom,
        isFocusOnTapEnabled = useTapToFocus,
        onZoomRatioChanged = {
            zoomHasChanged = true
            zoomRatio = it
        },
        content = {
            BlinkPictureBox(lastPicture = lastPicture, isVideo = false)
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 45.dp)
                        .align(Alignment.BottomCenter), // Aligning content to the bottom of the box,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GalleryButton(lastPicture, onClick = onGalleryClick)
                    PictureButton(
                        onClick = { onTakePicture() }
                    )

                    ButtonChild(
                        icon = if (enableTorch) R.drawable.ic_light_on else R.drawable.ic_light_off,
                        clickAction = {
                            enableTorch = !enableTorch
                        }
                    )
                }

            }

        }
    )
}

@Composable
fun BlinkPictureBox(lastPicture: File?, isVideo: Boolean) {
    var picture by remember(Unit) { mutableStateOf(lastPicture) }
    if (!isVideo && lastPicture != picture) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )

        LaunchedEffect(lastPicture) {
            delay(25)
            picture = lastPicture
        }
    }
}


@Composable
private fun PictureButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {

    Box(
        modifier = modifier
            .size(80.dp)
            .background(
                color = Color.Transparent,
                shape = CircleShape
            )
            .then(
                Modifier.border(
                    width = 4.dp,
                    color = Color.White,
                    shape = CircleShape
                )
            )
            .clickable(
                onClick = onClick
            )
    )


}


@Composable
fun GalleryButton(lastPicture: File?, onClick: () -> Unit) {
    var shouldAnimate by remember { mutableStateOf(false) }
    val animScale by animateFloatAsState(targetValue = if (shouldAnimate) 1.25F else 1F)
    AsyncImage(
        modifier = Modifier
            .scale(animScale)
            .size(48.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.5F), CircleShape)
            .border(
                width = 1.dp,
                color = Color.White,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentScale = ContentScale.Crop,
        model = ImageRequest.Builder(LocalContext.current)
            .data(lastPicture)
            .decoderFactory(VideoFrameDecoder.Factory())
            .videoFrameMillis(1)
            .build(),
        contentDescription = ""
    )

    LaunchedEffect(lastPicture) {
        shouldAnimate = true
        delay(50)
        shouldAnimate = false
    }
}


