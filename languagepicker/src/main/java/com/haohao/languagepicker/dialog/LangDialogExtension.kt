package com.haohao.languagepicker.dialog


import android.content.Context
import com.haohao.languagepicker.config.CPDialogViewIds
import com.haohao.languagepicker.config.LangDialogConfig
import com.haohao.languagepicker.config.LangRowConfig
import com.haohao.languagepicker.dataGenerator.LangDataStoreGenerator
import com.haohao.languagepicker.dataGenerator.LangFileReading
import com.haohao.languagepicker.model.LangDataStore
import com.haohao.languagepicker.model.LanguageModel


fun Context.launchCountryPickerDialog(
    customMasterCountries: String = LangDataStoreGenerator.defaultMasterLang,
    customExcludedCountries: String = LangDataStoreGenerator.defaultExcludedLang,
    countryFileReader: LangFileReading = LangDataStoreGenerator.defaultLangFileReader,
    useCache: Boolean = LangDataStoreGenerator.defaultUseCache,
    customDataStoreModifier: ((LangDataStore) -> Unit)? = null,
    cpFlagProvider: Boolean = LangRowConfig.defaultShowFlag,
    primaryTextGenerator: (LanguageModel) -> String = LangRowConfig.defaultPrimaryTextGenerator,
    secondaryTextGenerator: ((LanguageModel) -> String)? = LangRowConfig.defaultSecondaryTextGenerator,
    highlightedTextGenerator: ((LanguageModel) -> String)? = LangRowConfig.defaultHighlightedTextGenerator,
    dialogViewIds: CPDialogViewIds = LangDialogConfig.defaultCPDialogViewIds,
    allowSearch: Boolean = LangDialogConfig.defaultCPDialogAllowSearch,
    allowClearSelection: Boolean = LangDialogConfig.defaultCPDialogAllowClearSelection,
    showTitle: Boolean = LangDialogConfig.defaultCPDialogDefaultShowTitle,
    showFullScreen: Boolean = LangDialogConfig.defaultCPDialogShowFullScreen,
    onCountryClickListener: (LanguageModel?) -> Unit,
    langCodeSelect: String = "en",

    ) {
    val cpDataStore = LangDataStoreGenerator.generate(
        context = this,
        customMasterLang = customMasterCountries,
        customExcludedLang = customExcludedCountries,
        langFileReader = countryFileReader,
        useCache = useCache,
        langCodeSelect = langCodeSelect
    )

    customDataStoreModifier?.invoke(cpDataStore)

    val cpDialogConfig = LangDialogConfig(
        dialogViewIds = dialogViewIds,
        allowSearch = allowSearch,
        allowClearSelection = allowClearSelection,
        showTitle = showTitle,
        showFullScreen = showFullScreen,
    )

    val cpCountryRowConfig = LangRowConfig(
        cpFlagProvider = cpFlagProvider,
        primaryTextGenerator = primaryTextGenerator,
        secondaryTextGenerator = secondaryTextGenerator,
        highlightedTextGenerator = highlightedTextGenerator
    )


    val helper =
        LangDialogHelper(
            langDataStore = cpDataStore,
            langDialogConfig = cpDialogConfig,
            langRowConfig = cpCountryRowConfig,
            onCLangClickListener = onCountryClickListener,
            langSelect = cpDataStore.langSelect ?: LanguageModel(
                code = "en",
                "English",
                "\uD83C\uDDE6\uD83C\uDDEA"
            )
        )

    val dialog = helper.createDialog(this)
    dialog.show()
}
