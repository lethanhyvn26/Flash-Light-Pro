package flashlight.phichung.com.torch.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.ads.MobileAds
import com.haohao.billing.BillingClientListener
import com.haohao.billing.BillingHelper
import dagger.hilt.android.AndroidEntryPoint
import flashlight.phichung.com.torch.MyApplication
import flashlight.phichung.com.torch.R
import flashlight.phichung.com.torch.ads.AdmobHelp
import flashlight.phichung.com.torch.ads.GoogleMobileAdsConsentManager
import flashlight.phichung.com.torch.data.CachePreferencesHelper
import flashlight.phichung.com.torch.utils.Constant.KEY_PAYMENT_IN_APP_ADMOB
import timber.log.Timber
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private var secondsRemaining: Long = 0L

    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)
    @Inject
    lateinit var cachePreferencesHelper: CachePreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Handle the splash screen transition.
        installSplashScreen()
        setContentView(R.layout.activity_splash)

        initBilling()
        createTimer()

        googleMobileAdsConsentManager =
            GoogleMobileAdsConsentManager.getInstance(applicationContext)
        googleMobileAdsConsentManager.gatherConsent(this) { consentError ->
            if (consentError != null) {
                // Consent not obtained in current session.

                Timber.i(String.format("%s: %s", consentError.errorCode, consentError.message))
            }

            if (googleMobileAdsConsentManager.canRequestAds) {
                initializeMobileAdsSdk()
            }

            if (secondsRemaining <= 0) {
                startMainActivity()
            }
        }

        // This sample attempts to load ads using consent obtained in the previous session.
        if (googleMobileAdsConsentManager.canRequestAds) {
            initializeMobileAdsSdk()
        }
    }

    private fun createTimer() {
        val countDownTimer: CountDownTimer =
            object : CountDownTimer(3000L, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    secondsRemaining = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1
                }

                override fun onFinish() {
                    secondsRemaining = 0
                    if (googleMobileAdsConsentManager.canRequestAds) {
                        startMainActivity()
                    }
                }
            }
        countDownTimer.start()
    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }

        AdmobHelp.instance?.init(this) //init ads

    }

    private fun initBilling() {


        BillingHelper(this@SplashActivity)
            .setInAppKeys(mutableListOf(KEY_PAYMENT_IN_APP_ADMOB))
            .enableLogging(isEnableLog = true)
            .setBillingClientListener(object :
                BillingClientListener {
                override fun onClientReady() {
                }

                override fun onClientInitError() {
                }

                override fun onFetchActiveInAppPurchasesHistoryDone() {
                    checkPaidRemoveAds()
                }

            })
    }

    private fun checkPaidRemoveAds() {

        // paid
        cachePreferencesHelper.stateRemoveAds = BillingHelper(this).isInAppPremiumUserByInAppKey(
            KEY_PAYMENT_IN_APP_ADMOB
        )
    }

    private fun startMainActivity() {

        if (MyApplication.appOpenAdManager.isShowingAd) {
            return
        }


        MyApplication.appOpenAdManager.showAdIfAvailable(
            this@SplashActivity,
            object : MyApplication.OnShowAdCompleteListener {

                override fun onShowAdComplete() {

                    Handler(Looper.getMainLooper()).postDelayed({
                        val mainIntent = Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(mainIntent)
                        finish()
                    }, 50)
                }
            })




    }



    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {

    }
}

