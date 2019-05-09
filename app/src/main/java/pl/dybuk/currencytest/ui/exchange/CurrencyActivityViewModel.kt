package pl.dybuk.currencytest.ui.exchange

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import pl.dybuk.currencytest.core.common.CurrencyType
import pl.dybuk.currencytest.core.common.Money
import pl.dybuk.currencytest.core.currency.entity.ExchangeRate
import pl.dybuk.currencytest.core.currency.entity.ExchangeRateState
import pl.dybuk.currencytest.ui.exchange.adapter.SelectedCurrencyEvent
import java.math.BigDecimal
import java.util.Locale.filter


class CurrencyActivityViewModel(initMoney: Money) : ViewModel() {
    val selectedCurrency = MutableLiveData<Money>()

    val listState = MutableLiveData<CurrencyActivityViewState>()

    init {
        selectedCurrency.value = initMoney
    }

    fun currencyChanged(event: SelectedCurrencyEvent) {
        selectedCurrency.value = event.money
    }

    private var subscribe: Disposable? = null

    fun setDataSource(exchangeRateNullable: ExchangeRate?) {
        subscribe?.dispose()
        subscribe = null

        exchangeRateNullable?.let { exchangeRate ->
            subscribe = exchangeRate.rates
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it) {
                        is ExchangeRateState.DataState -> listState.value = CurrencyActivityViewState.DataState(
                            selectedCurrency.value ?: Money(CurrencyType("EUR"), BigDecimal("100")),  // user input Money (currency + amount)
                            it.currencyType,    // rates currency - this might be different then user input, user could changed currency during fetching time
                            it.rates            // rates
                        )

                        is ExchangeRateState.ErrorState -> listState.value =
                                CurrencyActivityViewState.ErrorState(it.error)

                        is ExchangeRateState.LoadingState -> listState.value =
                                CurrencyActivityViewState.Loading(it.currencyType)
                    }
                }, { it.printStackTrace() }, { Log.i("RXJAVA", "COMPLETE") })
        }
    }


}