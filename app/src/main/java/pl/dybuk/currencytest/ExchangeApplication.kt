package pl.dybuk.currencytest

import android.app.Application
import pl.dybuk.currencytest.core.CoreModule
import pl.dybuk.currencytest.core.currency.ExchangeRepository
import javax.inject.Inject

class ExchangeApplication : Application() {

    open lateinit var appComponent: AppComponent

    @Inject
    lateinit var exchangeRepository: ExchangeRepository

    override fun onCreate() {
        super.onCreate()

        init()
    }


    open fun init() {
        appComponent = DaggerAppComponent
            .builder().coreModule(CoreModule(this))
            .build()

        appComponent.inject(this)
    }
}