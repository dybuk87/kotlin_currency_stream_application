package pl.dybuk.currencytest.core.service.currency

import io.reactivex.Observable
import pl.dybuk.currencytest.core.service.currency.dto.ExchangeRateDto

interface CurrencyService {
    fun getExchangeRate(sourceCurrency : String) : Observable<ExchangeRateDto>
}