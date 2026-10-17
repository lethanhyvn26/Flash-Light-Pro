package flashlight.phichung.com.torch.utils

import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.WorkerThread
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

@WorkerThread
suspend fun mediaDurationSeconds(
    context: Context,
    uri: Uri,
    isVideo: Boolean,
): Int? = withContext(Dispatchers.IO) {
    if (!isVideo) return@withContext null
    val retriever = MediaMetadataRetriever()
    try {
        if (uri.scheme == ContentResolver.SCHEME_FILE) {
            retriever.setDataSource(uri.path)
        } else {
            retriever.setDataSource(context, uri)
        }
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            ?.toLongOrNull()
            ?.milliseconds
            ?.toInt(DurationUnit.SECONDS)
    } catch (_: Exception) {
        null
    } finally {
        retriever.release()
    }
}

@WorkerThread
suspend fun deleteMedia(
    contentResolver: ContentResolver,
    uri: Uri,
    intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
): Boolean = withContext(dispatcher) {
    if (uri.scheme == ContentResolver.SCHEME_FILE) {
        val path = uri.path ?: return@withContext false
        return@withContext File(path).delete()
    }
    try {
        contentResolver.delete(uri, null, null) > 0
    } catch (exception: SecurityException) {
        val intentSender = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                MediaStore.createDeleteRequest(contentResolver, listOf(uri)).intentSender
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                (exception as? RecoverableSecurityException)
                    ?.userAction
                    ?.actionIntent
                    ?.intentSender
            }

            else -> null
        }
        if (intentSender != null) {
            withContext(Dispatchers.Main) {
                intentSenderLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
            }
        }
        false
    }
}
