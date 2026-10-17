package flashlight.phichung.com.torch.camera.data.local.datasource

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

data class CapturedMedia(
    val uri: Uri,
    val name: String,
    val isVideo: Boolean,
    val dateAdded: Long,
)

@Singleton
class FileDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val externalDir = "${Environment.DIRECTORY_DCIM}${File.separator}$RELATIVE_PATH"

    private val currentFileName: String
        get() = "${System.currentTimeMillis()}-${UUID.randomUUID()}"

    private val externalStorage
        get() = Environment.getExternalStoragePublicDirectory(externalDir).apply { mkdirs() }

    fun listCaptured(): List<CapturedMedia> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        queryOwnedMedia()
    } else {
        listLegacyFiles()
    }

    fun latestCaptured(): CapturedMedia? = listCaptured().firstOrNull()

    fun getFile(extension: String = "jpg"): File =
        File(externalStorage.path, "$currentFileName.$extension").apply {
            if (parentFile?.exists() == false) parentFile?.mkdirs()
            createNewFile()
        }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun newImageContentValues(): ContentValues = contentValues(JPEG_MIME_TYPE)

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun queryOwnedMedia(): List<CapturedMedia> {
        val images = queryCollection(
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL),
            isVideo = false,
        )
        val videos = queryCollection(
            collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL),
            isVideo = true,
        )
        return (images + videos).sortedByDescending { it.dateAdded }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun queryCollection(collection: Uri, isVideo: Boolean): List<CapturedMedia> {
        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATE_ADDED,
        )
        val selection =
            "${MediaStore.MediaColumns.RELATIVE_PATH} LIKE ? AND ${MediaStore.MediaColumns.IS_PENDING} = ?"
        val selectionArgs = arrayOf("$externalDir%", "0")
        val sort = "${MediaStore.MediaColumns.DATE_ADDED} DESC"
        return try {
            context.contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                sort,
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)
                buildList {
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        add(
                            CapturedMedia(
                                uri = ContentUris.withAppendedId(collection, id),
                                name = cursor.getString(nameColumn).orEmpty(),
                                isVideo = isVideo,
                                dateAdded = cursor.getLong(dateColumn),
                            )
                        )
                    }
                }
            }.orEmpty()
        } catch (exception: Exception) {
            Timber.e(exception, "Unable to list captured media")
            emptyList()
        }
    }

    private fun listLegacyFiles(): List<CapturedMedia> =
        externalStorage.listFiles()
            ?.sortedByDescending { it.lastModified() }
            ?.map { file ->
                CapturedMedia(
                    uri = Uri.fromFile(file),
                    name = file.name,
                    isVideo = file.extension.equals("mp4", ignoreCase = true),
                    dateAdded = file.lastModified(),
                )
            }
            .orEmpty()

    private fun contentValues(mimeType: String) = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, currentFileName)
        put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        put(MediaStore.MediaColumns.RELATIVE_PATH, externalDir)
    }

    companion object {
        private const val JPEG_MIME_TYPE = "image/jpeg"
        private const val RELATIVE_PATH = "flashlight"
    }
}
