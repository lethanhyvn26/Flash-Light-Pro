package com.haohao.languagepicker.model

data class LangDataStore(
    var languageList: MutableList<LanguageModel>,
    var langSelect: LanguageModel? = null,
)
