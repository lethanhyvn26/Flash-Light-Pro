package com.haohao.languagepicker.recyclerview

import com.airbnb.epoxy.TypedEpoxyController
import com.haohao.languagepicker.config.LangRowConfig
import com.haohao.languagepicker.model.LangDataStore
import com.haohao.languagepicker.model.LanguageModel

data class LangListControllerData(
    var allLanguage: List<LanguageModel>,
    val onCountryClickListener: ((LanguageModel) -> Unit),
    val cpRowConfig: LangRowConfig,
    val cpDataStore: LangDataStore,
    val countrySelect: LanguageModel
)

class LangListController : TypedEpoxyController<LangListControllerData>() {

    override fun buildModels(
        data: LangListControllerData
    ) {
        try {


            data.apply {


                allLanguage.forEach { country ->

                    if (countrySelect.code == country.code) {

                        val configRow = cpRowConfig.copy()
                        configRow.isLanguageSelected = true
                        langRow {
                            id("preferredCountry${country.code}")
                                .lang(country)
                                .clickListener(onCountryClickListener)
                                .rowConfig(configRow)
                        }
                    } else {
                        langRow {
                            id("preferredCountry${country.code}")
                                .lang(country)
                                .clickListener(onCountryClickListener)
                                .rowConfig(cpRowConfig)
                        }
                    }
                    dividerRow { id("preferred-divider") }

                }


            }

        } catch (_: Exception) {
        }
    }
}
