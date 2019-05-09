package pl.dybuk.currencytest.core.currency.entity

import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import org.fest.assertions.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import pl.dybuk.currencytest.core.common.CurrencyType
import pl.dybuk.currencytest.core.currency.value.Rate
import pl.dybuk.currencytest.core.service.currency.CurrencyService
import pl.dybuk.currencytest.core.service.currency.dto.ExchangeRateDto
import java.lang.NullPointerException
import java.lang.RuntimeException
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class ExchangeRateTest {


    @Mock
    lateinit var currencyService: CurrencyService

    @Test
    fun testInitState() {
        val exchangeRate = ExchangeRate(
            currencyService,
            CurrencyType.EUR
        )

        assertThat(exchangeRate.rates.value)
            .isEqualTo(ExchangeRateState.LoadingState(CurrencyType.EUR))
    }

    @Test
    fun testErrorNullFromService() {
        // Given
        val exchangeRate = ExchangeRate(
            currencyService,
            CurrencyType.EUR,
            1000,
            3000
        )
        val testObserver = TestObserver<ExchangeRateState>()
        val scheduler = TestScheduler()
        RxJavaPlugins.setIoSchedulerHandler { scheduler }
        RxJavaPlugins.setComputationSchedulerHandler { scheduler }

        exchangeRate
            .rates
            .subscribe(testObserver)

        // when
        exchangeRate
            .start()

        scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS)

        // then
        testObserver.assertNotComplete()
        testObserver.assertNoErrors()
        testObserver.assertValueCount(2)

        assertThat(testObserver.values()[0]).isEqualTo(ExchangeRateState.LoadingState(CurrencyType.EUR))
        assertThat(testObserver.values()[1]).isInstanceOf(ExchangeRateState.ErrorState::class.java)
        assertThat((testObserver.values()[1] as ExchangeRateState.ErrorState).error).isInstanceOf(NullPointerException::class.java)

        exchangeRate.stop()

    }


    @Test
    fun testErrorFromService() {
        // Given
        Mockito.`when`(currencyService.getExchangeRate(Mockito.anyString()))
            .then {
                Observable.create<ExchangeRateDto> { supplier -> supplier.onError(RuntimeException("TEST")) }
            }

        val exchangeRate = ExchangeRate(
            currencyService,
            CurrencyType.EUR,
            1000,
            3000
        )
        val testObserver = TestObserver<ExchangeRateState>()
        val scheduler = TestScheduler()
        RxJavaPlugins.setIoSchedulerHandler { scheduler }
        RxJavaPlugins.setComputationSchedulerHandler { scheduler }

        exchangeRate
            .rates
            .subscribe(testObserver)

        // when
        exchangeRate
            .start()

        scheduler.advanceTimeBy(2500, TimeUnit.MILLISECONDS)

        // then
        testObserver.assertNotComplete()
        testObserver.assertNoErrors()
        testObserver.assertValueCount(3)

        assertThat(testObserver.values()[0]).isEqualTo(ExchangeRateState.LoadingState(CurrencyType.EUR))
        assertThat(testObserver.values()[1]).isEqualTo(ExchangeRateState.LoadingState(CurrencyType.EUR))
        assertThat(testObserver.values()[2]).isInstanceOf(ExchangeRateState.ErrorState::class.java)

        assertThat((testObserver.values()[2] as ExchangeRateState.ErrorState).error).isInstanceOf(RuntimeException::class.java)

        exchangeRate.stop()
    }


    @Test
    fun testErrorRetryTimeService() {
        // Given
        Mockito.`when`(currencyService.getExchangeRate(Mockito.anyString()))
            .then {
                Observable.create<ExchangeRateDto> { supplier -> supplier.onError(RuntimeException("TEST")) }
            }

        val exchangeRate = ExchangeRate(
            currencyService,
            CurrencyType.EUR,
            1000,
            3000
        )
        val testObserver = TestObserver<ExchangeRateState>()
        val scheduler = TestScheduler()
        RxJavaPlugins.setIoSchedulerHandler { scheduler }
        RxJavaPlugins.setComputationSchedulerHandler { scheduler }

        exchangeRate
            .rates
            .subscribe(testObserver)

        // when
        exchangeRate
            .start()

        scheduler.advanceTimeBy(10000, TimeUnit.MILLISECONDS)

        // then
        testObserver.assertNotComplete()
        testObserver.assertNoErrors()
        testObserver.assertValueCount(9)

        assertThat(testObserver.values()[0]).isEqualTo(ExchangeRateState.LoadingState(CurrencyType.EUR))

        // 10 seconds leads to 3 retries
        for (i in 0 until 3) {
            assertThat(testObserver.values()[1 + i * 2]).isEqualTo(ExchangeRateState.LoadingState(CurrencyType.EUR))
            assertThat(testObserver.values()[2 + i * 2]).isInstanceOf(ExchangeRateState.ErrorState::class.java)
            assertThat((testObserver.values()[2 + i * 2] as ExchangeRateState.ErrorState).error).isInstanceOf(
                RuntimeException::class.java
            )
        }

        exchangeRate.stop()
    }

    @Test
    fun testSuccessfulFetch() {
        val usdRate = BigDecimal("1.1224")
        val plnRate = BigDecimal("4.2963")
        // Given
        Mockito.`when`(currencyService.getExchangeRate(Mockito.anyString()))
            .then {
                Observable.create<ExchangeRateDto> { supplier ->
                    supplier.onNext(
                        ExchangeRateDto(
                            "EUR", "2020-09-01",
                            mapOf(Pair("USD", usdRate), Pair("PLN", plnRate))
                        )
                    )
                }
            }

        val exchangeRate = ExchangeRate(
            currencyService,
            CurrencyType.EUR,
            1000,
            3000
        )
        val testObserver = TestObserver<ExchangeRateState>()
        val scheduler = TestScheduler()
        RxJavaPlugins.setIoSchedulerHandler { scheduler }
        RxJavaPlugins.setComputationSchedulerHandler { scheduler }

        exchangeRate
            .rates
            .subscribe(testObserver)

        // when
        exchangeRate
            .start()

        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS)

        // then
        testObserver.assertNotComplete()
        testObserver.assertNoErrors()
        testObserver.assertValueCount(3)

        assertThat(testObserver.values()[0]).isEqualTo(ExchangeRateState.LoadingState(CurrencyType.EUR))

        assertThat(testObserver.values()[1]).isEqualTo(ExchangeRateState.LoadingState(CurrencyType.EUR))

        assertThat(testObserver.values()[2]).isInstanceOf(ExchangeRateState.DataState::class.java)

        val dateState = testObserver.values()[2] as ExchangeRateState.DataState

        assertThat(dateState.currencyType).isEqualTo(CurrencyType.EUR)

        assertThat(dateState.rates).hasSize(2)

        assertThat(dateState.rates).containsAll(
            listOf(
                Rate(CurrencyType.USD, usdRate),
                Rate(CurrencyType.PLN, plnRate)
            )
        )

        exchangeRate.stop()

    }


}