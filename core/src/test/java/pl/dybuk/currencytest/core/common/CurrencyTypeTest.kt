package pl.dybuk.currencytest.core.common

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.fest.assertions.api.Assertions.assertThat

@RunWith(MockitoJUnitRunner::class)
class CurrencyTypeTest {

    @Test
    fun testIgnoreCase() {
        assertThat(CurrencyType("USD")).isEqualTo(CurrencyType("usd"))
    }

}