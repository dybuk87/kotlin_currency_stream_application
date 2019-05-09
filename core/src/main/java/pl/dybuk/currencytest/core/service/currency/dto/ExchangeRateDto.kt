package pl.dybuk.currencytest.core.service.currency.dto

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ExchangeRateDto(
    @SerializedName("base") val sourceCurrency : String,
    val date : String,
    val rates : Map<String, BigDecimal>
)