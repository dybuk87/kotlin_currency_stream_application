package pl.dybuk.currencytest.core

import pl.dybuk.currencytest.core.common.CurrencyType
import pl.dybuk.currencytest.core.service.currency.CurrencyService
import retrofit2.Retrofit
import java.lang.NullPointerException
import java.lang.RuntimeException
import java.util.concurrent.TimeoutException

class FlavorFactoryImpl : FlavorFactory {

    private val mockCurrencies : List<CurrencyType> = listOf(
        CurrencyType.EUR, CurrencyType.USD, CurrencyType.PLN, CurrencyType.GBP, CurrencyType.PHP
    )


    override fun createCurrencyService(retrofit: Retrofit): CurrencyService =
            CurrencyServiceMock(
                listOf(
                    MockEvents.Error(RuntimeException("SIMULATE RUNTIME ERROR")),
                    MockEvents.Error(NullPointerException("SIMULATE NULL POINTER ERROR")),
                    MockEvents.Data(mockCurrencies, 5),
                    MockEvents.Error(TimeoutException("SIMULATE TIMEOUT ERROR 1")),
                    MockEvents.Error(TimeoutException("SIMULATE TIMEOUT ERROR 2")),
                    MockEvents.Error(TimeoutException("SIMULATE TIMEOUT ERROR 3")),
                    MockEvents.Data(mockCurrencies.filter { it !=(CurrencyType.USD) }, -1)
                )
            )

}