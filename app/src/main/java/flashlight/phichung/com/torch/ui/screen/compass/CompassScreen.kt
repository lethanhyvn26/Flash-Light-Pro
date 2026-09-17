package flashlight.phichung.com.torch.ui.screen.compass

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import flashlight.phichung.com.torch.R
import flashlight.phichung.com.torch.ui.components.AdmobBanner
import flashlight.phichung.com.torch.ui.components.TopAppBarApp
import flashlight.phichung.com.torch.ui.theme.TextWhiteColor
import timber.log.Timber


object CompassNavigation {

    const val route = "compass"

}

@Composable
fun CompassScreen(
    viewModel: CompassViewModel = hiltViewModel<CompassViewModel>(),
    navController: NavHostController
) {

    TopAppBarApp(
        navController = navController,
        title = stringResource(id = R.string.str_compass_title),
        content = {

            Column(modifier = Modifier.padding(it)) {

                Box(modifier = Modifier.weight(1f)) {
                    Compass(
                        rotation = viewModel.rotation.collectAsState(0).value
                    )
                }

                Spacer(modifier = Modifier.size(15.dp))

                AdmobBanner(cachePreferencesHelper = viewModel.getCachePreferencesHelper())


            }

        }
    )



    DisposableEffect(true) {
        Timber.i("HAOHAO startRotationUpdates")
        viewModel.startRotationUpdates()
        onDispose {
            Timber.i("HAOHAO stopRotationUpdates")

            viewModel.stopRotationUpdates()
        }
    }
}


@Composable
fun Compass(
    rotation: Int
) {
    val (lastRotation, setLastRotation) = remember { mutableIntStateOf(0) }
    var newRotation = lastRotation
    val modLast = if (lastRotation > 0) lastRotation % 360 else 360 - (-lastRotation % 360)

    if (modLast != rotation) {
        // new rotation comes in
        val backward = if (rotation > modLast) modLast + 360 - rotation else modLast - rotation
        val forward = if (rotation > modLast) rotation - modLast else 360 - modLast + rotation

        newRotation = if (backward < forward) {
            // backward rotation is shorter
            lastRotation - backward
        } else {
            // forward rotation is shorter (or they are equals)
            lastRotation + forward
        }

        setLastRotation(newRotation)
    }

    val angle: Float by animateFloatAsState(
        targetValue = -newRotation.toFloat(),
        animationSpec = tween(
            durationMillis = UPDATE_FREQUENCY,
            easing = LinearEasing
        )
    )

    Rose(angle = angle, rotation = rotation)


}


@Composable
fun Rose(
    angle: Float,
    rotation: Int,
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier
            .padding(30.dp)
            .fillMaxSize()
            .rotate(angle),
        painter = painterResource(id = R.drawable.ic_rose),
        contentDescription = "",
        contentScale = ContentScale.Fit,

        )

    Text(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        text = stringResource(id = R.string.degree_format, rotation),
        color = TextWhiteColor,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.displayLarge,

        )
}
