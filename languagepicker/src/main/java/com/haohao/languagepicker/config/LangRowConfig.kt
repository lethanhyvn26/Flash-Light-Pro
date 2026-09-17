package com.haohao.languagepicker.config

import com.haohao.languagepicker.model.LanguageModel

data class LangRowConfig(
    var cpFlagProvider: Boolean = defaultShowFlag,
    var primaryTextGenerator: ((LanguageModel) -> String) = defaultPrimaryTextGenerator,
    var secondaryTextGenerator: ((LanguageModel) -> String)? = defaultSecondaryTextGenerator,
    var highlightedTextGenerator: ((LanguageModel) -> String)? = defaultHighlightedTextGenerator,
    var isLanguageSelected: Boolean = false

) {
    companion object {
        const val defaultShowFlag = true
        val defaultPrimaryTextGenerator: ((LanguageModel) -> String) = { it.name }
        val defaultSecondaryTextGenerator = null
        val defaultHighlightedTextGenerator = null
    }

}
