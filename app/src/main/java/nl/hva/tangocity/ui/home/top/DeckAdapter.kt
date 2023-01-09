package nl.hva.tangocity.ui.home.top

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Pie
import nl.hva.tangocity.R
import nl.hva.tangocity.databinding.ItemDeckBinding
import nl.hva.tangocity.getColorCode
import nl.hva.tangocity.model.Deck
import kotlin.collections.ArrayList


class DeckAdapter(private val decks: ArrayList<Deck>, private val context: Context, private val clickListener: (Int) -> Unit): RecyclerView.Adapter<DeckAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        private val binding = ItemDeckBinding.bind(itemView)
        private val deckListView = binding.root.findViewById(R.id.deck_list_chart) as AnyChartView

        fun databind(deck: Deck, position: Int, clickListener: (Int) -> Unit) {
            deckListView.setBackgroundColor(getColorCode(context, R.color.background))

            val chart : Pie = AnyChart.pie()
            chart.background().fill(getColorCode(context, R.color.background))
            chart.innerRadius("85%")
            chart.legend(false)
            chart.labels(false)

            val data: MutableList<DataEntry> = ArrayList()
            var newCount = 0
            var matureCount = 0
            var youngCount = 0
            deck.cards.forEach{ card ->
                if (card.interval < 31) {
                    newCount += 1
                } else if (card.interval in 32..61){
                    youngCount += 1
                } else {
                    matureCount += 1
                }
            }
            data.add( getDataEntry("New", newCount, getColorCode(context, R.color.new_color)))
            data.add( getDataEntry("Mature", matureCount, getColorCode(context, R.color.mature_color)))
            data.add( getDataEntry("Young", youngCount, getColorCode(context, R.color.young_color)))

            chart.data(data)
            deckListView.setChart(chart)

            binding.deckTitle.text = deck.name
            binding.deckSubTitle.text = String.format("New: %d\n Young: %d\nMature: %d", newCount, youngCount, matureCount)
            binding.deckScript.setOnClickListener{ clickListener(position) }
        }
    }

    /**
     * Creates and returns a ViewHolder object, inflating a standard layout called simple_list_item_1.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_deck, parent, false)
        )
    }

    /**
     * Returns the size of the list
     */
    override fun getItemCount(): Int {
        return decks.size
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.databind(decks[position], position, clickListener)
    }

    private fun getDataEntry( x: String, value: Int, color: String): ValueDataEntry{
        val dataEntry = DataEntry()
        dataEntry.setValue("fill", color)
        val chartDataEntry = ValueDataEntry(x,value)
        chartDataEntry.setValue("normal", dataEntry)

        return chartDataEntry
    }
}


