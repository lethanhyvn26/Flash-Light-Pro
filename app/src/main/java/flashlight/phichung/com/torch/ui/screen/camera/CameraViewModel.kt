package flashlight.phichung.com.torch.ui.screen.camera


import android.net.Uri
import android.os.Build
import com.haohao.camposer.state.CameraState
import com.haohao.camposer.state.ImageCaptureResult
import dagger.hilt.android.lifecycle.HiltViewModel
import flashlight.phichung.com.torch.base.BaseViewModel
import flashlight.phichung.com.torch.camera.data.local.User
import flashlight.phichung.com.torch.camera.data.local.datasource.FileDataSource
import flashlight.phichung.com.torch.data.CachePreferencesHelper
import flashlight.phichung.com.torch.utils.CoroutineContextProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class CameraViewModel @Inject constructor(
    contextProvider: CoroutineContextProvider,
    private val preferencesHelper: CachePreferencesHelper,
    private val fileDataSource: FileDataSource,


    ) : BaseViewModel(contextProvider, preferencesHelper) {


    private val _uiState: MutableStateFlow<CameraUiState> = MutableStateFlow(CameraUiState.Initial)
    val uiState: StateFlow<CameraUiState> get() = _uiState


    private var user: User = User.Default


    init {
        launchCoroutineIO {
            _uiState.value = CameraUiState.Ready(
                user = User.Default,
                lastPicture = fileDataSource.latestCaptured()?.uri,
            )
        }
    }


    fun takePicture(cameraState: CameraState) = with(cameraState) {
        launchCoroutineMain {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> takePicture(
                    fileDataSource.newImageContentValues(),
                    onResult = ::onImageResult
                )

                else -> takePicture(
                    fileDataSource.getFile("jpg"),
                    ::onImageResult
                )
            }
        }
    }


    private fun captureSuccess(savedUri: Uri?) {
        launchCoroutineIO {
            val latest = savedUri ?: fileDataSource.latestCaptured()?.uri
            _uiState.update {
                CameraUiState.Ready(user = user, lastPicture = latest)
            }
        }
    }


    private fun onImageResult(imageResult: ImageCaptureResult) {
        when (imageResult) {
            is ImageCaptureResult.Error -> onError(imageResult.throwable)
            is ImageCaptureResult.Success -> captureSuccess(imageResult.savedUri)
        }
    }

    private fun onError(throwable: Throwable?) {
        _uiState.update { current ->
            val picture = (current as? CameraUiState.Ready)?.lastPicture
            CameraUiState.Ready(user, picture, throwable)
        }
    }

}

sealed interface CameraUiState {
    data object Initial : CameraUiState
    data class Ready(
        val user: User,
        val lastPicture: Uri?,
        val throwable: Throwable? = null,
    ) : CameraUiState
}
