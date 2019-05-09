package pl.dybuk.currencytest.core.common

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import pl.dybuk.currencytest.core.currency.value.Rate
import java.math.BigDecimal
import org.fest.assertions.api.Assertions.assertThat

@RunWith(MockitoJUnitRunner::class)
class MoneyTest {

    @Test
    fun exchangeHalfUpTest() {
        val money = Money(CurrencyType.EUR, BigDecimal("2.94234"))
        val exchanged = money.exchange(Rate(CurrencyType.USD, BigDecimal("43.13413")))

        assertThat(exchanged.currencyType).isEqualTo(CurrencyType.USD)
        assertThat(exchanged.value).isEqualTo(BigDecimal("126.92"))

        assertThat(exchanged).isEqualTo(
            Money(CurrencyType.USD, BigDecimal("126.92"))
        )
    }

}