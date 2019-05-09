package pl.dybuk.currencytest.core

import pl.dybuk.currencytest.core.service.currency.CurrencyService
import pl.dybuk.currencytest.core.service.currency.impl.CurrencyServiceImpl
import pl.dybuk.currencytest.core.service.currency.impl.CurrencyServiceRetrofit
import retrofit2.Retrofit

class FlavorFactoryImpl : FlavorFactory {

    override fun createCurrencyService(retrofit: Retrofit): CurrencyService
        = CurrencyServiceImpl(CurrencyServiceRetrofit.create(retrofit))

}