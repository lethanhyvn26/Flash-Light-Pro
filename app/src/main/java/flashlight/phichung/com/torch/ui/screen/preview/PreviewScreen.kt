package flashlight.phichung.com.torch.ui.screen.preview

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import flashlight.phichung.com.torch.ui.components.AdmobBanner
import flashlight.phichung.com.torch.ui.theme.IconWhiteColor


object PreviewNavigation {

    const val pathArg = "path"

    const val route = "preview/{$pathArg}"
    fun createRoute(path: String) = "preview/${Uri.encode(path)}"


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    viewModel: PreviewViewModel = hiltViewModel<PreviewViewModel>(),
    navController: NavHostController = rememberNavController(),

    ) {
    val uiState by viewModel.uiState.collectAsState()
    val intentSenderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            navController.navigateUp()
        }
        Log.d("INTENT SENDER", "RESULT: ${it.resultCode}")
    }

    Log.d("PREVIEW SCREEN", "OPA")

    when (val result: PreviewUiState = uiState) {
        PreviewUiState.Initial -> {}
        PreviewUiState.Empty -> {
            Log.d("PreviewUiState", "Empty")

        }

        is PreviewUiState.Ready -> {
            val context = LocalContext.current

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        title = {
                        },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(Icons.Filled.KeyboardArrowLeft, null, tint = IconWhiteColor)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        actions = {
                            IconButton(onClick = {
                                viewModel.deleteFile(context, intentSenderLauncher, result.file)
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Delete",
                                    tint = IconWhiteColor
                                )
                            }
                        },


                        )
                },
                content = ({
                    val file = result.file
                    Column(modifier = Modifier.padding(it)) {
                        AsyncImage(
                            modifier = Modifier.weight(1f),
                            model = file,
                            contentScale = ContentScale.Fit,
                            contentDescription = file.name,
                        )

                        Spacer(modifier = Modifier.size(15.dp))
                        AdmobBanner(cachePreferencesHelper = viewModel.getCachePreferencesHelper())

                    }
                }),

                )

        }

        PreviewUiState.Deleted -> LaunchedEffect(Unit) {
            navController.navigateUp()
        }
    }
}
