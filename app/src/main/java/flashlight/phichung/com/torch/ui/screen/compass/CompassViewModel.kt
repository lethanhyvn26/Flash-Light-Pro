package flashlight.phichung.com.torch.ui.screen.compass


import dagger.hilt.android.lifecycle.HiltViewModel
import flashlight.phichung.com.torch.base.BaseViewModel
import flashlight.phichung.com.torch.compass.RotationRepository
import flashlight.phichung.com.torch.data.CachePreferencesHelper
import flashlight.phichung.com.torch.utils.CoroutineContextProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


/**
 * rotation update frequency
 */
const val UPDATE_FREQUENCY = 250
const val MINIMAL_DIFFERENCE_TO_LOAD_ADDRESS = 20 // meters

@HiltViewModel
class CompassViewModel @Inject constructor(
    contextProvider: CoroutineContextProvider,
    private val preferencesHelper: CachePreferencesHelper,
    private val rotationRepository: RotationRepository,

    ) : BaseViewModel(contextProvider, preferencesHelper) {


    private var lastRotationUpdateTime = 0L
    private var lastSavedRotation = 0

    /**
     * rotation is changing to fast and very irregularly
     * it jumps in range of 10 degrees
     * average doesn't help
     */
    val rotation: Flow<Int> = rotationRepository.rotationFlow.map { r ->
        val c = System.currentTimeMillis()

        if (lastRotationUpdateTime + UPDATE_FREQUENCY < c) {
            lastRotationUpdateTime = c
            lastSavedRotation = r
        }

        lastSavedRotation
    }


    fun startRotationUpdates() = rotationRepository.startObservingRotation()
    fun stopRotationUpdates() = rotationRepository.stopObservingRotation()

}