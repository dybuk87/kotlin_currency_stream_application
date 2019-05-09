package pl.dybuk.currencytest.ui.exchange

import pl.dybuk.currencytest.core.common.CurrencyType
import pl.dybuk.currencytest.core.common.Money
import pl.dybuk.currencytest.core.currency.value.Rate

sealed class CurrencyActivityViewState {
    data class Loading(val currencyType: CurrencyType?) : CurrencyActivityViewState()

    data class DataState(val money : Money, val ratesCurrency : CurrencyType, val rates: List<Rate>) : CurrencyActivityViewState()

    data class ErrorState(val th : Throwable) : CurrencyActivityViewState()
}