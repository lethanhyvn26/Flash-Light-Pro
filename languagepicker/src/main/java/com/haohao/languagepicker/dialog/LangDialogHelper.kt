package com.haohao.languagepicker.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.haohao.languagepicker.config.LangDialogConfig
import com.haohao.languagepicker.config.LangRowConfig
import com.haohao.languagepicker.model.LangDataStore
import com.haohao.languagepicker.model.LanguageModel
import com.haohao.languagepicker.recyclerview.LangRecyclerViewHelper


class LangDialogHelper(
    private val langDataStore: LangDataStore,
    private val langDialogConfig: LangDialogConfig,
    private val langRowConfig: LangRowConfig,
    private val langSelect: LanguageModel,
    val onCLangClickListener: (LanguageModel?) -> Unit,

    ) {

    fun createDialog(context: Context): Dialog {
        val dialog = Dialog(context, android.R.style.Theme_Translucent_NoTitleBar)
        dialog.setCancelable(true)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogView =
            LayoutInflater.from(context)
                .inflate(langDialogConfig.dialogViewIds.layoutId, null, false)
        dialog.window?.setContentView(dialogView)
        val window = dialog.window
        val wlp = window!!.attributes

        wlp.gravity = Gravity.CENTER
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND.inv()
        window.attributes = wlp
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

//
//        val containerViewGroup = dialogView.findViewById<ViewGroup>(langDialogConfig.dialogViewIds.containerId)
        val recyclerView: RecyclerView =
            dialogView.findViewById(langDialogConfig.dialogViewIds.recyclerViewId)
        val etQuery: EditText? =
            langDialogConfig.dialogViewIds.queryEditTextId?.let { etQueryId ->
                dialogView.findViewById(
                    etQueryId
                )
            }
        val imgClearQuery: ImageView? =
            langDialogConfig.dialogViewIds.clearQueryImageViewId?.let { imgClearQueryId ->
                dialogView.findViewById(imgClearQueryId)
            }
        val tvTitle: TextView? = langDialogConfig.dialogViewIds.titleTextViewId?.let { tvTitleId ->
            dialogView.findViewById(tvTitleId)

        }

        val ivBack: ImageView? = langDialogConfig.dialogViewIds.ivBack?.let { imgClearQueryId ->
            dialogView.findViewById(imgClearQueryId)
        }

        val cardSearch: CardView? = langDialogConfig.dialogViewIds.cardSearch?.let { cardSearchId ->
            dialogView.findViewById(cardSearchId)
        }
        cardSearch?.isVisible = langDialogConfig.allowSearch
        // configuration
        fun refreshClearQueryButton() {
            imgClearQuery?.isVisible = etQuery?.text?.isNotEmpty() ?: false
        }
        ivBack?.setOnClickListener { dialog.dismiss() }
        tvTitle?.isVisible = langDialogConfig.showTitle
        imgClearQuery?.setOnClickListener {
            etQuery?.setText("")
        }
        //   etQuery?.isVisible = langDialogConfig.allowSearch
        etQuery?.doOnTextChanged { _, _, _, _ -> refreshClearQueryButton() }
        refreshClearQueryButton()

        recyclerView.apply {
            val cpRecyclerViewHelper =
                LangRecyclerViewHelper(
                    cpDataStore = langDataStore,
                    cpRowConfig = langRowConfig,
                    recyclerView = this,
                    countrySelect = langSelect,
                    onCountryClickListener = {
                        onCLangClickListener(it)
                        dialog.dismiss()
                    }

                )

            cpRecyclerViewHelper.attachRecyclerView()
            cpRecyclerViewHelper.attachFilterQueryEditText(etQuery)
        }



        return dialog
    }
}
