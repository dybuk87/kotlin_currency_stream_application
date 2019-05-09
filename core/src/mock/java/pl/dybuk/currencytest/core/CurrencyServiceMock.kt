package pl.dybuk.currencytest.core

import io.reactivex.Observable
import pl.dybuk.currencytest.core.service.currency.CurrencyService
import pl.dybuk.currencytest.core.service.currency.dto.ExchangeRateDto
import java.math.BigDecimal

class CurrencyServiceMock : CurrencyService {

    override fun getExchangeRate(sourceCurrency: String): Observable<ExchangeRateDto> =
        Observable.create {
            Thread.sleep(1500)

            it.onNext(
                ExchangeRateDto(
                    sourceCurrency,
                    "2018-09-06",
                    mutableMapOf(
                        Pair("EUR", BigDecimal("1.333")),
                        Pair("USD", BigDecimal("1.712")),
                        Pair("GBP", BigDecimal("2.698"))
                    )
                )
            )

            it.onComplete()
        }


}