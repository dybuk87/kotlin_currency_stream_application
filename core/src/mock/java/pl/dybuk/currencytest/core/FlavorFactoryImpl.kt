package pl.dybuk.currencytest.core

import pl.dybuk.currencytest.core.service.currency.CurrencyService
import retrofit2.Retrofit

class FlavorFactoryImpl : FlavorFactory {
    override fun createCurrencyService(retrofit: Retrofit): CurrencyService =
            CurrencyServiceMock()

}