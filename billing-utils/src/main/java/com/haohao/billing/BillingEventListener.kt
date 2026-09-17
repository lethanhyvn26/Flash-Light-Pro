package com.haohao.billing

import com.android.billingclient.api.Purchase
import com.haohao.billing.model.ErrorType


interface BillingEventListener {
    fun onProductsPurchased(purchases: List<Purchase?>)
    fun onPurchaseAcknowledged(purchase: Purchase)
    fun onPurchaseConsumed(purchase: Purchase)
    fun onBillingError(error: ErrorType)
}