package flashlight.phichung.com.torch.ui.screen.camera


import android.os.Build
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
class CameraViewModel @Inject constructor(
    contextProvider: CoroutineContextProvider,
    private val preferencesHelper: CachePreferencesHelper,
    private val fileDataSource: FileDataSource,


    ) : BaseViewModel(contextProvider, preferencesHelper) {


    private val _uiState: MutableStateFlow<CameraUiState> = MutableStateFlow(CameraUiState.Initial)
    val uiState: StateFlow<CameraUiState> get() = _uiState


    private lateinit var user: User


    init {

        _uiState.value = CameraUiState.Ready(User.Default, fileDataSource.lastPicture).apply {
            this@CameraViewModel.user = user

        }

    }


    fun takePicture(cameraState: CameraState) = with(cameraState) {
        viewModelScope.launch {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> takePicture(
                    fileDataSource.imageContentValues,
                    onResult = ::onImageResult
                )

                else -> takePicture(
                    fileDataSource.getFile("jpg"),
                    ::onImageResult
                )
            }
        }
    }


    private fun captureSuccess() {
        viewModelScope.launch {
            _uiState.update {
                CameraUiState.Ready(user = user, lastPicture = fileDataSource.lastPicture)
            }
        }
    }


    private fun onImageResult(imageResult: ImageCaptureResult) {
        when (imageResult) {
            is ImageCaptureResult.Error -> onError(imageResult.throwable)
            is ImageCaptureResult.Success -> captureSuccess()
        }
    }

    private fun onError(throwable: Throwable?) {
        _uiState.update { CameraUiState.Ready(user, fileDataSource.lastPicture, throwable) }
    }

}

sealed interface CameraUiState {
    data object Initial : CameraUiState
    data class Ready(
        val user: User,
        val lastPicture: File?,
        val throwable: Throwable? = null,
    ) : CameraUiState
}