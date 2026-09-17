package flashlight.phichung.com.torch.ui.screen.brightness


import dagger.hilt.android.lifecycle.HiltViewModel
import flashlight.phichung.com.torch.base.BaseViewModel
import flashlight.phichung.com.torch.data.CachePreferencesHelper
import flashlight.phichung.com.torch.utils.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BrightnessViewModel @Inject constructor(
    contextProvider: CoroutineContextProvider,
    private val preferencesHelper: CachePreferencesHelper,

    ) : BaseViewModel(contextProvider, preferencesHelper) {


    private val _uiBlinkFloatState = MutableStateFlow(0f)
    val uiBlinkFloatState: StateFlow<Float> = _uiBlinkFloatState.asStateFlow()


    private val _uiIsBlinkState = MutableStateFlow(false)
    val uiIsBlinkState: StateFlow<Boolean> = _uiIsBlinkState.asStateFlow()

    var job: Job? = null


    fun setBlinkState(blinkState: Float) {
        _uiBlinkFloatState.value = blinkState
    }

    fun setIsBlinkState(isBlink: Boolean) {
        _uiIsBlinkState.value = isBlink
    }


    fun toggleBlinkStateWithDelay(sliderPosition: Float) {

        job?.cancel() // Cancel the previous coroutine if it exists
        if (sliderPosition == 0f) {
            setIsBlinkState(false) // turn off blink
            return
        }

        job = CoroutineScope(Dispatchers.IO).launch {

            val delayMs = ((10 - sliderPosition.toInt()) * 120L) // Adjust as needed
            while (true) {
                setIsBlinkState(true)
                delay(delayMs)
                setIsBlinkState(false)
                delay(delayMs) // Wait for 1 second

            }

        }


    }


}