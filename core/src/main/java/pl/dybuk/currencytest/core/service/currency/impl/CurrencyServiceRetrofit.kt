package pl.dybuk.currencytest.core.service.currency.impl

import io.reactivex.Observable
import pl.dybuk.currencytest.core.service.currency.dto.ExchangeRateDto
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query


interface CurrencyServiceRetrofit {

    @GET("latest")
    fun getExchangeRate(@Query("base") sourceCurrency : String) : Observable<ExchangeRateDto>

    companion object {
        fun create(retrofit: Retrofit) : CurrencyServiceRetrofit {
            return retrofit.create(CurrencyServiceRetrofit::class.java)
        }
    }
}