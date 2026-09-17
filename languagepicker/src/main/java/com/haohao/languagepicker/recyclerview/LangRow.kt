package com.haohao.languagepicker.recyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.airbnb.epoxy.AfterPropsSet
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.haohao.languagepicker.R
import com.haohao.languagepicker.config.LangRowConfig
import com.haohao.languagepicker.databinding.ItemLangRowBinding
import com.haohao.languagepicker.model.LanguageModel

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
internal class LangRow @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var binding: ItemLangRowBinding

    init {
        inflate(context, R.layout.item_lang_row, this)
        binding = ItemLangRowBinding.bind(this.findViewById(R.id.langRow))
    }

    lateinit var lang: LanguageModel
        @ModelProp set

    var rowConfig = LangRowConfig()
        @ModelProp set

    @CallbackProp
    fun clickListener(clickListener: ((LanguageModel) -> Unit)?) {
        setOnClickListener { clickListener?.invoke(lang) }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        binding.root.setOnClickListener(l)
    }

    @AfterPropsSet
    fun updateViews() {
        binding.tvNameLangRow.text = rowConfig.primaryTextGenerator.invoke(lang)
        applyFlag()
        setIsSelectedLanguage()


    }

    private fun setIsSelectedLanguage() {
        if (rowConfig.isLanguageSelected) {
            binding.ivDropdownLangRow.visibility = View.VISIBLE
        } else {
            binding.ivDropdownLangRow.visibility = View.GONE
        }
    }


    private fun applyFlag() {
        val flagProvider = rowConfig.cpFlagProvider
        if (flagProvider) {

            binding.tvEmojiFlagLangRow.visibility = View.VISIBLE
            binding.tvEmojiFlagLangRow.text = lang.emoji
        } else {
            binding.tvEmojiFlagLangRow.visibility = View.GONE

        }

    }

}
