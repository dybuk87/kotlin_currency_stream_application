package pl.dybuk.currencytest.core.currency.entity

import android.util.Log
import com.annimon.stream.Collectors
import com.annimon.stream.Stream
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import pl.dybuk.currencytest.core.common.CurrencyType
import pl.dybuk.currencytest.core.currency.value.Rate
import pl.dybuk.currencytest.core.service.currency.CurrencyService
import java.util.concurrent.TimeUnit


class ExchangeRate(
    private val currencyService: CurrencyService,
    private val sourceCurrency: CurrencyType,
    private val refreshTime : Long = DEFAULT_REFRESH_TIMER,
    private val refreshErrorTime : Long = DEFAULT_ERROR_REFRESH_TIMER
) {

    companion object {
        const val DEFAULT_REFRESH_TIMER = 1000L
        const val DEFAULT_ERROR_REFRESH_TIMER = 5000L
    }

    val rates: BehaviorSubject<ExchangeRateState> = BehaviorSubject.create()

    private var interval: Disposable? = null

    private val intervalTime: BehaviorSubject<Long> = BehaviorSubject.createDefault(refreshTime)

    init {
        rates.onNext(ExchangeRateState.LoadingState(sourceCurrency))
    }

    fun start() {
        if (interval == null) {
            interval = intervalTime
                .distinctUntilChanged()     // new interval only when oldone changed
                .switchMap { currentPeriod ->
                    Observable.interval(0, currentPeriod, TimeUnit.MILLISECONDS)
                }
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext {
                    println("EXECUTE ${it}")
                    this.timerUpdate(it)
                }
                .subscribe({}, {
                    intervalTime.onNext(refreshErrorTime)
                    rates.onNext(ExchangeRateState.ErrorState(it))
                } )
        }

    }

    private fun timerUpdate(dt: Long) = currencyService
        .getExchangeRate(sourceCurrency.name)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .map {
            val newRates = Stream.of(it.rates).map { rateDto ->
                Rate(
                    CurrencyType(rateDto.key),
                    rateDto.value
                )
            }.sortBy { rate -> rate.currencyType.name }
                .collect(Collectors.toList())
            ExchangeRateState.DataState(sourceCurrency, newRates) as ExchangeRateState
        }
        .concatWith(Observable.never())
        .onErrorReturn { ExchangeRateState.ErrorState(it) }
        .startWith(ExchangeRateState.LoadingState(sourceCurrency))
        .subscribe({
            if (it !is ExchangeRateState.LoadingState) {
                intervalTime.onNext(if (it is ExchangeRateState.ErrorState) refreshErrorTime else refreshTime)
            }
            rates.onNext(it)
        }, {
            intervalTime.onNext(refreshErrorTime)
            rates.onNext(ExchangeRateState.ErrorState(it))
        })

    fun stop() {
        interval?.dispose()
        interval = null
    }

}