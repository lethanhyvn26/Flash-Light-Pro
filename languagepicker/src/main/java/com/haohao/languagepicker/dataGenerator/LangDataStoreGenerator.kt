package com.haohao.languagepicker.dataGenerator

import android.content.Context
import com.haohao.languagepicker.model.LangDataStore
import com.haohao.languagepicker.model.LanguageModel
import java.util.Locale


object LangDataStoreGenerator {
    private var masterDataStore: LangDataStore? = null
    const val defaultMasterLang = ""
    const val defaultExcludedLang = ""
    const val defaultUseCache = true
    val defaultLangFileReader = LangFileReader

    fun generate(
        context: Context,
        customMasterLang: String = defaultMasterLang,
        customExcludedLang: String = defaultExcludedLang,
        langFileReader: LangFileReading = defaultLangFileReader,
        useCache: Boolean = defaultUseCache,
        langCodeSelect: String = "en",

        ): LangDataStore {
//        if (masterDataStore == null || !useCache) {
//            masterDataStore = langFileReader.readMasterDataFromFiles(context)
//        }
        masterDataStore = langFileReader.readMasterDataFromFiles(context)

        masterDataStore?.let {
            var langList =
                filterCustomMasterList(
                    it.languageList,
                    customMasterLang
                )
            langList =
                filterExcludedCountriesList(
                    langList,
                    customExcludedLang
                )

            it.langSelect = getLanguageSelect(langList, langCodeSelect)
            return it.copy(
                languageList = langList.sortedBy { cpCountry -> cpCountry.name }.toMutableList()
            )
        }

        throw IllegalStateException("MasterDataStore can not be null at this point.")
    }

    private fun filterExcludedCountriesList(
        langList: List<LanguageModel>,
        customExcluded: String
    ): List<LanguageModel> {
        val countryAlphaCodes =
            customExcluded.split(",").map { it.trim().uppercase(Locale.US) }
        val filteredCountries = langList.filterNot {
            countryAlphaCodes.contains(it.code)
        }
        return filteredCountries.ifEmpty {
            langList
        }
    }

    private fun getLanguageSelect(
        langList: List<LanguageModel>,
        languageCodeSelect: String
    ): LanguageModel? {

        val indexOf = langList.firstOrNull {
            languageCodeSelect.contains(it.code)
        }
        return indexOf
    }

    private fun filterCustomMasterList(
        masterCountryList: List<LanguageModel>,
        customExcludedLang: String
    ): List<LanguageModel> {
        val countryAlphaCodes =
            customExcludedLang.split(",").map { it.trim().uppercase(Locale.US) }
        val customMaster = masterCountryList.filter {
            countryAlphaCodes.contains(it.code)
        }

        /**
         * If there is no valid master country, return default master list
         */
        return customMaster.ifEmpty {
            masterCountryList
        }
    }

    fun invalidateCache() {
        masterDataStore = null
    }
}
