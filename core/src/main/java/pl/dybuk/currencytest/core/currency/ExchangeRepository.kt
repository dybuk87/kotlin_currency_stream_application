package pl.dybuk.currencytest.core.currency

import pl.dybuk.currencytest.core.common.CurrencyType
import pl.dybuk.currencytest.core.currency.entity.ExchangeRate

interface ExchangeRepository {

    fun getExchangeRate(currencyType : CurrencyType) : ExchangeRate

}