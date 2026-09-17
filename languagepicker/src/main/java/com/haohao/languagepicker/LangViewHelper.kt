package com.haohao.languagepicker

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.haohao.languagepicker.config.LangDialogConfig
import com.haohao.languagepicker.config.LangRowConfig
import com.haohao.languagepicker.config.LangViewConfig
import com.haohao.languagepicker.dataGenerator.LangDataStoreGenerator
import com.haohao.languagepicker.dialog.LangDialogHelper
import com.haohao.languagepicker.model.LangDataStore
import com.haohao.languagepicker.model.LanguageModel
import java.util.Locale


class LangViewHelper(
    val context: Context,
    val langDataStore: LangDataStore = LangDataStoreGenerator.generate(context),
    val langViewConfig: LangViewConfig = LangViewConfig(),
    val langDialogConfig: LangDialogConfig = LangDialogConfig(),
    val langRowConfig: LangRowConfig = LangRowConfig(),
) {
    private var _selectedLanguage = MutableLiveData<LanguageModel?>().apply {
        value = LanguageModel(code = "en", "English", "\uD83C\uDDE6\uD83C\uDDEA")
    }
    val selectedLanguage: LiveData<LanguageModel?> = _selectedLanguage
    private var viewComponentGroup: ViewComponentGroup? = null
    var onLanguageChangedListener: ((LanguageModel?) -> Unit)? = null
        set(value) {
            field = value
            field?.invoke(selectedLanguage.value)
        }

    init {
        setInitialLang(langViewConfig.initialSelectionLang)


    }

    private fun setInitialLang(
        initialSelection: LangViewConfig.InitialSelectionLang
    ) {
        when (initialSelection) {
            LangViewConfig.InitialSelectionLang.EmptySelection -> clearSelection()
            is LangViewConfig.InitialSelectionLang.AutoDetectLange -> setAutoDetectedLang()
            is LangViewConfig.InitialSelectionLang.SpecificLang -> setLangForAlphaCode(
                initialSelection.langCode
            )
        }
    }

    /**
     * CountryCode can be alpha2 or alpha3 code
     */
    fun setLangForAlphaCode(langCode: String?) {
        val country = langDataStore.languageList.firstOrNull {
            it.code.equals(langCode, true)
        }
        setLanguage(country)
    }

    private fun clearSelection() {
        setLanguage(null)
    }


    private fun getDeviceLanguage(context: Context): String {
        val locale: Locale =
            context.resources.configuration.locales.get(0)
        return locale.language
    }

    private fun setAutoDetectedLang() {
        val detectedAlpha2 = getDeviceLanguage(context)
        val detectedLang = langDataStore.languageList.firstOrNull {
            it.code.lowercase(Locale.ROOT) == detectedAlpha2.lowercase(Locale.ROOT)
        }
        setLanguage(detectedLang)
    }

    private fun launchDialog() {


        val dialogHelper = LangDialogHelper(
            langDataStore = langDataStore,
            langDialogConfig = langDialogConfig,
            langRowConfig = langRowConfig,
            langSelect = _selectedLanguage.value!!,
            onCLangClickListener = { setLanguage(it) }
        )
        dialogHelper.createDialog(context).show()
    }


    private fun setLanguage(lang: LanguageModel?) {
        _selectedLanguage.value = lang
        refreshView()
        onLanguageChangedListener?.invoke(lang)
    }

    fun refreshView() {
        val selectedLanguage = _selectedLanguage.value
        viewComponentGroup?.apply {
            // text
            tvNameLang.text =
                if (selectedLanguage != null) langViewConfig.viewTextGenerator(selectedLanguage) else "EN"
            tvEmojiFlagLang.text = selectedLanguage?.emoji

            ivDropdownLang.visibility =
                if (langViewConfig.cpFlagProvider) View.VISIBLE else View.GONE


            tvNameLang.requestLayout()
            tvNameLang.invalidate()
            tvEmojiFlagLang.requestLayout()
            tvEmojiFlagLang.invalidate()
            container.requestLayout()
            container.invalidate()
        }
    }

    class ViewComponentGroup(
        val container: ViewGroup,
        val tvNameLang: TextView,
        val tvEmojiFlagLang: TextView,
        val ivDropdownLang: ImageView,

        )
}
