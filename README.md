# kotlin_currency_stream_application

This application fetch currency online and present it as list.
Currency are update automatically by timer.
User can select one currency as input, this will move currency to the top and allow user to input currency amount. Other currency will show this value after exchange. 

Application have multiple flavours :
- Mock - this flavour will generate data/error responses from service. We can define delay, type of event and quantity. 
- Dev - flavour fetch currency from online service 
- Rec - This flavour works like dev but with recording service responses to a file.

# Project structure 

Project is divided into two parts

core - This is android module that wrap all classes that are application logic (exclude activities/views etc.) including business logic, data access layer, anti corruption layer
app - this is actual android part of application view only part.

# Core Module

## Service

We have only one service method, this method will fetch selected currency exchange rates

```
interface CurrencyService {
    fun getExchangeRate(sourceCurrency : String) : Observable<ExchangeRateDto>
}
```

This interface have multiple definition, depend on product flavour

### Mock service

This service allow as to define input as event list, this is example how we can define response from service:

```
CurrencyServiceMock(
                listOf(
                    MockEvents.Error(RuntimeException("SIMULATE RUNTIME ERROR")),
                    MockEvents.Error(NullPointerException("SIMULATE NULL POINTER ERROR")),
                    MockEvents.Data(mockCurrencies, 5),
                    MockEvents.Error(TimeoutException("SIMULATE TIMEOUT ERROR 1")),
                    MockEvents.Error(TimeoutException("SIMULATE TIMEOUT ERROR 2")),
                    MockEvents.Error(TimeoutException("SIMULATE TIMEOUT ERROR 3")),
                    MockEvents.Data(mockCurrencies.filter { it !=(CurrencyType.USD) }, -1)
                )
            )
```

We are creating service and provide event in desired order. 

MockEvent is seals class, that can be defined as Error or Data.

```
sealed class MockEvents(val count: Int, val delay: Long) {
    companion object {
        const val INFINITY = -1;
    }
    data class Error(val throwable: Throwable, var delayMs: Long = 1500L) : MockEvents(1, delayMs)
    data class Data(val currencies: List<CurrencyType>, var repetitionCount: Int = 1, var delayMs: Long = 1500L) :
        MockEvents(repetitionCount, delayMs)
}
```
- Error require exception and response delay, 
- Data require list of currencies to simulate, event repetition and response delay

Our example will send:
- first two request will be error responses, one runtimeexception and one nullpointer
- next service will generate valid 5 responses 
- after that we will have 3 timeouts
- finally we will have infinity valid answers (excluded USD)

### Dev Service 

This service use retrofit and GSON to fetch data, it very simple :

```
class CurrencyServiceImpl(private val currencyServiceRetrofit: CurrencyServiceRetrofit) : CurrencyService {

    override fun getExchangeRate(sourceCurrency: String): Observable<ExchangeRateDto> =
        currencyServiceRetrofit.getExchangeRate(sourceCurrency)

}
```

### Service with recording  

Service work just like Dev Service with additional forwarding to Record class.

Service does not return answer from retrofit, instead it create PublishSubject that act as proxy.
Service subscribes to retrofit Observable, and push result to PublishSubject, and to recorder.data

```
class CurrencyServiceWithRecorder( private val currencyServiceRetrofit: CurrencyServiceRetrofit) : CurrencyService {

    private val recorder = Recorder()

    private val gson = GsonBuilder().setLenient().create()

    override fun getExchangeRate(sourceCurrency: String): Observable<ExchangeRateDto> {
        var out: PublishSubject<ExchangeRateDto> = PublishSubject.create()

        val subscribe = this.currencyServiceRetrofit
            .getExchangeRate(sourceCurrency)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe({
                recorder.data.onNext(Record.Data(gson.toJson(it)))
                out.onNext(it)
            }, {
                recorder.data.onNext(Record.Error(it.localizedMessage))
                out.onError(it)
            }, {
                out.onComplete()
            })

        return out
    }
}
```

Recorder save data to external storage so permission have to be enabled from application manager (application does not ask for this permission because it is additional debug feature!)
