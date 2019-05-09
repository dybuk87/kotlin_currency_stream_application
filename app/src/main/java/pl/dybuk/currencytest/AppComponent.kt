package pl.dybuk.currencytest

import dagger.Component
import pl.dybuk.currencytest.core.CoreModule
import pl.dybuk.currencytest.ui.exchange.CurrencyActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [CoreModule::class])
interface AppComponent {
    fun inject(exchangeApplication: ExchangeApplication)
    fun inject(exchangeApplication: CurrencyActivity)
}