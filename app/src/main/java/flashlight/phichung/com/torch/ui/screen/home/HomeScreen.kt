package flashlight.phichung.com.torch.ui.screen.home

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import flashlight.phichung.com.torch.R
import flashlight.phichung.com.torch.ads.AdmobHelp
import flashlight.phichung.com.torch.data.model.Skin
import flashlight.phichung.com.torch.ui.components.AdmobBanner
import flashlight.phichung.com.torch.ui.components.ButtonChild
import flashlight.phichung.com.torch.ui.theme.GrayColor
import flashlight.phichung.com.torch.ui.theme.GrayDarkColor
import flashlight.phichung.com.torch.ui.theme.PowerOffColor
import flashlight.phichung.com.torch.ui.theme.TextSOSColor
import flashlight.phichung.com.torch.ui.theme.TextWhiteColor
import timber.log.Timber

object HomeNavigation {

    const val route = "home"

}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel<HomeViewModel>(),
    openSkinAction: () -> Unit,
    openSettingsAction: () -> Unit,
    openColorAction: () -> Unit,
    openCompassAction: () -> Unit,
    openMorseAction: () -> Unit,
    openRemoveAdsAction: () -> Unit,
    openCameraAction: () -> Unit,
) {
    // val context = LocalContext.current
//    var camManager by remember { mutableStateOf<CameraManager?>(null) }
//    var cameraId by remember { mutableStateOf<String?>(null) }

    var skinCurrent = viewModel.getSkinCurrent()

    DisposableEffect(Unit) {   //register torch callback


        onDispose {
            viewModel.onDisposeTorch()
            //    viewModel.toggleBlinkStateWithDelay(camManager = camManager, cameraId = cameraId)
        }
    }

    Box {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.bg_home),
            contentDescription = "background home",
            contentScale = ContentScale.FillBounds
        )
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            content = ({ innerPadding ->
                Column(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    Spacer(modifier = Modifier.size(60.dp))
                    SliderFlash(
                        viewModel = viewModel,
                        skinCurrent = skinCurrent
                    )
                    ButtonPower(
                        modifier = Modifier.weight(1f),
                        viewModel = viewModel,
                        skinCurrent = skinCurrent
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
                    ) {

                        ButtonChild(
                            icon = R.drawable.ic_brightness,
                            clickAction = openColorAction
                        )
                        ButtonChild(
                            icon = R.drawable.ic_camera,
                            clickAction = openCameraAction
                        )

                        ButtonChild(
                            icon = R.drawable.ic_compass,
                            clickAction = openCompassAction
                        )

                        ButtonChild(
                            icon = R.drawable.ic_morse,
                            clickAction = openMorseAction
                        )
                    }
                    Spacer(modifier = Modifier.size(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
                    ) {
                        var stateSound by remember { mutableStateOf(viewModel.getSoundState()) }

                        ButtonChild(
                            icon = if (stateSound) R.drawable.ic_volume_off else R.drawable.ic_volume_on,
                            clickAction = {
                                val valueSet = !stateSound
                                stateSound = valueSet
                                viewModel.setSoundState(valueSet)
                            }
                        )
                        ButtonChild(
                            icon = R.drawable.ic_skin,
                            clickAction = openSkinAction
                        )

                        if (!viewModel.getCachePreferencesHelper().stateRemoveAds) {
                            ButtonChild(
                                icon = R.drawable.ic_pro,
                                clickAction = openRemoveAdsAction
                            )
                        }


                        ButtonChild(
                            icon = R.drawable.ic_settings,
                            clickAction = openSettingsAction
                        )
                    }
                    Spacer(modifier = Modifier.size(15.dp))

                    AdmobBanner(cachePreferencesHelper = viewModel.getCachePreferencesHelper())

                }
            })
        )
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderFlash(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel<HomeViewModel>(),
    skinCurrent: Skin
) {

    var lisText = listOf("SOS", "1", "2", "3", "4", "5", "6", "7", "8", "9")
    val uiBlinkState by viewModel.uiFlashFloatBlinkState.collectAsState()
    var sliderPosition by remember { mutableFloatStateOf(uiBlinkState) }
    val uiPowerState by viewModel.uiPowerState.collectAsState()
    val activity = LocalContext.current as AppCompatActivity

//    val uiIsBlinkState by viewModel.uiIsBlinkState.collectAsState()

    Box(contentAlignment = Alignment.Center) {
        VerticalLines(lisText)
        Slider(
            modifier = Modifier.padding(horizontal = 15.dp),
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it

                Timber.i("sliderPosition: $sliderPosition")

                viewModel.setValueFlashFloatBlinkState(sliderPosition)
                viewModel.toggleBlinkStateWithDelay()


            },
            onValueChangeFinished = {
                AdmobHelp.instance?.showInterstitialAd(activity,
                    object : AdmobHelp.AdCloseListener {
                        override fun onAdClosed() {
                        }

                    })
            },

            colors = SliderDefaults.colors(
                //   thumbColor = if (uiPowerState) skinCurrent.colorSkin else PowerOffColor,
                activeTrackColor = Color.Transparent,
                inactiveTrackColor = Color.Transparent,
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent,
                disabledThumbColor = GrayColor
            ),

            thumb = {

                Image(
                    painter = painterResource(id = R.drawable.ic_circle),
                    colorFilter = if (uiPowerState) ColorFilter.tint(skinCurrent.colorSkin) else ColorFilter.tint(
                        PowerOffColor
                    ),
                    contentDescription = "Thumb Slider",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(24.dp)
                        .shadow(10.dp, CircleShape)
                        .border(
                            BorderStroke(4.dp, GrayDarkColor),
                            CircleShape
                        )
                        .clip(CircleShape)
//                        .clickable {
//                            viewModel.setPowerState(!uiPowerState)
//
//                        }
                )

            },

            steps = 8,
            valueRange = 0.1f..9f,

            )
    }

}


@Composable
fun VerticalLines(dates: List<String>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
    ) {
        val drawPadding: Float = with(LocalDensity.current) { 10.dp.toPx() + 15.dp.toPx() }
        Canvas(modifier = Modifier.fillMaxSize()) {
            val yStart = 0f
            val yEnd = size.height / 2
            val distance: Float = (size.width.minus(2 * drawPadding)).div(dates.size.minus(1))
            dates.forEachIndexed { index, step ->
                drawLine(
                    color = GrayColor,
                    start = Offset(x = drawPadding + index.times(distance), y = yStart),
                    end = Offset(x = drawPadding + index.times(distance), y = yEnd),
                    strokeWidth = 5f
                )
                drawLine(
                    color = GrayColor,
                    start = Offset(0f, yEnd),
                    end = Offset(size.width, yEnd),
                    strokeWidth = 15f
                )
                // Draw text above each line
                drawIntoCanvas { canvas ->
                    val textPaint =
                        android.graphics.Paint().apply {
                            color =
                                if (index == 0) TextSOSColor.toArgb() else TextWhiteColor.toArgb() // Set text color
                            textSize = 17.sp.toPx()

                        }
                    val text = dates[index] // Text to display
                    val textWidth = textPaint.measureText(text)
                    val xPosition = drawPadding + index.times(distance) - textWidth / 2
                    val yPosition = yStart - 60 // Adjust the y-coordinate for text placement
                    canvas.nativeCanvas.drawText(text, xPosition, yPosition, textPaint)
                }
            }
        }
    }
}

@Composable
fun ButtonPower(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    skinCurrent: Skin
) {
    val uiPowerState by viewModel.uiPowerState.collectAsState()
    val activity = LocalContext.current as AppCompatActivity


    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.ic_power),
            colorFilter = if (uiPowerState) ColorFilter.tint(skinCurrent.colorSkin) else ColorFilter.tint(
                PowerOffColor
            ),
            contentDescription = "Button Power",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(150.dp)
                .shadow(10.dp, CircleShape)
                .border(
                    BorderStroke(4.dp, if (uiPowerState) skinCurrent.colorSkin else PowerOffColor),
                    CircleShape
                )
                .clip(CircleShape)
                .clickable {
                    AdmobHelp.instance?.showInterstitialAd(activity,
                        object : AdmobHelp.AdCloseListener {
                            override fun onAdClosed() {
                                viewModel.setPowerState(!uiPowerState)
                            }

                        })
//                    viewModel.toggleBlinkStateWithDelay(
//                        camManager = camManager,
//                        cameraId = cameraId
//                    )
                }
        )
    }
}



