package flashlight.phichung.com.torch.ui.screen.morse

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import flashlight.phichung.com.torch.R
import flashlight.phichung.com.torch.ui.components.AdmobBanner
import flashlight.phichung.com.torch.ui.components.ButtonChild
import flashlight.phichung.com.torch.ui.components.TopAppBarApp
import flashlight.phichung.com.torch.ui.theme.GrayColor
import flashlight.phichung.com.torch.ui.theme.HintTextColor
import flashlight.phichung.com.torch.ui.theme.TextWhiteColor


object MorseNavigation {

    const val route = "morse"

}

@Composable
fun MorseScreen(
    viewModel: MorseViewModel = hiltViewModel<MorseViewModel>(),
    navController: NavHostController
) {
    val keyboardController = LocalSoftwareKeyboardController.current  // help hide soft keyboard

    TopAppBarApp(
        navController = navController,
        modifier = Modifier
            .clickable {
                keyboardController?.hide()
            },
        title = stringResource(id = R.string.str_morse_title),
        content = {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
            ) {


                var valueInputMorse by remember { mutableStateOf("") }
                var isHintDisplayed by remember { mutableStateOf(true) }
                var isLightOn by remember { mutableStateOf(true) }
                val uiMorseCodeState by viewModel.combinedFlow.collectAsState(AnnotatedString(""))
                val uiPlayState by viewModel.uiPlayState.collectAsState()
                var isSoundOn by remember { mutableStateOf(true) }
                val uiIndexCharacterInputState by viewModel.uiIndexCharacterInputState.collectAsState()




                Spacer(modifier = Modifier.size(15.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 15.dp),
                    text = stringResource(id = R.string.str_morse_your_text),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextWhiteColor
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                        .weight(1f)
                        .border(
                            width = 2.dp,
                            color = GrayColor,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .shadow(4.dp, RoundedCornerShape(8.dp))
                ) {
                    BasicTextField(
                        cursorBrush = SolidColor(TextWhiteColor),
                        enabled = !uiPlayState,
                        value = valueInputMorse,
                        onValueChange = {
                            valueInputMorse = it
                            isHintDisplayed = it.isEmpty()
                            viewModel.generateTextMorse(it)

                        },
                        visualTransformation = {

                            val annotatedString = buildAnnotatedString {
                                it.forEachIndexed { idx, char ->
                                    val color =
                                        if (idx <= uiIndexCharacterInputState) viewModel.getSkinCurrent().colorSkin else TextWhiteColor
                                    withStyle(style = SpanStyle(color = color)) {
                                        append(char.toString())
                                    }
                                }
                            }
                            TransformedText(annotatedString, OffsetMapping.Identity)

                        },

                        textStyle = TextStyle(color = TextWhiteColor),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(15.dp)
                            .background(Color.Transparent),
                        decorationBox = { innerTextField ->
                            if (isHintDisplayed) {
                                Text(
                                    text = stringResource(id = R.string.str_morse_hint_your_text),
                                    color = HintTextColor,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            innerTextField()
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Default
                        ),


                        //   keyboardActions = KeyboardActions(onDone = { /* Handle Done action */ }),


                    )

                }




                Text(
                    modifier = Modifier.padding(horizontal = 15.dp),
                    text = stringResource(id = R.string.str_morse_code_text),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextWhiteColor
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                        .weight(1f)
                        .border(
                            width = 2.dp,
                            color = GrayColor,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .shadow(4.dp, RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = uiMorseCodeState,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextWhiteColor,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(15.dp)
                            .verticalScroll(rememberScrollState())

                    )


                }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
                )
                {

                    ButtonChild(
                        icon = if (isSoundOn) R.drawable.ic_volume_off else R.drawable.ic_volume_on,
                        clickAction = {
                            isSoundOn = !isSoundOn
                            viewModel.setStateSound(isSoundOn)
                        }
                    )

                    ButtonChild(
                        icon = if (uiPlayState) R.drawable.ic_pause else R.drawable.ic_play,
                        clickAction = {
                            viewModel.setValuePlayState(!uiPlayState)

                        }
                    )

                    ButtonChild(
                        icon = if (isLightOn) R.drawable.ic_light_on else R.drawable.ic_light_off,
                        clickAction = {

                            isLightOn = !isLightOn
                            viewModel.setFlashlight(isLightOn)
                        }
                    )
                }
                Spacer(modifier = Modifier.size(15.dp))

                AdmobBanner(cachePreferencesHelper = viewModel.getCachePreferencesHelper())


            }

        }
    )



    DisposableEffect(true) {
        onDispose {
            viewModel.onDispose()
        }
    }


}

