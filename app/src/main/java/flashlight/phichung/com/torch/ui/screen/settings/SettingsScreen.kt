package flashlight.phichung.com.torch.ui.screen.settings

import android.app.Activity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.haohao.languagepicker.allLanguage
import com.haohao.languagepicker.dataGenerator.LangFileReaderHard
import com.haohao.languagepicker.dialog.launchCountryPickerDialog
import com.haohao.languagepicker.localesLanguageApp
import com.haohao.languagepicker.setApplicationLocales
import flashlight.phichung.com.torch.MyApplication
import flashlight.phichung.com.torch.R
import flashlight.phichung.com.torch.ads.AdmobHelp
import flashlight.phichung.com.torch.ads.GoogleMobileAdsConsentManager
import flashlight.phichung.com.torch.ui.components.AdmobBanner
import flashlight.phichung.com.torch.ui.components.TopAppBarApp
import flashlight.phichung.com.torch.ui.theme.GrayWhiteColor
import flashlight.phichung.com.torch.ui.theme.IconWhiteColor
import flashlight.phichung.com.torch.ui.theme.TextGrayWhiteColor
import flashlight.phichung.com.torch.ui.theme.TextWhiteColor
import flashlight.phichung.com.torch.ui.theme.checkedThumbColor
import flashlight.phichung.com.torch.ui.theme.checkedTrackColor
import flashlight.phichung.com.torch.ui.theme.uncheckedThumbColor
import flashlight.phichung.com.torch.ui.theme.uncheckedTrackColor
import flashlight.phichung.com.torch.utils.feedback
import flashlight.phichung.com.torch.utils.getVersionApp
import flashlight.phichung.com.torch.utils.shareApp
import timber.log.Timber


object SettingsNavigation {

    const val route = "settings"

}

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel<SettingsViewModel>(),
    navController: NavHostController = rememberNavController()
) {

    TopAppBarApp(
        navController = navController,
        title = stringResource(id = R.string.str_settings_title),
        content = {

            CustomOptionUI(it, viewModel)

        }
    )

}

@Composable
fun CustomOptionUI(padding: PaddingValues, viewModel: SettingsViewModel) {
    Column(
        modifier = Modifier
            .padding(padding)
    ) {
        var stateSound by remember { mutableStateOf(viewModel.getSoundState()) }
        var stateAutomaticFlashMode by remember { mutableStateOf(viewModel.getAutomaticOnState()) }
        val activity = LocalContext.current as AppCompatActivity
       val googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(MyApplication.instance)
        LazyColumn(modifier = Modifier
            .padding(horizontal = 15.dp)
            .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 15.dp),
            content = {
                item {
                    Text(
                        text = stringResource(id = R.string.str_settings_customize),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrayWhiteColor,
                    )
                }
                item {
                    CustomOptionsItem(
                        icon = R.drawable.ic_language,
                        mainText = stringResource(id = R.string.str_settings_language),
                        onClick = {

                            AdmobHelp.instance?.showInterstitialAd(activity,
                                object : AdmobHelp.AdCloseListener {
                                    override fun onAdClosed() {
                                        activity.launchCountryPickerDialog(
                                            countryFileReader = LangFileReaderHard,
                                            allowSearch = false,
                                            onCountryClickListener = { languageModel ->
                                                val langChange = languageModel?.code ?: ""
                                                Timber.tag("HAOHAO").i("langChange :  " + langChange)
                                                viewModel.setLanguageApp(langChange)
                                                val index = localesLanguageApp.indexOfFirst {
                                                    it.toLanguageTag().lowercase() == langChange.lowercase()
                                                }
                                                if (index != -1) {
                                                    Timber.tag("HAOHAO").i("index :  " + index)
                                                    setApplicationLocales(
                                                        localesLanguageApp[index].language.toString()
                                                            .lowercase()
                                                    )


                                                }

                                            },
                                            langCodeSelect = viewModel.getLanguageApp()

                                        )
                                    }

                                })


                        },
                        switchVisible = false,
                        textVisible = true,
                        subText = allLanguage!!.filter { it.code == viewModel.getLanguageApp() }[0].emoji
                    )
                }

                item {
                    CustomOptionsItem(
                        icon = R.drawable.ic_automatic_on,
                        mainText = stringResource(id = R.string.str_settings_automatic_on),
                        onClick = {},
                        switchVisible = true,
                        textVisible = false,
                        statusSwitch = stateAutomaticFlashMode,
                        onCheckedChange = {
                            //  stateSound = it
                            AdmobHelp.instance?.showInterstitialAd(activity,
                                object : AdmobHelp.AdCloseListener {
                                    override fun onAdClosed() {
                                        viewModel.setAutomaticOnState(it)

                                    }

                                })
                        }
                    )
                }


                item {
                    CustomOptionsItem(
                        icon = R.drawable.ic_settings_sound,
                        mainText = stringResource(id = R.string.str_settings_sound),
                        onClick = {},
                        switchVisible = true,
                        textVisible = false,
                        statusSwitch = stateSound,
                        onCheckedChange = {
                            //  stateSound = it
                            AdmobHelp.instance?.showInterstitialAd(activity,
                                object : AdmobHelp.AdCloseListener {
                                    override fun onAdClosed() {
                                        viewModel.setSoundState(it)

                                    }

                                })
                        }

                    )
                }

                item {

                    Divider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = GrayWhiteColor, thickness = 1.dp
                    )
                }
                item {
                    Text(
                        text = stringResource(id = R.string.str_settings_support),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrayWhiteColor,
                    )
                }

                if(googleMobileAdsConsentManager.isPrivacyOptionsRequired){
                    item {
                        CustomOptionsItem(
                            icon = R.drawable.ic_privacy_admod,
                            mainText = stringResource(id = R.string.str_settings_privacy_admob),
                            onClick = {
                                googleMobileAdsConsentManager.showPrivacyOptionsForm(activity) { formError ->
                                    if (formError != null) {
                                        Toast.makeText(activity, formError.message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            switchVisible = false,
                            textVisible = false,
                        )
                    }
                }


                item {
                    CustomOptionsItem(
                        icon = R.drawable.ic_feedback,
                        mainText = stringResource(id = R.string.str_settings_feedback),
                        onClick = { activity.feedback() },
                        switchVisible = false,
                        textVisible = false,
                    )
                }

                item {
                    CustomOptionsItem(
                        icon = R.drawable.ic_share,
                        mainText = stringResource(id = R.string.str_settings_share),
                        onClick = { activity.shareApp() },
                        switchVisible = false,
                        textVisible = false,
                    )
                }

                item {
                    CustomOptionsItem(
                        icon = R.drawable.ic_version,
                        mainText = stringResource(id = R.string.str_settings_version),
                        onClick = {},
                        switchVisible = false,
                        textVisible = true,
                        subText = activity.getVersionApp(),
                    )

                }
            }


        )

        Spacer(modifier = Modifier.size(15.dp))

        AdmobBanner(cachePreferencesHelper = viewModel.getCachePreferencesHelper())


    }
}


@Composable
fun CustomOptionsItem(
    icon: Int,
    mainText: String,
    onClick: () -> Unit,
    switchVisible: Boolean,
    textVisible: Boolean,
    subText: String = "",
    statusSwitch: Boolean = false,
    onCheckedChange: (Boolean) -> Unit? = {},

    ) {
    val checked = remember { mutableStateOf(statusSwitch) }
    Row(
        modifier = if (!switchVisible) {
            Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clickable { onClick() }
        } else {
            Modifier
                .fillMaxWidth()
                .height(50.dp)
        },

        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,

        ) {
        Icon(
            modifier = Modifier
                .size(24.dp),
            painter = painterResource(id = icon),
            contentDescription = "",
            tint = IconWhiteColor,
        )
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = mainText,
            style = MaterialTheme.typography.bodyMedium,
            color = TextWhiteColor,
        )
        Spacer(modifier = Modifier.weight(1f))
        if (switchVisible) {
            Switch(
                modifier = Modifier.scale(0.75f),
                checked = checked.value,
                onCheckedChange = {
                    checked.value = it
                    onCheckedChange(it)
                },
                thumbContent = {
                    Icon(
                        imageVector = if (checked.value) Icons.Filled.Check else Icons.Filled.Close,
                        contentDescription = "",
                        tint = if (checked.value) Color.Green else Color.Gray
                    )
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = checkedThumbColor,
                    uncheckedThumbColor = uncheckedThumbColor,
                    checkedTrackColor = checkedTrackColor,
                    uncheckedTrackColor = uncheckedTrackColor,

                    )
            )
        }
        if (textVisible) {
            Text(
                text = subText,
                style = MaterialTheme.typography.bodyLarge,
                color = TextGrayWhiteColor,
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun SettingsScreenPreview() {
//    flashlight.phichung.com.torch.ui.screen.brightness.SettingsScreen()
//}