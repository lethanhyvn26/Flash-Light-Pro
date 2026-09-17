package flashlight.phichung.com.torch.ui.screen.preview

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import flashlight.phichung.com.torch.base.BaseViewModel
import flashlight.phichung.com.torch.data.CachePreferencesHelper
import flashlight.phichung.com.torch.ui.screen.preview.PreviewNavigation.pathArg
import flashlight.phichung.com.torch.utils.CoroutineContextProvider
import flashlight.phichung.com.torch.utils.delete
import flashlight.phichung.com.torch.utils.isVideo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject


@HiltViewModel
class PreviewViewModel @Inject constructor(
    contextProvider: CoroutineContextProvider,
    private val preferencesHelper: CachePreferencesHelper,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(contextProvider, preferencesHelper) {

    private val _uiState = MutableStateFlow<PreviewUiState>(PreviewUiState.Initial)
    val uiState: StateFlow<PreviewUiState> = _uiState

    init {
        viewModelScope.launch {
            flow {
                val path = savedStateHandle.get<String?>(pathArg)
                    ?: return@flow emit(PreviewUiState.Empty)

                Timber.d("HAOHAO PATH: $path")
                val file = File(Uri.decode(path))
                emit(PreviewUiState.Ready(file, file.isVideo))
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun deleteFile(
        context: Context,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>,
        file: File
    ) {
        viewModelScope.launch {
            file.delete(context.contentResolver, intentSenderLauncher)
            if (!file.exists()) {
                _uiState.value = PreviewUiState.Deleted
            }
        }
    }
}

sealed interface PreviewUiState {
    data object Initial : PreviewUiState
    data object Empty : PreviewUiState
    data object Deleted : PreviewUiState

    data class Ready(val file: File, val isVideo: Boolean) : PreviewUiState
}

