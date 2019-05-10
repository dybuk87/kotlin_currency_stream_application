package pl.dybuk.currencytest.core

import android.content.Context
import pl.dybuk.currencytest.core.service.currency.CurrencyService
import retrofit2.Retrofit


interface FlavorFactory {

    fun createCurrencyService(retrofit: Retrofit) : CurrencyService

}