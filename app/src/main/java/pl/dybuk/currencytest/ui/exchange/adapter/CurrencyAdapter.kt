package pl.dybuk.currencytest.ui.exchange.adapter

import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.annimon.stream.Collectors
import com.annimon.stream.Stream
import org.greenrobot.eventbus.EventBus
import pl.dybuk.currencytest.R
import pl.dybuk.currencytest.core.common.CurrencyType
import pl.dybuk.currencytest.core.currency.value.Rate
import pl.dybuk.currencytest.ui.exchange.CurrencyActivityViewState


class CurrencyAdapter(val eventBus: EventBus) : RecyclerView.Adapter<CurrencyAdapterViewHolder>() {

    companion object {
        const val LOADING_ITEM_COUNT = 20

        const val ITEM = 1
        const val EMPTY = 2
        const val ERROR = 3
        const val LOADER = 4
    }

    val list: MutableList<CurrencyAdapterItem> = mutableListOf()

    fun update(viewState: CurrencyActivityViewState) = when (viewState) {
        is CurrencyActivityViewState.Loading -> handleLoadingState(viewState)
        is CurrencyActivityViewState.DataState -> handleDataState(viewState)
        is CurrencyActivityViewState.ErrorState -> handleErrorState(viewState)
    }


    private fun handleLoadingState(viewState: CurrencyActivityViewState.Loading) {
        // show loaders only when list is empty or showing error / empty state
        if (list.isEmpty() || list[0] is CurrencyAdapterItem.Error || list[0] is CurrencyAdapterItem.Empty) {
            list.clear()

            for (i in 0 until LOADING_ITEM_COUNT) {
                list.add(CurrencyAdapterItem.Loading("LOADING_{$i}"))
            }

            notifyDataSetChanged()
        } else {
            // user input out of sync with user data, only move element to top
            val selected =
                list.indexOfFirst { it is CurrencyAdapterItem.Item && it.money.currencyType == viewState.currencyType }

            if (selected != -1 && selected == 0) {
                // move
                list.add(0, list.removeAt(selected))
                notifyItemMoved(selected, 0)
            }
        }
    }


    private fun recalculateCurrencies(viewState: CurrencyActivityViewState.DataState): List<CurrencyAdapterItem> {
        val out: MutableList<CurrencyAdapterItem> = mutableListOf()
        // add selected currency to top
        list.add(CurrencyAdapterItem.Item(viewState.money))

        // add other currencies with conversion
        list.addAll(
            Stream.of(viewState.rates)
                .filter { viewState.money.currencyType != it.currencyType }   // exclude selected currency
                .map { CurrencyAdapterItem.Item(viewState.money.exchange(it)) }
                .collect(Collectors.toList())
        )

        return out
    }


    private fun handleDataState(viewState: CurrencyActivityViewState.DataState) {
        val start = System.currentTimeMillis()

        // user selection synced with data
        if (viewState.ratesCurrency == viewState.money.currencyType) {
            val oldList: MutableList<CurrencyAdapterItem> = mutableListOf()
            oldList.addAll(list)

            list.clear()
            // data state has two states - empty and with data
            if (viewState.rates.isEmpty()) {
                list.add(CurrencyAdapterItem.Empty("EMPTY"))
            } else {
                list.addAll(recalculateCurrencies(viewState))
            }

            val calculateDiff = DiffUtil.calculateDiff(CurrencyAdapterDiffCallBack(oldList, list), true)
            calculateDiff.dispatchUpdatesTo(this)
        }

        val stop = System.currentTimeMillis()
        Log.i("TIME", (stop - start).toString() + "ms")
    }


    private fun handleErrorState(viewState: CurrencyActivityViewState.ErrorState) {
        // when list is empty or not showing items, display error state
        // when list have at least one item, error state will show as notification
        if (list.isEmpty() || list.get(0) !is CurrencyAdapterItem.Item) {
            list.clear()
            list.add(CurrencyAdapterItem.Error("ERROR", viewState.th.localizedMessage))
            notifyDataSetChanged()
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (list[position]) {
            is CurrencyAdapterItem.Item -> ITEM
            is CurrencyAdapterItem.Error -> ERROR
            is CurrencyAdapterItem.Loading -> LOADER
            is CurrencyAdapterItem.Empty -> EMPTY
        }

    override fun onCreateViewHolder(root: ViewGroup, type: Int): CurrencyAdapterViewHolder =
        when (type) {
            EMPTY -> CurrencyAdapterViewHolder.CurrencyAdapterEmptyViewHolder(
                LayoutInflater.from(root.context).inflate(R.layout.view_holder_currency_empty, root, false)
            )
            ERROR -> CurrencyAdapterViewHolder.CurrencyAdapterEmptyViewHolder(
                LayoutInflater.from(root.context).inflate(R.layout.view_holder_currency_error, root, false)
            )
            LOADER -> CurrencyAdapterViewHolder.CurrencyAdapterEmptyViewHolder(
                LayoutInflater.from(root.context).inflate(R.layout.view_holder_currency_loader, root, false)
            )
            else -> CurrencyAdapterViewHolder.CurrencyAdapterItemViewHolder(
                eventBus,
                LayoutInflater.from(root.context).inflate(R.layout.view_holder_currency_item, root, false)
            )
        }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(viewHolder: CurrencyAdapterViewHolder, position: Int) {
        viewHolder.bind(list[position])
    }

    override fun onBindViewHolder(holder: CurrencyAdapterViewHolder, position: Int, payloads: MutableList<Any>) {
        //  super.onBindViewHolder(holder, position, payloads)
        holder.bind(
            list[position],
            if (!payloads.isEmpty() && payloads[0] is Bundle) payloads[0] as Bundle else null
        )
    }
}