package pl.dybuk.currencytest.core.service.currency.impl

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.subjects.PublishSubject
import pl.dybuk.currencytest.core.service.currency.CurrencyService
import pl.dybuk.currencytest.core.service.currency.dto.ExchangeRateDto

class CurrencyServiceImpl(private val currencyServiceRetrofit: CurrencyServiceRetrofit) : CurrencyService {

    override fun getExchangeRate(sourceCurrency: String): Observable<ExchangeRateDto> =
        currencyServiceRetrofit.getExchangeRate(sourceCurrency)

}