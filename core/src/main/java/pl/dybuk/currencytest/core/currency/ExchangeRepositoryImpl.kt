package pl.dybuk.currencytest.core.currency

import pl.dybuk.currencytest.core.common.CurrencyType
import pl.dybuk.currencytest.core.currency.entity.ExchangeRate
import pl.dybuk.currencytest.core.service.currency.CurrencyService

class ExchangeRepositoryImpl(private val currencyService: CurrencyService) : ExchangeRepository {

    override fun getExchangeRate(currencyType: CurrencyType) : ExchangeRate
        = ExchangeRate(currencyService, currencyType)

}