package com.haohao.languagepicker.config

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.haohao.languagepicker.R

data class LangDialogConfig(
    var dialogViewIds: CPDialogViewIds = defaultCPDialogViewIds,
    var allowSearch: Boolean = defaultCPDialogAllowSearch,
    var allowClearSelection: Boolean = defaultCPDialogAllowClearSelection,
    var showTitle: Boolean = defaultCPDialogDefaultShowTitle,
    var showFullScreen: Boolean = defaultCPDialogShowFullScreen,
    var sizeMode: SizeModeLanguage = defaultCPDialogDefaultSizeMode,

    ) {

    companion object {
        const val defaultCPDialogAllowSearch = true
        const val defaultCPDialogShowFullScreen = true
        const val defaultCPDialogAllowClearSelection = false
        const val defaultCPDialogDefaultShowTitle = true

        val defaultCPDialogDefaultSizeMode: SizeModeLanguage = SizeModeLanguage.Auto

        val defaultCPDialogViewIds =
            CPDialogViewIds(
                R.layout.cp_dialog,
                R.id.clParent,
                R.id.rcvAll,
                R.id.tvTitle,
                R.id.edtSearch,
                R.id.imgClearQuery,
                R.id.ivBack,
                R.id.cardSearch
            )
    }

    private fun getAutoDetectedResizeMode(): SizeModeLanguage {
        return if (showFullScreen) {
            SizeModeLanguage.Unchanged
        } else {
            SizeModeLanguage.Wrap
        }
    }

    fun getApplicableResizeMode(): SizeModeLanguage {
        return if (sizeMode == SizeModeLanguage.Auto) {
            getAutoDetectedResizeMode()
        } else {
            sizeMode
        }
    }
}

data class CPDialogViewIds(
    @LayoutRes val layoutId: Int,
    @IdRes val containerId: Int,
    @IdRes val recyclerViewId: Int,
    @IdRes val titleTextViewId: Int?,
    @IdRes val queryEditTextId: Int?,
    @IdRes val clearQueryImageViewId: Int?,
    @IdRes val ivBack: Int?,
    @IdRes val cardSearch: Int?,


    )

sealed class SizeModeLanguage {
    data object Auto : SizeModeLanguage()
    data object Unchanged : SizeModeLanguage()
    data object Wrap : SizeModeLanguage()
}
