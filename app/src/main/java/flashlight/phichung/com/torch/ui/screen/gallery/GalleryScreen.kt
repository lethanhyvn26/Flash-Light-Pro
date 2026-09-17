package flashlight.phichung.com.torch.ui.screen.gallery

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFramePercent
import flashlight.phichung.com.torch.R
import flashlight.phichung.com.torch.ads.AdmobHelp
import flashlight.phichung.com.torch.ui.components.AdmobBanner
import flashlight.phichung.com.torch.ui.components.TopAppBarApp
import flashlight.phichung.com.torch.ui.theme.TextWhiteColor
import flashlight.phichung.com.torch.utils.getDuration
import java.io.File
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


object GalleryNavigation {

    const val route = "gallery"

}

@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = hiltViewModel<GalleryViewModel>(),
    navController: NavHostController,
    onPreviewClick: (String) -> Unit,
) {
    TopAppBarApp(
        navController = navController,
        title = stringResource(id = R.string.str_gallery_title),
        content = {

            Column(Modifier.padding(it)) {

                Box(modifier = Modifier.weight(1f)) {
                    viewModel.fetchGallery()
                    val uiState by viewModel.uiState.collectAsState()
                    when (val result: GalleryUiState = uiState) {
                        GalleryUiState.Initial -> GalleryLoading()
                        GalleryUiState.Empty -> GalleryEmpty()
                        is GalleryUiState.Success -> GallerySection(
                            imageFiles = result.images,
                            onPreviewClick = onPreviewClick,
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
private fun GalleryEmpty() {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(24.dp),
            textAlign = TextAlign.Center,
            text = stringResource(id = R.string.str_gallery_empty).replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.bodyMedium,
            color = TextWhiteColor,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GallerySection(imageFiles: List<File>, onPreviewClick: (String) -> Unit) {
    val activity = LocalView.current.context as AppCompatActivity
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        items(imageFiles, { it.name }) { image ->
            val context = LocalContext.current
            var duration by rememberSaveable { mutableStateOf<Int?>(null) }
            LaunchedEffect(Unit) { duration = image.getDuration(context) }
            PlaceholderImage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .animateItemPlacement()
                    .aspectRatio(1F)
                    .clickable(onClick = {
                        AdmobHelp.instance?.showInterstitialAd(
                            activity,
                            object : AdmobHelp.AdCloseListener {
                                override fun onAdClosed() {
                                    onPreviewClick(image.path)
                                }
                            })
                         }),
                data = image,
                contentDescription = image.name,
                placeholder = {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color.LightGray)
                            .clip(RoundedCornerShape(6.dp))
                    )
                },
            ) {
                duration?.let { duration ->
                    Box(
                        modifier = Modifier.background(Color.Black.copy(0.25F)),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                "${duration.minutes}:${duration.seconds}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                            )
                            Icon(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(Color.White, CircleShape),
                                imageVector = Icons.Rounded.PlayArrow,
                                tint = Color.Black,
                                contentDescription = "Play",
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceholderImage(
    modifier: Modifier = Modifier,
    data: Any,
    placeholder: @Composable () -> Unit,
    contentDescription: String?,
    innerContent: @Composable () -> Unit,
) {
    var imageState: AsyncImagePainter.State by remember { mutableStateOf(AsyncImagePainter.State.Empty) }
    Box(modifier) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(6.dp)),
            model = ImageRequest.Builder(LocalContext.current)
                .data(data)
                .decoderFactory(VideoFrameDecoder.Factory())
                .videoFramePercent(0.5)
                .build(),
            onState = { imageState = it },
            contentScale = ContentScale.Crop,
            contentDescription = contentDescription,
        )
        GalleryAnimationVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = when (imageState) {
                is AsyncImagePainter.State.Empty,
                is AsyncImagePainter.State.Success,
                -> false

                is AsyncImagePainter.State.Loading,
                is AsyncImagePainter.State.Error,
                -> true
            }
        ) { placeholder() }

        GalleryAnimationVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = when (imageState) {
                is AsyncImagePainter.State.Empty,
                is AsyncImagePainter.State.Loading,
                is AsyncImagePainter.State.Error,
                -> false

                is AsyncImagePainter.State.Success -> true
            }
        ) { innerContent() }
    }

}

@Composable
private fun GalleryAnimationVisibility(
    modifier: Modifier = Modifier,
    visible: Boolean,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        modifier = modifier,
        enter = fadeIn(),
        exit = fadeOut(),
        visible = visible
    ) { content() }
}

@Composable
private fun GalleryLoading() {
    Box(Modifier.fillMaxSize()) {
        CircularProgressIndicator()
    }
}
