package flashlight.phichung.com.torch.ui.screen.pro

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import flashlight.phichung.com.torch.R
import flashlight.phichung.com.torch.ui.theme.BottomCardShape
import flashlight.phichung.com.torch.ui.theme.GrayWhiteColor
import flashlight.phichung.com.torch.ui.theme.IconWhiteColor
import flashlight.phichung.com.torch.ui.theme.PrimaryColor
import flashlight.phichung.com.torch.ui.theme.ProColor
import flashlight.phichung.com.torch.ui.theme.TextWhiteColor


object ProNavigation {

    const val route = "pro"

}

@Composable
fun ProScreen(
    viewModel: ProViewModel = hiltViewModel<ProViewModel>(),
    navController: NavHostController = rememberNavController()
) {

    val statePayment by viewModel.statePayment.collectAsState()
    val activity = (LocalContext.current as Activity)

    viewModel.initListenerBilling(activity)

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {

                Image(
                    painter = painterResource(id = R.drawable.bg_pro),
                    contentDescription = "ProScreen",
                    contentScale = ContentScale.FillBounds,

                    )

                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "",
                    tint = IconWhiteColor,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(30.dp)
                        .size(30.dp)
                        .clickable {
                            navController.navigateUp()
                        })
            }
            Spacer(modifier = Modifier.weight(2f))
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter),
        ) {

            Spacer(modifier = Modifier.weight(3f))
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(5f),
                colors = CardDefaults.cardColors(PrimaryColor),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 7.dp
                ),
                border = BorderStroke(1.dp, GrayWhiteColor),
                shape = BottomCardShape.large
            ) {

                when (statePayment) {
                    is StatePayment.Initial -> {}
                    is StatePayment.Paid -> {

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(15.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                painter = painterResource(id = R.drawable.ic_paid),
                                contentDescription = "background paid",
                                contentScale = ContentScale.Crop
                            )
                        }

                    }

                    is StatePayment.Unpaid -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "PRO",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.headlineLarge,
                                color = ProColor,
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            CustomProOptionUI(
                                icon = R.drawable.ic_pro,
                                mainText = stringResource(id = R.string.str_pro_remove_ads)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            RoundedButton(
                                statePayment,
                            ) {  //click

                                viewModel.callPayment(activity)
                            }

                        }
                    }
                }


            }
        }
    }

}


@Composable
fun RoundedButton(statePayment: StatePayment, onClick: () -> Unit) {
    val description = AnnotatedString.Builder().apply {
        pushStyle(SpanStyle(color = TextWhiteColor))
        append(stringResource(id = R.string.str_pro_limited_offer))
        append("\n")
        pushStyle(SpanStyle(color = ProColor))
        append(stringResource(id = R.string.str_pro_sale_off))
        pop()
    }.toAnnotatedString()
    Text(
        text = description,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Start
    )
    Spacer(modifier = Modifier.size(10.dp))
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(ProColor),
    )
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (statePayment is StatePayment.Unpaid) statePayment.price else "",
                color = TextWhiteColor,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,

                )
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = IconWhiteColor,
                modifier = Modifier
                    .size(24.dp)
            )
        }
    }
}

@Composable
fun CustomProOptionUI(icon: Int, mainText: String) {
    Row(
        modifier = Modifier
            .wrapContentSize(),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "",
            tint = IconWhiteColor,
            modifier = Modifier
                .size(28.dp)
        )
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = mainText,
            style = MaterialTheme.typography.bodyLarge,
            color = TextWhiteColor,

            )
    }
}

