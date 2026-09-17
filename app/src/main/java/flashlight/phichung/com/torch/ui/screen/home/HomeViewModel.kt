package flashlight.phichung.com.torch.ui.screen.home


import android.content.Context
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import flashlight.phichung.com.torch.MyApplication
import flashlight.phichung.com.torch.R
import flashlight.phichung.com.torch.base.BaseViewModel
import flashlight.phichung.com.torch.data.CachePreferencesHelper
import flashlight.phichung.com.torch.data.model.Skin
import flashlight.phichung.com.torch.utils.CoroutineContextProvider
import flashlight.phichung.com.torch.utils.listSkin
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    contextProvider: CoroutineContextProvider,
    private val preferencesHelper: CachePreferencesHelper,

    ) : BaseViewModel(contextProvider, preferencesHelper) {


    private val _uiFlashFloatBlinkState = MutableStateFlow(1f)
    val uiFlashFloatBlinkState: StateFlow<Float> =
        _uiFlashFloatBlinkState.asStateFlow() // speech blink


    private val _uiPowerState = MutableStateFlow(false)
    val uiPowerState: StateFlow<Boolean> = _uiPowerState.asStateFlow()   //on off

    var jobBlink: Job? = null

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.d("HAOHAO coroutineExceptionHandler : $exception")
    }

    private var mpTorchOn: MediaPlayer
    private var mpTorchOff: MediaPlayer

    var camManager: CameraManager? = null
    var cameraId: String? = null
    private var torchCallback: CameraManager.TorchCallback? = null


    init {
        mpTorchOn = MediaPlayer.create(MyApplication.instance, R.raw.sound_on)
        mpTorchOff = MediaPlayer.create(MyApplication.instance, R.raw.sound_off)

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



        setPowerState(preferencesHelper.stateAutomaticOn)
    }

    fun setValueFlashFloatBlinkState(blinkState: Float) {
        _uiFlashFloatBlinkState.value = blinkState
    }


    fun getSkinCurrent(): Skin {
        return preferencesHelper.skin ?: listSkin[0]
    }


    fun setPowerState(powerState: Boolean) {
        _uiPowerState.value = powerState
        isPowerOn = uiPowerState.value

        if (preferencesHelper.stateSound) {
            if (powerState) {
                mpTorchOn.start()
            } else {
                mpTorchOff.start()
            }
        }
        toggleBlinkStateWithDelay()
    }

    fun setSoundState(isOn: Boolean) {
        preferencesHelper.stateSound = isOn
    }

    fun getSoundState(): Boolean {
        return preferencesHelper.stateSound
    }

    private var isPowerOn = false
    fun toggleBlinkStateWithDelay() {

        if (!uiPowerState.value) {
            cameraId?.let { camManager?.setTorchMode(it, false) } // Turn OFF
            jobBlink?.cancel() // Cancel the previous coroutine if it exists
            return
        }
        isPowerOn = false
        jobBlink?.cancel() // Cancel the previous coroutine if it exists


        launchCoroutineIO {
            delay(50)
            isPowerOn = true

            val blinkValue = uiFlashFloatBlinkState.value.toInt()
            Timber.i("blinkValue: " + blinkValue.toString())

            when (blinkValue) {
                0 -> { //SOS
                    jobBlink = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {

                        while (isPowerOn) {
                            try {

                                cameraId?.let { camManager?.setTorchMode(it, true) }   // Turn ON
                                delay(200.toLong())
                                cameraId?.let { camManager?.setTorchMode(it, false) } // Turn OFF

                                delay(200.toLong())
                                cameraId?.let { camManager?.setTorchMode(it, true) }   // Turn ON

                                delay(200.toLong())
                                cameraId?.let { camManager?.setTorchMode(it, false) } // Turn OFF

                                delay(200.toLong())
                                cameraId?.let { camManager?.setTorchMode(it, true) }   // Turn ON

                                delay(200.toLong())
                                cameraId?.let { camManager?.setTorchMode(it, false) } // Turn OFF

                                delay(200.toLong())

                                cameraId?.let { camManager?.setTorchMode(it, true) }   // Turn ON

                                delay(600.toLong())
                                cameraId?.let { camManager?.setTorchMode(it, false) } // Turn OFF

                                delay(200.toLong())
                                cameraId?.let { camManager?.setTorchMode(it, true) }   // Turn ON

                                delay(600.toLong())
                                cameraId?.let { camManager?.setTorchMode(it, false) } // Turn OFF

                                delay(200.toLong())
                                cameraId?.let { camManager?.setTorchMode(it, true) }   // Turn ON

                                delay(600.toLong())
                                cameraId?.let { camManager?.setTorchMode(it, false) } // Turn OFF

                                delay(200.toLong())
                                cameraId?.let { camManager?.setTorchMode(it, true) }   // Turn ON

                                delay(200.toLong())
                                cameraId?.let { camManager?.setTorchMode(it, false) } // Turn OFF

                                delay(200.toLong())
                                cameraId?.let { camManager?.setTorchMode(it, true) }   // Turn ON

                                delay(200.toLong())
                                cameraId?.let { camManager?.setTorchMode(it, false) } // Turn OFF

                                delay(200.toLong())
                                cameraId?.let { camManager?.setTorchMode(it, true) }   // Turn ON

                                delay(200.toLong())

                                cameraId?.let { camManager?.setTorchMode(it, false) } // Turn OFF

                                delay(1400.toLong())
                            } catch (ex: Exception) {
                                Timber.i("HAOHAO jobBlink" + ex.toString())
                            }

                        }
                    }

                }
                1 -> {
                    camManager?.setTorchMode(cameraId!!, true) // Turn ON

                }
                else -> {
                    jobBlink = CoroutineScope(Dispatchers.IO).launch {
                        val delayMs = ((10 - blinkValue) * 100L) // Adjust as needed

                        while (isPowerOn) {
                            try {
                                cameraId?.let { camManager?.setTorchMode(it, true) } // Turn ON
                                delay(delayMs)
                                cameraId?.let { camManager?.setTorchMode(it, false) } // Turn OFF
                                delay(delayMs) // Wait for 1 second
                            } catch (ex: Exception) {
                                Timber.i("HAOHAO jobBlink" + ex.toString())
                            }
                        }
                    }
                }
            }

        }


    }

    fun onDisposeTorch() {

        try {
            _uiPowerState.value = false
            jobBlink?.cancel() // Cancel the previous coroutine if it exists
            cameraId?.let { camManager?.setTorchMode(it, false) } // Turn OFF

            torchCallback?.let { camManager?.unregisterTorchCallback(it) }
        } catch (_: Exception) {
        }

    }


}