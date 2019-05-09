package pl.dybuk.currencytest.ui.exchange


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_currency.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import pl.dybuk.currencytest.R
import pl.dybuk.currencytest.core.common.CurrencyType
import pl.dybuk.currencytest.core.common.Money
import pl.dybuk.currencytest.core.currency.ExchangeRepository
import pl.dybuk.currencytest.core.currency.entity.ExchangeRate
import pl.dybuk.currencytest.ui.exchange.adapter.CurrencyAdapter
import pl.dybuk.currencytest.ui.exchange.adapter.SelectedCurrencyEvent
import pl.dybuk.currencytest.utils.BaseViewModelFactory
import pl.dybuk.currencytest.utils.dagger
import pl.dybuk.currencytest.utils.getViewModel
import  android.support.design.widget.Snackbar;
import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class CurrencyActivity : FragmentActivity() {

    private val viewModel: CurrencyActivityViewModel by lazy {
        getViewModel { CurrencyActivityViewModel( Money(CurrencyType("EUR"), BigDecimal("10"))) }
    }

    private lateinit var adapter: CurrencyAdapter

    @Inject
    lateinit var exchangeRepository: ExchangeRepository

    var exchangeRate : ExchangeRate? = null

    @Inject
    lateinit var eventBus: EventBus

    val errorNotification : PublishSubject<Throwable> = PublishSubject.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency)

        dagger().inject(this)

        eventBus.register(this)


        adapter = CurrencyAdapter(eventBus)

        currency_list.recycledViewPool.setMaxRecycledViews(CurrencyAdapter.ITEM, 100)
        currency_list.adapter = adapter
        currency_list.layoutManager = LinearLayoutManager(this)

        viewModel.listState.observe(this, Observer {
            when(it) {
                is CurrencyActivityViewState.ErrorState -> {
                    errorNotification.onNext(it.th)
                }
            }
            adapter.update(it!!)
        })

        viewModel.selectedCurrency.observe(this, Observer {
            exchangeRate?.stop()
            exchangeRate = exchangeRepository.getExchangeRate(it!!.currencyType)
            viewModel.setDataSource(exchangeRate)
            exchangeRate?.start()
        })

        errorNotification.observeOn(AndroidSchedulers.mainThread())
            .window(10, TimeUnit.SECONDS)
            .switchMap { source -> source.distinctUntilChanged{ it -> it.localizedMessage } }
            .subscribe { Snackbar.make(currency_list, it.localizedMessage, Snackbar.LENGTH_SHORT).show() }


    }

    override fun onResume() {
        super.onResume()

        exchangeRate?.start()
    }

    override fun onPause() {
        super.onPause()

        exchangeRate?.stop()
    }

    @Subscribe
    fun onCurrencySelectEvent(event: SelectedCurrencyEvent) {
        viewModel.currencyChanged(event)
    }

    override fun onDestroy() {
        super.onDestroy()

        eventBus.unregister(this)

        errorNotification.onComplete()
    }
}