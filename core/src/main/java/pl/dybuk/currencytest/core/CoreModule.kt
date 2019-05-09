package pl.dybuk.currencytest.core

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import org.greenrobot.eventbus.EventBus
import pl.dybuk.currencytest.core.currency.ExchangeRepository
import pl.dybuk.currencytest.core.currency.ExchangeRepositoryImpl
import pl.dybuk.currencytest.core.service.currency.CurrencyService
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
open class CoreModule(private val application : Application) {

    val flavorFactory = FlavorFactoryImpl()

    @Provides
    @Singleton
    open fun providesEventBus() : EventBus = EventBus()

    @Provides
    open fun providesGson() : Gson = GsonBuilder().setLenient().create()

    @Provides
    open fun providesRetrofit(gson: Gson) : Retrofit =
        Retrofit.Builder()
            .baseUrl("https://revolut.duckdns.org")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    @Provides
    open fun providesCurrencyService(retrofit: Retrofit) = flavorFactory.createCurrencyService(retrofit)


    @Provides
    @Singleton
    open fun providesExchangeRepository(currencyService: CurrencyService) : ExchangeRepository = ExchangeRepositoryImpl(currencyService)

}