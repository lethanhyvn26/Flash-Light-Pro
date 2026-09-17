package flashlight.phichung.com.torch

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import flashlight.phichung.com.torch.ui.screen.brightness.BrightnessNavigation
import flashlight.phichung.com.torch.ui.screen.brightness.BrightnessScreen
import flashlight.phichung.com.torch.ui.screen.camera.CameraNavigation
import flashlight.phichung.com.torch.ui.screen.camera.CameraScreen
import flashlight.phichung.com.torch.ui.screen.compass.CompassNavigation
import flashlight.phichung.com.torch.ui.screen.compass.CompassScreen
import flashlight.phichung.com.torch.ui.screen.gallery.GalleryNavigation
import flashlight.phichung.com.torch.ui.screen.gallery.GalleryScreen
import flashlight.phichung.com.torch.ui.screen.home.HomeNavigation
import flashlight.phichung.com.torch.ui.screen.home.HomeScreen
import flashlight.phichung.com.torch.ui.screen.morse.MorseNavigation
import flashlight.phichung.com.torch.ui.screen.morse.MorseScreen
import flashlight.phichung.com.torch.ui.screen.preview.PreviewNavigation
import flashlight.phichung.com.torch.ui.screen.preview.PreviewScreen
import flashlight.phichung.com.torch.ui.screen.pro.ProNavigation
import flashlight.phichung.com.torch.ui.screen.pro.ProScreen
import flashlight.phichung.com.torch.ui.screen.settings.SettingsNavigation
import flashlight.phichung.com.torch.ui.screen.settings.SettingsScreen
import flashlight.phichung.com.torch.ui.screen.skin.SkinNavigation
import flashlight.phichung.com.torch.ui.screen.skin.SkinScreen


@Composable
fun FlashlightApp(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = HomeNavigation.route
    ) {
        composable(HomeNavigation.route) {
            HomeScreen(
                openSkinAction = {
                    navController.navigate(SkinNavigation.route)

                },
                openSettingsAction = {
                    navController.navigate(SettingsNavigation.route)
                },
                openColorAction = {
                    navController.navigate(BrightnessNavigation.route)

                },
                openCompassAction = {
                    navController.navigate(CompassNavigation.route)

                },
                openMorseAction = {
                    navController.navigate(MorseNavigation.route)
                },
                openRemoveAdsAction = {
                    navController.navigate(ProNavigation.route)

                },
                openCameraAction = {
                    navController.navigate(CameraNavigation.route)
                })
        }

        composable(SettingsNavigation.route) {
            SettingsScreen(navController = navController)
        }
        composable(BrightnessNavigation.route) {
            BrightnessScreen(navController = navController)
        }
        composable(CameraNavigation.route) {
            CameraScreen(navController = navController) {
                navController.navigate(GalleryNavigation.route)
            }
        }
        composable(CompassNavigation.route) {
            CompassScreen(navController = navController)
        }
        composable(MorseNavigation.route) {
            MorseScreen(navController = navController)
        }
        composable(ProNavigation.route) {
            ProScreen(navController = navController)
        }
        composable(SkinNavigation.route) {
            SkinScreen(navController = navController)
        }


        composable(GalleryNavigation.route) {
            GalleryScreen(navController = navController) {
                //preview
                navController.navigate(PreviewNavigation.createRoute(it))

            }
        }

        composable(
            PreviewNavigation.route, arguments = listOf(
                navArgument(PreviewNavigation.pathArg) { type = NavType.StringType },
            )
        ) {

            PreviewScreen(navController = navController)
        }
    }
}
