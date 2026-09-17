package flashlight.phichung.com.torch.ui.components

import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import flashlight.phichung.com.torch.R
import flashlight.phichung.com.torch.ads.AdmobHelp
import flashlight.phichung.com.torch.data.CachePreferencesHelper
import flashlight.phichung.com.torch.ui.theme.GrayColor
import flashlight.phichung.com.torch.ui.theme.IconWhiteColor
import flashlight.phichung.com.torch.ui.theme.TextWhiteColor


/**
 * Button child with border and icon
 *
 * @param modifier
 * @param icon
 * @param clickAction
 * @receiver
 */
@Composable
fun ButtonChild(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    clickAction: () -> Unit = {}
) {

    val activity = LocalContext.current as AppCompatActivity
    Box(
        modifier = Modifier
            .size(50.dp)
            .clickable {
                AdmobHelp.instance?.showInterstitialAd(activity,
                    object : AdmobHelp.AdCloseListener {
                        override fun onAdClosed() {
                            clickAction.invoke()
                        }

                    })
            }, contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "",
//            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(IconWhiteColor),
            modifier = Modifier
                .size(50.dp)
                .border(
                    BorderStroke(2.dp, GrayColor),
                    CircleShape
                )
                .padding(10.dp)
                .clip(CircleShape)
        )
    }


}

@Composable
fun NavigationIcon(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    IconButton(modifier = modifier, onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
        )
    }
}

@Composable
fun AdmobBanner(modifier: Modifier = Modifier, cachePreferencesHelper: CachePreferencesHelper) {
    val adWidth = LocalConfiguration.current.screenWidthDp
    val context = LocalContext.current

    val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
        context,
        adWidth
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(adSize.height.dp)
    ) {

        if (!cachePreferencesHelper.stateRemoveAds) {
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { context ->


                    // on below line specifying ad view.
                    AdView(context).apply {
                        // on below line specifying ad size
                        //adSize = AdSize.BANNER
                        // on below line specifying ad unit id
                        // currently added a test ad unit id.
                        setAdSize(adSize)
                        adUnitId = context.getString(R.string.str_ads_banner)
                        // calling load ad to load our ad.
                        loadAd(AdRequest.Builder().build())
                    }
                }
            )
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarApp(
    modifier: Modifier = Modifier,
    title: String = "",
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
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
                    containerColor = MaterialTheme.colorScheme.primary
                )


            )
        },
        content = ({
            content(it)
        }),

        )
}