package pl.dybuk.currencytest.core.common

import pl.dybuk.currencytest.core.currency.value.Rate
import java.math.BigDecimal
import java.math.RoundingMode

data class Money (
    val currencyType: CurrencyType,
    val value : BigDecimal) {

    fun exchange(rate : Rate) : Money =
            Money(rate.currencyType, value.multiply(rate.value).setScale(2, RoundingMode.HALF_UP))

}