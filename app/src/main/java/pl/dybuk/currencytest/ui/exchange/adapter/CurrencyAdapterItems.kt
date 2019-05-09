package pl.dybuk.currencytest.ui.exchange.adapter

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import org.greenrobot.eventbus.EventBus
import pl.dybuk.currencytest.R
import pl.dybuk.currencytest.core.common.Money
import java.math.BigDecimal
import android.os.Bundle
import android.widget.ImageView
import com.squareup.picasso.Picasso


data class SelectedCurrencyEvent(val money: Money)

sealed class CurrencyAdapterItem {

    abstract val id: String

    data class Loading(override val id: String) : CurrencyAdapterItem()

    data class Item(var money: Money) : CurrencyAdapterItem() {
        override val id: String = money.currencyType.name
    }

    data class Empty(override val id: String) : CurrencyAdapterItem()

    data class Error(override val id: String, val message: String) : CurrencyAdapterItem()

}


class CurrencyAdapterDiffCallBack(
    val oldList: List<CurrencyAdapterItem>,
    val newList: List<CurrencyAdapterItem>
) : DiffUtil.Callback() {

    companion object {
        const val AMOUNT = "amount"
    }

    override fun areItemsTheSame(p0: Int, p1: Int): Boolean = oldList[p0].id == newList[p1].id

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(p0: Int, p1: Int): Boolean = oldList[p0] == newList[p1]

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? =
        when (val item = newList[newItemPosition]) {
            is CurrencyAdapterItem.Item -> {
                val payload = Bundle()
                payload.putString(AMOUNT, String.format(item.money.value.toString()))
                payload
            }
            else -> null
        }
}

sealed class CurrencyAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(item: CurrencyAdapterItem, payload: Bundle? = null) {}

    class CurrencyAdapterItemViewHolder(val eventBus: EventBus, view: View) : CurrencyAdapterViewHolder(view) {

        private val currencyField: TextView by lazy { view.findViewById<TextView>(R.id.currency_type) }
        private val currencyValue: EditText by lazy { view.findViewById<EditText>(R.id.currency_value) }
        private val currencyIcon: ImageView by lazy { view.findViewById<ImageView>(R.id.currency_icon) }

        var item: CurrencyAdapterItem.Item? = null

        var onBindState = false

        init {
            itemView.setOnClickListener { currencyValue.requestFocus() }

            currencyValue.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus && !onBindState) {
                    item?.let { eventBus.post(SelectedCurrencyEvent(it.money)) }
                }
            }

            currencyValue.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    item?.let {
                        val oldMoney = it.money
                        it.money = Money(it.money.currencyType, safeBigDecimal(s.toString()))
                        if (it.money != oldMoney && !onBindState) {
                            eventBus.post(SelectedCurrencyEvent(it.money))
                        }
                    }
                }

                private fun safeBigDecimal(value: String): BigDecimal =
                    if (value.trim().matches(Regex("^[0-9]+\\.?[0-9]*$"))) {
                        BigDecimal(value.trim())
                    } else {
                        BigDecimal.ZERO
                    }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            })
        }

        override fun bind(item: CurrencyAdapterItem, payload: Bundle?) {
            onBindState = true
            if (item is CurrencyAdapterItem.Item) {
                val oldMoney = this.item?.money
                this.item = item

                if (oldMoney != this.item?.money) {
                    if (payload == null) {
                        this.currencyField.text = item.money.currencyType.name
                        this.currencyValue.setText(String.format(item.money.value.toString()))
                        Picasso
                            .with(itemView.context)
                            .load(item.money.currencyType.flagUrl())
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .fit()
                            .centerInside()
                            .into(currencyIcon)
                    } else  {
                        this.currencyValue.setText(payload.getString(CurrencyAdapterDiffCallBack.AMOUNT))
                    }
                }
            }
            onBindState = false
        }

    }

    class CurrencyAdapterEmptyViewHolder(view: View) : CurrencyAdapterViewHolder(view) {

        val errorMessage: TextView by lazy { view.findViewById<TextView>(R.id.error_message) }

        override fun bind(item: CurrencyAdapterItem, payload: Bundle?) {
            when (item) {
                is CurrencyAdapterItem.Error -> errorMessage.text = item.message
            }
        }

    }


    class CurrencyAdapterLoaderViewHolder(view: View) : CurrencyAdapterViewHolder(view)

    class CurrencyAdapterErrorViewHolder(view: View) : CurrencyAdapterViewHolder(view)
}