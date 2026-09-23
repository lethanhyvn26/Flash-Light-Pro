package flashlight.phichung.com.torch.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import flashlight.phichung.com.torch.data.model.Skin
import javax.inject.Inject

open class CachePreferencesHelper @Inject constructor(context: Context) {

    companion object {
        private const val PREF_PACKAGE_NAME = "flashlight.phichung.com.torch.preferences"
        private const val PREF_KEY_LANGUAGE_APP = "language_app"
        private const val PREF_KEY_SOUND = "sound_on_of_app"
        private const val PREF_KEY_SKIN = "skin_app"
        private const val PREF_KEY_AUTOMATIC_ON = "flash_light_automatic_on"
        private const val PREF_KEY_REMOVE_ADS = "flash_light_remove_ads"


    }

    private val gson = Gson()

    // security-crypto 1.1.0 deprecates these types. They are still the only API that
    // reads the encrypted file already on disk, so existing settings stay readable.
    @Suppress("DEPRECATION")
    var masterKey: MasterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    @Suppress("DEPRECATION")
    var preferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREF_PACKAGE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )


//    private val preferences: SharedPreferences =
//        context.getSharedPreferences(PREF_PACKAGE_NAME, Context.MODE_PRIVATE)


    var languageApp: String
        get() = preferences.getString(PREF_KEY_LANGUAGE_APP, "").toString()
        set(language) = preferences.edit { putString(PREF_KEY_LANGUAGE_APP, language) }


    var stateSound: Boolean
        get() = preferences.getBoolean(PREF_KEY_SOUND, true)
        set(sound) = preferences.edit { putBoolean(PREF_KEY_SOUND, sound) }


    var skin: Skin?
        get() {
            val json = preferences.getString(PREF_KEY_SKIN, null)
            return gson.fromJson(json, Skin::class.java)
        }
        set(value) {
            val json = gson.toJson(value)
            preferences.edit { putString(PREF_KEY_SKIN, json) }
        }


    var stateAutomaticOn: Boolean
        get() = preferences.getBoolean(PREF_KEY_AUTOMATIC_ON, true)
        set(isOn) = preferences.edit { putBoolean(PREF_KEY_AUTOMATIC_ON, isOn) }


    var stateRemoveAds: Boolean  // true => remove ads (paid)
        get() = preferences.getBoolean(PREF_KEY_REMOVE_ADS, false)
        set(sound) = preferences.edit { putBoolean(PREF_KEY_REMOVE_ADS, sound) }


    fun clearCache() {
        preferences.edit { clear() }
    }


}
