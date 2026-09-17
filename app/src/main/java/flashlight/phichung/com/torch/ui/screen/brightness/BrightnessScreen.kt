package flashlight.phichung.com.torch.ui.screen.brightness

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.flask.colorpicker.ColorPickerView
import flashlight.phichung.com.torch.R
import flashlight.phichung.com.torch.ads.AdmobHelp
import flashlight.phichung.com.torch.ui.components.AdmobBanner
import flashlight.phichung.com.torch.ui.theme.GrayColor
import flashlight.phichung.com.torch.ui.theme.IconWhiteColor
import flashlight.phichung.com.torch.ui.theme.TextWhiteColor
import flashlight.phichung.com.torch.utils.hexToColor
import timber.log.Timber


object BrightnessNavigation {


    const val route = "brightness"

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrightnessScreen(
    viewModel: BrightnessViewModel = hiltViewModel<BrightnessViewModel>(),
    navController: NavHostController = rememberNavController()
) {
    var stateIsShowUi by remember { mutableStateOf(true) }
    var stateColorCurrent by remember { mutableStateOf("#191919") }
    val uiIsBlinkState by viewModel.uiIsBlinkState.collectAsState()

    val activity = LocalView.current.context as AppCompatActivity



    if (!stateIsShowUi) {
        Scaffold(
            containerColor = if (!uiIsBlinkState) hexToColor(stateColorCurrent) else Color.Black,
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {

                    },
                    actions = {
                        IconButton(onClick = {
                            AdmobHelp.instance?.showInterstitialAd(
                                activity,
                                object : AdmobHelp.AdCloseListener {
                                    override fun onAdClosed() {
                                        stateIsShowUi = true
                                    }
                                })
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_eyes),
                                contentDescription = "Button Open eyes",
                                colorFilter = ColorFilter.tint(IconWhiteColor)
                            )
                        }
                    }, colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )


                )
            },
            content = ({

            }),

            )
    } else {


        Scaffold(
            containerColor = if (!uiIsBlinkState) hexToColor(stateColorCurrent) else Color.Black,
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.str_color_title),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhiteColor
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Filled.KeyboardArrowLeft, null, tint = IconWhiteColor)
                        }
                    }, colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    actions = {
                        IconButton(onClick = {
                            AdmobHelp.instance?.showInterstitialAd(
                                activity,
                                object : AdmobHelp.AdCloseListener {
                                    override fun onAdClosed() {
                                        stateIsShowUi = false
                                    }
                                })
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_close_eyes),
                                contentDescription = "Button Close eyes",
                                colorFilter = ColorFilter.tint(IconWhiteColor)
                            )
                        }
                    }


                )
            },
            content = ({ innerPadding ->
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
//                   Spacer(modifier = Modifier.fillMaxWidth().size(1.dp).shadow(1.dp))

                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxWidth(), contentAlignment = Alignment.Center
                    ) {
                        Canvas(
                            modifier = Modifier
                                .size(160.dp)
                                .aspectRatio(1f)
                        ) {
                            drawCircle(
                                color = GrayColor,
                                style = Stroke(2.dp.toPx()),
                                center = Offset(size.width / 2, size.height / 2),
                                radius = size.width / 2 - 2 / 2,
                                alpha = 0.2f
                            )
                        }
                        AndroidView(
                            modifier = Modifier
                                .size(150.dp)
                                .aspectRatio(1f),
                            factory = { context ->
                                // Inflate your custom view here
                                val customView = ColorPickerView(context)
                                customView.addOnColorChangedListener {
                                    Timber.i(
                                        "ColorPickerView addOnColorChangedListener: ${
                                            Integer.toHexString(
                                                it
                                            )
                                        }"
                                    )

                                    stateColorCurrent = "#${Integer.toHexString(it).substring(2)}"
                                }
                                customView
                            }
                        )
                    }


                    Row {
                        SliderBrightness(modifier = Modifier.weight(1f))
                        SliderBlink(viewModel = viewModel, modifier = Modifier.weight(1f))

                    }
                    Spacer(modifier = Modifier.size(15.dp))
                    AdmobBanner(cachePreferencesHelper = viewModel.getCachePreferencesHelper())

                }

            }),

            )
    }


}


@Composable
fun SliderBrightness(modifier: Modifier = Modifier) {
    val view = LocalView.current
    val window = (view.context as Activity).window
    val valueCurrent = window?.attributes?.screenBrightness

    val brightness = if (valueCurrent == -1f) 0.5f else valueCurrent?.toFloat() ?: 0f
    val activity = LocalContext.current as AppCompatActivity


    var sliderPosition by remember { mutableFloatStateOf(brightness) }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Slider(
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
                val layoutParams = window?.attributes // Get Params
                layoutParams?.screenBrightness = sliderPosition // Set Value
                window?.attributes = layoutParams // Set params
            },
            onValueChangeFinished = {
                AdmobHelp.instance?.showInterstitialAd(activity,
                    object : AdmobHelp.AdCloseListener {
                        override fun onAdClosed() {
                        }

                    })
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = Color.Transparent,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            ),
            steps = 9,
            valueRange = 0f..1f,
            modifier = Modifier
                .rotate(270f)
                .size(150.dp)

        )
        Spacer(modifier = Modifier.size(10.dp))

        Text(
            text = stringResource(R.string.str_color_brightness),
            style = MaterialTheme.typography.bodyMedium,
            color = TextWhiteColor
        )

    }
}

@Composable
fun SliderBlink(
    viewModel: BrightnessViewModel,
    modifier: Modifier = Modifier,
) {
    val uiBlinkState by viewModel.uiBlinkFloatState.collectAsState()
    val activity = LocalContext.current as AppCompatActivity

    var sliderPosition by remember { mutableFloatStateOf(uiBlinkState) }
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Slider(
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it

                viewModel.toggleBlinkStateWithDelay(sliderPosition)

            },
            onValueChangeFinished = {
                AdmobHelp.instance?.showInterstitialAd(activity,
                    object : AdmobHelp.AdCloseListener {
                        override fun onAdClosed() {
                        }

                    })
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = Color.Transparent,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            ),
            steps = 8,
            valueRange = 0f..9f,
            modifier = Modifier
                .rotate(270f)
                .size(150.dp)

        )
        Spacer(modifier = Modifier.size(10.dp))

        Text(
            text = stringResource(R.string.str_color_blink),
            style = MaterialTheme.typography.bodyMedium,
            color = TextWhiteColor
        )

    }
}


