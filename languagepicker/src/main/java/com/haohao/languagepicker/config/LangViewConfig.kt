package com.haohao.languagepicker.config


import com.haohao.languagepicker.model.LanguageModel

data class LangViewConfig(
    val initialSelectionLang: InitialSelectionLang = defaultCPInitialSelectionLangMode,
    var viewTextGenerator: ((LanguageModel) -> String) = defaultSelectedLangInfoTextGenerator,
    var cpFlagProvider: Boolean = defaultShowFlag
) {
    companion object {
        const val defaultShowFlag = true
        val defaultCPInitialSelectionLangMode = InitialSelectionLang.EmptySelection
        val defaultSelectedLangInfoTextGenerator: ((LanguageModel) -> String) = { it.name }
    }

    sealed class InitialSelectionLang {
        data object EmptySelection : InitialSelectionLang()

        data object AutoDetectLange : InitialSelectionLang()

        class SpecificLang(val langCode: String) : InitialSelectionLang()
    }
}
