package nl.hva.tangocity.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Pie
import nl.hva.tangocity.R
import nl.hva.tangocity.databinding.ItemDeckBinding
import kotlin.collections.ArrayList


class DeckAdapter(private val context: Context, private val clickListener: () -> Unit): RecyclerView.Adapter<DeckAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val binding = ItemDeckBinding.bind(itemView)
        private val deckListView = binding.root.findViewById(R.id.deck_list_chart) as AnyChartView

        fun databind(clickListener: () -> Unit) {
            val data: MutableList<DataEntry> = ArrayList()
            data.add( getDataEntry("New", (0..100).random(), getColor(R.color.new_color)))
            data.add( getDataEntry("Mature", (0..100).random(), getColor(R.color.mature_color)))
            data.add( getDataEntry("Young", (0..100).random(), getColor(R.color.young_color)))

            val chart : Pie = AnyChart.pie()
            chart.background().fill(getColor(R.color.background))
            chart.innerRadius("85%")
            chart.legend(false)
            chart.labels(false)
//            chart.tooltip().useHtml(true)

//            val centerLabel = chart.label(1)
//            centerLabel.useHtml(true)
//            centerLabel.text("<b>Deck 1</b><br>New: 10<br>Mature: 12<br>Young: 14")
////            centerLabel.useHtml().
//            centerLabel.hAlign("center")
//            centerLabel.fontColor(getColor(R.color.bold_text))
//            centerLabel.offsetX("48%")
//            centerLabel.offsetY("50%")
//            centerLabel.anchor("center")

            chart.data(data)
            deckListView.setChart(chart)

            //set click listener
            binding.deckTitle.setOnClickListener{ clickListener() }
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
//        return decks.size
        return 10
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.databind(clickListener)
    }

    private fun getColor(color: Int): String{
        return "#" + Integer.toHexString(
            ContextCompat.getColor(
                context, color
            ) and 0x00ffffff
        )
    }

    private fun getDataEntry( x: String, value: Int, color: String): ValueDataEntry{
        val dataEntry = DataEntry()
        dataEntry.setValue("fill", color)
        val chartDataEntry = ValueDataEntry(x,value)
        chartDataEntry.setValue("normal", dataEntry)

        return chartDataEntry
    }
}


