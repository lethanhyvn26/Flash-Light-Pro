package com.haohao.languagepicker

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.haohao.languagepicker.model.LanguageModel
import java.util.Locale


var allLanguage: List<LanguageModel>? = null
val languageToCountryMap = mapOf(
    "af" to "ZA",
    "agq" to "CM",
    "ak" to "GH",
    "am" to "ET",
    "ar" to "SA",
    "as" to "IN",
    "asa" to "TZ",
    "ast" to "ES",
    "az" to "AZ",
    "bas" to "CM",
    "be" to "BY",
    "bem" to "ZM",
    "bez" to "TZ",
    "bg" to "BG",
    "bm" to "ML",
    "bn" to "BD",
    "bo" to "CN",
    "br" to "FR",
    "brx" to "IN",
    "bs" to "BA",
    "ca" to "ES",
    "ccp" to "BD",
    "ce" to "RU",
    "ceb" to "PH",
    "cgg" to "UG",
    "chr" to "US",
    "ckb" to "IQ",
    "cs" to "CZ",
    "cy" to "GB",
    "da" to "DK",
    "dav" to "KE",
    "de" to "DE",
    "dje" to "NE",
    "dsb" to "DE",
    "dua" to "CM",
    "dyo" to "SN",
    "dz" to "BT",
    "ebu" to "KE",
    "ee" to "GH",
    "el" to "GR",
    "en" to "US",
    "eo" to "EO",
    "es" to "ES",
    "et" to "EE",
    "eu" to "ES",
    "ewo" to "CM",
    "fa" to "IR",
    "ff" to "SN",
    "fi" to "FI",
    "fil" to "PH",
    "fo" to "FO",
    "fr" to "FR",
    "fur" to "IT",
    "fy" to "NL",
    "ga" to "IE",
    "gd" to "GB",
    "gl" to "ES",
    "gsw" to "CH",
    "gu" to "IN",
    "guz" to "KE",
    "gv" to "GB",
    "ha" to "NG",
    "haw" to "US",
    "iw" to "IL",
    "hi" to "IN",
    "hr" to "HR",
    "hsb" to "DE",
    "hy" to "AM",
    "ia" to "IA",
    "in" to "ID",
    "ig" to "NG",
    "ii" to "CN",
    "is" to "IS",
    "it" to "IT",
    "ja" to "JP",
    "jgo" to "CM",
    "jmc" to "TZ",
    "jv" to "ID",
    "ka" to "GE",
    "kab" to "DZ",
    "kam" to "KE",
    "kde" to "TZ",
    "kea" to "CV",
    "khq" to "ML",
    "ki" to "KE",
    "kk" to "KZ",
    "kkj" to "CM",
    "kl" to "GL",
    "kln" to "KE",
    "km" to "KH",
    "kn" to "IN",
    "ko" to "KR",
    "kok" to "IN",
    "ks" to "IN",
    "ksb" to "TZ",
    "ksf" to "CM",
    "ksh" to "DE",
    "ku" to "TR",
    "kw" to "GB",
    "ky" to "KG",
    "lag" to "TZ",
    "lb" to "LU",
    "lg" to "UG",
    "lkt" to "US",
    "ln" to "CD",
    "lo" to "LA",
    "lrc" to "IR",
    "lt" to "LT",
    "lu" to "CD",
    "luo" to "KE",
    "luy" to "KE",
    "lv" to "LV",
    "mas" to "TZ",
    "mer" to "KE",
    "mfe" to "MU",
    "mg" to "MG",
    "mgh" to "MZ",
    "mgo" to "CM",
    "mi" to "NZ",
    "mk" to "MK",
    "ml" to "IN",
    "mn" to "MN",
    "mr" to "IN",
    "ms" to "MY",
    "mt" to "MT",
    "mua" to "CM",
    "my" to "MM",
    "mzn" to "IR",
    "naq" to "NA",
    "nb" to "NO",
    "nd" to "ZW",
    "nds" to "DE",
    "ne" to "NP",
    "nl" to "NL",
    "nmg" to "CM",
    "nn" to "NO",
    "nnh" to "CM",
    "nus" to "SD",
    "nyn" to "UG",
    "om" to "ET",
    "or" to "IN",
    "os" to "RU",
    "pa" to "IN",
    "pl" to "PL",
    "ps" to "AF",
    "pt" to "PT",
    "qu" to "PE",
    "rm" to "CH",
    "rn" to "BI",
    "ro" to "RO",
    "rof" to "TZ",
    "ru" to "RU",
    "rw" to "RW",
    "rwk" to "TZ",
    "sah" to "RU",
    "saq" to "KE",
    "sbp" to "TZ",
    "sd" to "PK",
    "se" to "NO",
    "seh" to "MZ",
    "ses" to "ML",
    "sg" to "CF",
    "shi" to "MA",
    "si" to "LK",
    "sk" to "SK",
    "sl" to "SI",
    "smn" to "FI",
    "sn" to "ZW",
    "so" to "SO",
    "sq" to "AL",
    "sr" to "RS",
    "sv" to "SE",
    "sw" to "TZ",
    "ta" to "IN",
    "te" to "IN",
    "teo" to "UG",
    "tg" to "TJ",
    "th" to "TH",
    "ti" to "ER",
    "tk" to "TM",
    "to" to "TO",
    "tr" to "TR",
    "tt" to "RU",
    "twq" to "NE",
    "tzm" to "MA",
    "ug" to "CN",
    "uk" to "UA",
    "ur" to "PK",
    "uz" to "UZ",
    "vai" to "LR",
    "vi" to "VN",
    "vun" to "TZ",
    "wae" to "CH",
    "wo" to "SN",
    "xh" to "ZA",
    "xog" to "UG",
    "yav" to "CM",
    "ji" to "IL",
    "yo" to "NG",
    "yue" to "HK",
    "zgh" to "MA",
    "zh" to "CN",
    "zu" to "ZA"
)

fun getCountryCodeFromLanguageCode(languageCode: String): String {
    return languageToCountryMap[languageCode] ?: ""
}

fun getFlagEmojiBaseCountryCode(countryCode: String): String {
    if (countryCode.length != 2) {
        return "" // Invalid country code, return empty string
    }

    val countryCodeUpperCase = countryCode.uppercase(Locale.ROOT)
    val flagOffset = 0x1F1E6 - 0x41 // Offset for letter 'A' in Unicode

    val firstChar = Character.codePointAt(countryCodeUpperCase, 0) + flagOffset
    val secondChar = Character.codePointAt(countryCodeUpperCase, 1) + flagOffset

    return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
}

fun getCurrentLanguageCode(): String {
    val currentLocale: Locale = Locale.getDefault()
    return currentLocale.language
}


fun setApplicationLocales(tagsLanguageApp: String) {

    Log.i("HAO-HAO", "tagsLanguageApp :  $tagsLanguageApp")
    AppCompatDelegate.setApplicationLocales(
        LocaleListCompat.forLanguageTags(tagsLanguageApp)
    )
}


fun initLanguage() {
    val allLanguageA = Locale.getAvailableLocales()
        .mapNotNull { locale ->
            val language = locale.displayLanguage
            val languageCode = locale.language
            val countryCode = getCountryCodeFromLanguageCode(languageCode)
            val emojiFlag = getFlagEmojiBaseCountryCode(countryCode)
            if (language.isNotEmpty() && emojiFlag.isNotEmpty() && languageCode.isNotEmpty()) {
                LanguageModel(languageCode, language, emojiFlag)
            } else {
                null
            }
        }.distinctBy { it.code }

    allLanguage = allLanguageA.toMutableList()
}

val listLangAppSupport = arrayListOf(
    "en",
    "zh",
    "es",
    "hi",
    "fr",
//    "ar",
    "ru",
    "pt",
    "ja",
    "vi",
    "de",
    "it",
    "ko",
    "tr",
    "in",
    "pl"
)

val localesLanguageApp = listLangAppSupport.map { Locale.forLanguageTag(it) }.toTypedArray()
