package flashlight.phichung.com.torch.ui.screen.gallery

import dagger.hilt.android.lifecycle.HiltViewModel
import flashlight.phichung.com.torch.base.BaseViewModel
import flashlight.phichung.com.torch.camera.data.local.datasource.FileDataSource
import flashlight.phichung.com.torch.data.CachePreferencesHelper
import flashlight.phichung.com.torch.utils.CoroutineContextProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import java.io.File
import javax.inject.Inject


@HiltViewModel
class GalleryViewModel @Inject constructor(
    contextProvider: CoroutineContextProvider,
    private val preferencesHelper: CachePreferencesHelper,
    private val fileDataSource: FileDataSource,
) : BaseViewModel(contextProvider, preferencesHelper) {

    private val _uiState = MutableStateFlow<GalleryUiState>(GalleryUiState.Initial)
    val uiState: StateFlow<GalleryUiState> get() = _uiState


    fun fetchGallery() {
        Timber.i("HAOHAO GalleryViewModel init: ${fileDataSource.externalFiles}")
        fileDataSource.externalFiles.orEmpty().run {
            if (isEmpty()) GalleryUiState.Empty else GalleryUiState.Success(this)
        }.also {
            _uiState.value = it
        }

    }
}

sealed interface GalleryUiState {
    data object Initial : GalleryUiState
    data object Empty : GalleryUiState
    data class Success(val images: List<File>) : GalleryUiState
}