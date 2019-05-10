package pl.dybuk.currencytest.core.service.currency.impl

import android.content.Context
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import pl.dybuk.currencytest.core.service.currency.CurrencyService
import pl.dybuk.currencytest.core.service.currency.dto.ExchangeRateDto

class CurrencyServiceWithRecorder( private val currencyServiceRetrofit: CurrencyServiceRetrofit) : CurrencyService {

    private val recorder = Recorder()

    private val gson = GsonBuilder().setLenient().create()

    override fun getExchangeRate(sourceCurrency: String): Observable<ExchangeRateDto> {
        var out: PublishSubject<ExchangeRateDto> = PublishSubject.create()

        val subscribe = this.currencyServiceRetrofit
            .getExchangeRate(sourceCurrency)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe({
                recorder.data.onNext(Record.Data(gson.toJson(it)))
                out.onNext(it)
            }, {
                recorder.data.onNext(Record.Error(it.localizedMessage))
                out.onError(it)
            }, {
                out.onComplete()
            })

        return out
    }


    //
}