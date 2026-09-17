package com.haohao.languagepicker.recyclerview

import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.haohao.languagepicker.NpaLinearLayoutManager
import com.haohao.languagepicker.config.LangRowConfig
import com.haohao.languagepicker.model.LangDataStore
import com.haohao.languagepicker.model.LanguageModel

class LangRecyclerViewHelper(
    private val cpDataStore: LangDataStore,
    private val cpRowConfig: LangRowConfig = LangRowConfig(),
    onCountryClickListener: ((LanguageModel) -> Unit),
    private val recyclerView: RecyclerView,
    countrySelect: LanguageModel

) {
    private val additionalSearchTerm = mutableMapOf<String, String>()

    val epoxyController by lazy { LangListController() }

    val controllerData = LangListControllerData(
        cpDataStore.languageList,
        onCountryClickListener,
        cpRowConfig,
        cpDataStore,
        countrySelect
    )

    fun attachFilterQueryEditText(queryEditText: EditText?) {
        queryEditText?.doOnTextChanged { query, _, _, _ ->
            updateViewForQuery(query.toString())
        }
    }

    private fun updateViewForQuery(query: String) {
        updateDataForQuery(query)
        epoxyController.setData(controllerData)

    }

    fun updateDataForQuery(query: String) {
//        controllerData.preferredCountries = if (query.isBlank()) allPreferredCountries else emptyList()


        controllerData.allLanguage = cpDataStore.languageList.filterCountries(query, cpRowConfig)
    }


    fun attachRecyclerView() {
        recyclerView.layoutManager = NpaLinearLayoutManager(recyclerView.context)
        epoxyController.setData(controllerData)
        recyclerView.adapter = epoxyController.adapter
    }


    private fun List<LanguageModel>.filterCountries(
        filterQuery: String,
        cpRowConfig: LangRowConfig
    ): List<LanguageModel> {
        if (filterQuery.isBlank()) return this

        return this.filter { cpCountry ->
            val firstCharsOfWords by lazy {
                additionalSearchTerm.getOrPut(cpCountry.code) {
                    cpRowConfig.primaryTextGenerator(cpCountry).split(" ")
                        .filter { it.isNotBlank() }
                        .map { it[0] }.filter { it.isUpperCase() }.joinToString("")
                }
            }

            cpCountry.code.startsWith(filterQuery, true) ||
                    firstCharsOfWords.contains(filterQuery, true) ||
                    cpRowConfig.primaryTextGenerator(cpCountry).contains(filterQuery, true) ||
                    cpRowConfig.secondaryTextGenerator?.invoke(cpCountry)
                        ?.contains(filterQuery, true)
                    ?: false ||
                    cpRowConfig.highlightedTextGenerator?.invoke(cpCountry)
                        ?.contains(filterQuery, true)
                    ?: false ||
                    normalizeText(cpCountry.name).contains(normalizeText(filterQuery), true)


        }
    }

    private fun normalizeText(text: String): String {     // remove diacritic marks from Vietnamese text
        val diacriticMap = mapOf(
            'à' to 'a', 'á' to 'a', 'ạ' to 'a', 'ả' to 'a', 'ã' to 'a',
            'ầ' to 'a', 'ấ' to 'a', 'ậ' to 'a', 'ẩ' to 'a', 'ẫ' to 'a',
            'è' to 'e', 'é' to 'e', 'ẹ' to 'e', 'ẻ' to 'e', 'ẽ' to 'e',
            'ề' to 'e', 'ế' to 'e', 'ệ' to 'e', 'ể' to 'e', 'ễ' to 'e',
            'ì' to 'i', 'í' to 'i', 'ị' to 'i', 'ỉ' to 'i', 'ĩ' to 'i',
            'ò' to 'o', 'ó' to 'o', 'ọ' to 'o', 'ỏ' to 'o', 'õ' to 'o',
            'ồ' to 'o', 'ố' to 'o', 'ộ' to 'o', 'ổ' to 'o', 'ỗ' to 'o',
            'ờ' to 'o', 'ớ' to 'o', 'ợ' to 'o', 'ở' to 'o', 'ỡ' to 'o',
            'ù' to 'u', 'ú' to 'u', 'ụ' to 'u', 'ủ' to 'u', 'ũ' to 'u',
            'ừ' to 'u', 'ứ' to 'u', 'ự' to 'u', 'ử' to 'u', 'ữ' to 'u',
            'ỳ' to 'y', 'ý' to 'y', 'ỵ' to 'y', 'ỷ' to 'y', 'ỹ' to 'y',
            'đ' to 'd'
        )

        val stringBuilder = StringBuilder()

        for (c in text) {
            val normalizedChar = diacriticMap[c.lowercaseChar()] ?: c
            stringBuilder.append(normalizedChar)
        }

        return stringBuilder.toString()
    }

}
