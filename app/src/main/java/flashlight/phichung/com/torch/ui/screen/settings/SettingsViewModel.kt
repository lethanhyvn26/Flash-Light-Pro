package flashlight.phichung.com.torch.ui.screen.settings


import dagger.hilt.android.lifecycle.HiltViewModel
import flashlight.phichung.com.torch.base.BaseViewModel
import flashlight.phichung.com.torch.data.CachePreferencesHelper
import flashlight.phichung.com.torch.utils.CoroutineContextProvider
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    contextProvider: CoroutineContextProvider,
    private val preferencesHelper: CachePreferencesHelper,

    ) : BaseViewModel(contextProvider, preferencesHelper) {


    fun setSoundState(isOn: Boolean) {
        preferencesHelper.stateSound = isOn
    }

    fun getSoundState(): Boolean {
        return preferencesHelper.stateSound
    }

    fun setAutomaticOnState(isOn: Boolean) {
        preferencesHelper.stateAutomaticOn = isOn
    }

    fun getAutomaticOnState(): Boolean {
        return preferencesHelper.stateAutomaticOn
    }


    fun getLanguageApp(): String {
        return preferencesHelper.languageApp
    }

    fun setLanguageApp(language: String) {
        preferencesHelper.languageApp = language
    }
}