package com.haohao.languagepicker.dataGenerator

import android.content.Context
import com.haohao.languagepicker.allLanguage
import com.haohao.languagepicker.listLangAppSupport

import com.haohao.languagepicker.model.LangDataStore
import com.haohao.languagepicker.model.LanguageModel


interface LangFileReading {
    fun readMasterDataFromFiles(
        context: Context
    ): LangDataStore
}

object LangFileReader : LangFileReading {
    private var languageList: List<LanguageModel> = allLanguage!!

    override fun readMasterDataFromFiles(context: Context): LangDataStore {
        return LangDataStore(
            languageList.toMutableList(),
        )
    }


}


object LangFileReaderHard : LangFileReading {
    private var languageListHard: List<LanguageModel> =
        allLanguage!!.filter { listLangAppSupport.contains(it.code) }

    override fun readMasterDataFromFiles(context: Context): LangDataStore {


        return LangDataStore(
            languageListHard.toMutableList(),
        )
    }


}
