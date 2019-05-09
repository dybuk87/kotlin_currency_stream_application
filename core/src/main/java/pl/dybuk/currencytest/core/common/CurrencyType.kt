package pl.dybuk.currencytest.core.common

class CurrencyType(value: String) {

    val name = value.toUpperCase()

    companion object {
        val EUR = CurrencyType("EUR")
        val USD = CurrencyType("USD")
        val PLN = CurrencyType("PLN")
    }

    fun flagUrl() =
        "https://raw.githubusercontent.com/transferwise/currency-flags/master/src/flags/${name.toLowerCase()}.png"


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CurrencyType

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }


}