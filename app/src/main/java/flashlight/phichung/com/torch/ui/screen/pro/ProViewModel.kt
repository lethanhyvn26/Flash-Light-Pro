package flashlight.phichung.com.torch.ui.screen.pro


import android.app.Activity
import com.android.billingclient.api.Purchase
import com.haohao.billing.BillingEventListener
import com.haohao.billing.BillingHelper
import com.haohao.billing.model.ErrorType
import dagger.hilt.android.lifecycle.HiltViewModel
import flashlight.phichung.com.torch.MyApplication
import flashlight.phichung.com.torch.base.BaseViewModel
import flashlight.phichung.com.torch.data.CachePreferencesHelper
import flashlight.phichung.com.torch.utils.Constant.KEY_PAYMENT_IN_APP_ADMOB
import flashlight.phichung.com.torch.utils.CoroutineContextProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject


sealed class StatePayment {
    data object Initial : StatePayment()

    data object Paid : StatePayment()

    class Unpaid(val price: String) : StatePayment()
}

@HiltViewModel
class ProViewModel @Inject constructor(
    contextProvider: CoroutineContextProvider,
    preferencesHelper: CachePreferencesHelper,

    ) : BaseViewModel(contextProvider, preferencesHelper) {


    private val _statePayment = MutableStateFlow<StatePayment>(StatePayment.Initial)
    val statePayment = _statePayment.asStateFlow()


    fun callPayment(context: Activity) {
        BillingHelper(context).buyInApp(
            context,
            KEY_PAYMENT_IN_APP_ADMOB,
            false
        )
    }

    fun initListenerBilling(context: Activity) {
        if (BillingHelper(context)
                .setInAppKeys(mutableListOf(KEY_PAYMENT_IN_APP_ADMOB))
                .enableLogging(isEnableLog = true).isClientReady()
        ) {
            Timber.d(" BillingHelper onClientReady: ")
            checkPaidRemoveAds()

            BillingHelper(context).setBillingEventListener(object :
                BillingEventListener {
                override fun onProductsPurchased(purchases: List<Purchase?>) {
                    //call back when purchase occurred
                    Timber.d(" BillingHelper onProductsPurchased: $purchases")

                }

                override fun onPurchaseAcknowledged(purchase: Purchase) {
                    //call back when purchase occurred and acknowledged

                    Timber.d(" BillingHelper onPurchaseAcknowledged: $purchase")

                    //user paid
                    checkPaidRemoveAds()

                }

                override fun onPurchaseConsumed(purchase: Purchase) {
                    //call back when purchase occurred and consumed
                    Timber.d(" BillingHelper onPurchaseConsumed: $purchase")

                }

                override fun onBillingError(error: ErrorType) {
                    Timber.d(" BillingHelper onBillingError: $error")


                }
            })
        }
    }

    private fun checkPaidRemoveAds() {
        if (BillingHelper(MyApplication.instance).isInAppPremiumUserByInAppKey(
                KEY_PAYMENT_IN_APP_ADMOB
            )
        ) {
            // paid
            _statePayment.value = StatePayment.Paid
            getCachePreferencesHelper().stateRemoveAds = true
        } else {

            val textPrice =
                BillingHelper(MyApplication.instance).getProductPriceByKey(KEY_PAYMENT_IN_APP_ADMOB)?.price
            _statePayment.value = StatePayment.Unpaid(textPrice.toString())
            getCachePreferencesHelper().stateRemoveAds = false


        }
    }

}