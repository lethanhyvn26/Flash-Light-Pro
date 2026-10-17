package flashlight.phichung.com.torch.ui.screen.preview

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import flashlight.phichung.com.torch.base.BaseViewModel
import flashlight.phichung.com.torch.data.CachePreferencesHelper
import flashlight.phichung.com.torch.ui.screen.preview.PreviewNavigation.pathArg
import flashlight.phichung.com.torch.utils.CoroutineContextProvider
import flashlight.phichung.com.torch.utils.deleteMedia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import java.io.File
import javax.inject.Inject


@HiltViewModel
class PreviewViewModel @Inject constructor(
    contextProvider: CoroutineContextProvider,
    private val preferencesHelper: CachePreferencesHelper,
    @ApplicationContext private val appContext: Context,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(contextProvider, preferencesHelper) {

    private val _uiState = MutableStateFlow<PreviewUiState>(PreviewUiState.Initial)
    val uiState: StateFlow<PreviewUiState> = _uiState

    init {
        launchCoroutineIO {
            val path = savedStateHandle.get<String>(pathArg)
            if (path.isNullOrBlank()) {
                _uiState.value = PreviewUiState.Empty
                return@launchCoroutineIO
            }

            Timber.d("HAOHAO PATH: $path")
            val uri = path.toCapturedUri()
            val mime = appContext.contentResolver.getType(uri)
            val isVideo = mime?.startsWith("video/") == true ||
                uri.lastPathSegment?.endsWith(".mp4", ignoreCase = true) == true
            _uiState.value = PreviewUiState.Ready(
                uri = uri,
                name = displayName(uri),
                isVideo = isVideo,
            )
        }
    }

    fun deleteFile(
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>,
        uri: Uri,
    ) {
        launchCoroutineIO {
            val deleted = deleteMedia(appContext.contentResolver, uri, intentSenderLauncher)
            if (deleted) {
                _uiState.value = PreviewUiState.Deleted
            }
        }
    }

    private fun displayName(uri: Uri): String {
        if (uri.scheme == ContentResolver.SCHEME_FILE) {
            return File(uri.path.orEmpty()).name
        }
        return appContext.contentResolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null,
            null,
        )?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0) else null
        } ?: "photo"
    }
}

private fun String.toCapturedUri(): Uri {
    val parsed = Uri.parse(this)
    return if (parsed.scheme.isNullOrEmpty()) Uri.fromFile(File(this)) else parsed
}

sealed interface PreviewUiState {
    data object Initial : PreviewUiState
    data object Empty : PreviewUiState
    data object Deleted : PreviewUiState

    data class Ready(val uri: Uri, val name: String, val isVideo: Boolean) : PreviewUiState
}
