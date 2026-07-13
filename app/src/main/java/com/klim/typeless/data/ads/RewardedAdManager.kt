package com.klim.typeless.data.ads

import android.app.Activity
import android.content.Context
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.rewarded.Reward
import com.yandex.mobile.ads.rewarded.RewardedAd
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoadListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoader
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RewardedAdManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var rewardedAd: RewardedAd? = null
    private val rewardedAdLoader = RewardedAdLoader(context)

    fun loadAd() {
        val adRequestConfiguration =
            AdRequestConfiguration.Builder(AdUnitIds.REWARDED).build()

        rewardedAdLoader.setAdLoadListener(object : RewardedAdLoadListener {
            override fun onAdLoaded(rewardedAd: RewardedAd) {
                this@RewardedAdManager.rewardedAd = rewardedAd
            }

            override fun onAdFailedToLoad(error: AdRequestError) {
                rewardedAd = null
            }
        })

        rewardedAdLoader.loadAd(adRequestConfiguration)
    }

    fun isAdReady(): Boolean = rewardedAd != null

    fun showAd(
        activity: Activity,
        onRewarded: () -> Unit,
        onDismissed: () -> Unit = {},
        onFailedToShow: () -> Unit = {}
    ) {
        val ad = rewardedAd

        if (ad == null) {
            onFailedToShow()
            return
        }

        ad.setAdEventListener(object : RewardedAdEventListener {
            override fun onAdShown() {}

            override fun onAdFailedToShow(adError: com.yandex.mobile.ads.common.AdError) {
                rewardedAd = null
                onFailedToShow()
            }

            override fun onAdDismissed() {
                rewardedAd = null
                loadAd()
                onDismissed()
            }

            override fun onAdClicked() {}

            override fun onAdImpression(impressionData: ImpressionData?) {}

            override fun onRewarded(reward: Reward) {
                onRewarded()
            }
        })

        ad.show(activity)
    }
}