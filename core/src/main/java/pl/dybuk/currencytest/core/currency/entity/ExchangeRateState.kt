package pl.dybuk.currencytest.core.currency.entity

import pl.dybuk.currencytest.core.common.CurrencyType
import pl.dybuk.currencytest.core.currency.value.Rate

sealed class ExchangeRateState {
    data class LoadingState(val currencyType: CurrencyType) : ExchangeRateState()

    data class DataState(val currencyType: CurrencyType, val rates: List<Rate>) : ExchangeRateState()

    data class ErrorState(val error: Throwable) : ExchangeRateState()
}