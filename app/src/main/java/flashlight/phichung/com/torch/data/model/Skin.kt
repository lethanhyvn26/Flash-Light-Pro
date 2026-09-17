package flashlight.phichung.com.torch.data.model

import androidx.compose.ui.graphics.Color

data class Skin(
    val idSkin:Int,
    val colorSkin: Color,
    val imageSkin: Int,
    var selected:Boolean= false
)
