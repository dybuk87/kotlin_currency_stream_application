package pl.dybuk.currencytest.core

import android.content.Context
import pl.dybuk.currencytest.core.service.currency.CurrencyService
import pl.dybuk.currencytest.core.service.currency.impl.CurrencyServiceImpl
import pl.dybuk.currencytest.core.service.currency.impl.CurrencyServiceRetrofit
import pl.dybuk.currencytest.core.service.currency.impl.CurrencyServiceWithRecorder
import retrofit2.Retrofit

class FlavorFactoryImpl : FlavorFactory {

    override fun createCurrencyService(retrofit: Retrofit): CurrencyService
        = CurrencyServiceWithRecorder(CurrencyServiceRetrofit.create(retrofit))

}