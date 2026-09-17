package flashlight.phichung.com.torch.ads

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import flashlight.phichung.com.torch.R
import flashlight.phichung.com.torch.data.CachePreferencesHelper

class AdmobHelp private constructor() {
    private var mInterstitialAd: InterstitialAd? = null
    private var adCloseListener: AdCloseListener? = null
    private var isReload = false
    private var countShowAdsInterstitial = 0
    val NUMBER_COUNT_SHOW_ADS=1

    var isAdsInterstitialShowing = false

    fun init(context: Context) {
        MobileAds.initialize(context) {}
        loadInterstitialAd(context)
        countShowAdsInterstitial = 0
    }

    private fun loadInterstitialAd(context: Context) {
        if (mInterstitialAd != null) return
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            context.getString(R.string.str_ads_interstitial),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                    if (!isReload) {
                        isReload = true
                        loadInterstitialAd(context)
                    } else {
                        adCloseListener!!.onAdClosed()

                    }
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    isReload = false
                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                //     Log.d(TAG, 'Ad was dismissed.')
                                isAdsInterstitialShowing = false
                                adCloseListener!!.onAdClosed()

                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                //       Log.d(TAG, 'Ad failed to show.')
                                isAdsInterstitialShowing = false

                            }

                            override fun onAdShowedFullScreenContent() {
                                //  Log.d(TAG, 'Ad showed fullscreen content.')
                                mInterstitialAd = null
                                loadInterstitialAd(context)
                                isAdsInterstitialShowing = true

                            }

                        }

                }
            })


    }

    fun showInterstitialAd(activity: AppCompatActivity, adCloseListener: AdCloseListener) {

        val cachePreferencesHelper = CachePreferencesHelper(activity)

        if (cachePreferencesHelper.stateRemoveAds) {
            adCloseListener.onAdClosed()
            return  // not load show ads
        }

        if (countShowAdsInterstitial >= NUMBER_COUNT_SHOW_ADS) {

            if (canShowInterstitialAd(activity)) {
                countShowAdsInterstitial = 0   //reset count
                mInterstitialAd!!.show(activity)
                this.adCloseListener = adCloseListener

            } else {
                countShowAdsInterstitial++
                adCloseListener.onAdClosed()

            }
        } else {
            countShowAdsInterstitial++
            adCloseListener.onAdClosed()

        }


    }

    private fun canShowInterstitialAd(context: Context): Boolean {
        return mInterstitialAd != null && context is Activity
    }

    interface AdCloseListener {
        fun onAdClosed()
    }


    companion object {
        var instance: AdmobHelp? = null
            get() {
                if (field == null) {
                    field = AdmobHelp()
                }
                return field
            }
            private set
    }
}
