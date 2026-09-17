package flashlight.phichung.com.torch.ui.screen.morse


import android.content.Context
import android.hardware.camera2.CameraManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import flashlight.phichung.com.torch.MyApplication
import flashlight.phichung.com.torch.base.BaseViewModel
import flashlight.phichung.com.torch.data.CachePreferencesHelper
import flashlight.phichung.com.torch.data.model.Skin
import flashlight.phichung.com.torch.morse.DitDahGeneratorSettings
import flashlight.phichung.com.torch.morse.DitDahSoundStream
import flashlight.phichung.com.torch.morse.SoundTypes
import flashlight.phichung.com.torch.morse.StringToSoundSequence
import flashlight.phichung.com.torch.ui.theme.TextWhiteColor
import flashlight.phichung.com.torch.utils.CoroutineContextProvider
import flashlight.phichung.com.torch.utils.listSkin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class MorseViewModel @Inject constructor(
    contextProvider: CoroutineContextProvider,
    private val preferencesHelper: CachePreferencesHelper,

    ) : BaseViewModel(contextProvider, preferencesHelper) {


    var camManager: CameraManager? = null
    var cameraId: String? = null
    private var torchCallback: CameraManager.TorchCallback? = null


    private val _uiPlayState = MutableStateFlow(false) // isPlaying
    val uiPlayState: StateFlow<Boolean> = _uiPlayState.asStateFlow()

    private var mSoundPlayer: DitDahSoundStream

    private val _uiMorseCodeState = MutableStateFlow(listOf<SoundTypes>())
    val uiMorseCodeState: StateFlow<List<SoundTypes>> = _uiMorseCodeState.asStateFlow()


    private val _uiIndexCharacterState = MutableStateFlow(-1) // index of character
    private val _uiIndexCharacterInputState = MutableStateFlow(-1) // index of character
    val uiIndexCharacterInputState: StateFlow<Int> = _uiIndexCharacterInputState.asStateFlow()


    private lateinit var skinCurrent: Skin


    val combinedFlow: Flow<AnnotatedString> =
        _uiIndexCharacterState.combine(_uiMorseCodeState) { index, morseCodeList ->


            var count = if (_uiPlayState.value) 0 else -1
            buildAnnotatedString {
                morseCodeList.forEachIndexed { idx, soundTypes ->
                    var color: Color = TextWhiteColor
                    if (idx <= index) {
                        color = skinCurrent.colorSkin
                        if (soundTypes == SoundTypes.LETTER_SPACE || soundTypes == SoundTypes.WORD_SPACE) {
                            count++
                        }


                    }

                    val char = when (soundTypes) {
                        SoundTypes.DIT -> "."
                        SoundTypes.DAH -> "-"
                        SoundTypes.LETTER_SPACE -> " "
                        SoundTypes.WORD_SPACE -> "  "
                        SoundTypes.UNDEFINED -> "*"
                    }
                    withStyle(style = androidx.compose.ui.text.SpanStyle(color = color)) {
                        append(char.toString())
                    }


                }

                Timber.i("HAOHAO  buildAnnotatedString morseCodeList $count  : $morseCodeList")
                if (_uiIndexCharacterInputState.value != count)
                    _uiIndexCharacterInputState.value = count

            }


        }


    init {

        val generatorSettings = DitDahGeneratorSettings(MyApplication.instance)
        mSoundPlayer = DitDahSoundStream(generatorSettings)
        mSoundPlayer.streamNotificationListener =
            object : DitDahSoundStream.StreamNotificationListener {
                override fun symbolsExhausted(stream: DitDahSoundStream) {


                    Timber.i("symbolsExhausted: $stream")
                }

            }
        skinCurrent = getSkinCurrent()


        val cameraManager =
            MyApplication.instance.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        camManager = cameraManager
        try {
            if (camManager != null) {
                cameraId = camManager!!.cameraIdList[0]
            }
        } catch (e: Exception) {
            // Handle exception
        }

        torchCallback = object : CameraManager.TorchCallback() {
            override fun onTorchModeUnavailable(cameraId: String) {
                // Handle torch mode unavailable
            }

            override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                // Handle torch mode changed
            }
        }

        camManager?.registerTorchCallback(torchCallback!!, null)

    }

    fun setValuePlayState(playState: Boolean) {
        _uiPlayState.value = playState
        if (playState) {
            mSoundPlayer.enqueue(uiMorseCodeState.value)
            launchCoroutineIO {
                mSoundPlayer.makeSoundsWorkerThreadFunc(
                    onDone = {
                        Timber.i("HAOHAO makeSoundsWorkerThreadFunc onDone")
                        _uiPlayState.value = false
                        _uiIndexCharacterState.value = -1
                        _uiIndexCharacterInputState.value = -1

                    },
                    onCharacter = _uiIndexCharacterState,
                    camManager = camManager,
                    cameraId = cameraId
                )

            }
        } else {
            _uiIndexCharacterState.value = -1
            _uiIndexCharacterInputState.value = -1
            mSoundPlayer.quit()
        }

    }

    fun setStateSound(isOn: Boolean) {

        mSoundPlayer.setSoundState(isOn)
    }

    fun setFlashlight(isOn: Boolean) {

        mSoundPlayer.setFlashState(isOn)
    }


    fun generateTextMorse(text: String) {
        Timber.i("HAOHAO $uiMorseCodeState : $text")
        _uiMorseCodeState.value = StringToSoundSequence(text.uppercase(Locale.ROOT))
    }


    fun getSkinCurrent(): Skin {
        return preferencesHelper.skin ?: listSkin[0]
    }


    fun onDispose() {
        setValuePlayState(false)

        try {
            cameraId?.let { camManager?.setTorchMode(it, false) } // Turn OFF

            torchCallback?.let { camManager?.unregisterTorchCallback(it) }
        } catch (ex: Exception) {
        }

    }


}