package pl.dybuk.currencytest.core

import io.reactivex.Observable
import pl.dybuk.currencytest.core.MockEvents.Companion.INFINITY
import pl.dybuk.currencytest.core.common.CurrencyType
import pl.dybuk.currencytest.core.service.currency.CurrencyService
import pl.dybuk.currencytest.core.service.currency.dto.ExchangeRateDto
import java.lang.Thread.sleep
import java.math.BigDecimal
import kotlin.random.Random

sealed class MockEvents(val count: Int, val delay: Long) {

    companion object {
        const val INFINITY = -1;
    }


    data class Error(val throwable: Throwable, var delayMs: Long = 1500L) : MockEvents(1, delayMs)

    data class Data(val currencies: List<CurrencyType>, var repetitionCount: Int = 1, var delayMs: Long = 1500L) :
        MockEvents(repetitionCount, delayMs)
}

class CurrencyServiceMock(val events: List<MockEvents>) : CurrencyService {

    private var index: Int = -1

    private var repetitionLeft = 0

    @Synchronized
    private fun getNextEvent(): MockEvents =
        when (repetitionLeft) {
            INFINITY -> events[index]
            0 -> {
                index++
                // check boundaries
                if (index >= events.size) {
                    index = 0
                }
                repetitionLeft = events[index].count
                events[index]
            }
            else -> {
                repetitionLeft--
                events[index]
            }
        }

    private fun generateData(sourceCurrency: String, event: MockEvents.Data): ExchangeRateDto =
        ExchangeRateDto(
            sourceCurrency,
            "2018-09-06",
            event.currencies
                .filter { !it.name.equals(sourceCurrency, true) }
                .map {
                    Pair<String, BigDecimal>(it.name, BigDecimal.valueOf(Random.nextDouble(0.5, 10.0)))
                }.toMap()
        )


    override fun getExchangeRate(sourceCurrency: String): Observable<ExchangeRateDto> =
        Observable.create {
            val event = getNextEvent()
            sleep(event.delay)
            when (event) {
                is MockEvents.Error -> it.onError(event.throwable)
                is MockEvents.Data -> it.onNext(generateData(sourceCurrency, event))
            }
        }


}