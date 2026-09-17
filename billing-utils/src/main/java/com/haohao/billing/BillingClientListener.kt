package com.haohao.billing



interface BillingClientListener {
    fun onClientReady()
    fun onClientInitError()

    fun onFetchActiveInAppPurchasesHistoryDone()


}