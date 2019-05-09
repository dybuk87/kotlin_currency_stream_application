package pl.dybuk.currencytest.core.service.currency.impl

import io.reactivex.Observable
import pl.dybuk.currencytest.core.service.currency.CurrencyService
import pl.dybuk.currencytest.core.service.currency.dto.ExchangeRateDto

class CurrencyServiceImpl(private val currencyServiceRetrofit: CurrencyServiceRetrofit) : CurrencyService {

    override fun getExchangeRate(sourceCurrency: String): Observable<ExchangeRateDto> =
            currencyServiceRetrofit.getExchangeRate(sourceCurrency)
}