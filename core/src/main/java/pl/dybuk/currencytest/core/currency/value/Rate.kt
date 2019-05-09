package pl.dybuk.currencytest.core.currency.value

import pl.dybuk.currencytest.core.common.CurrencyType
import java.math.BigDecimal

data class Rate (
    val currencyType: CurrencyType,
    val value : BigDecimal
)
