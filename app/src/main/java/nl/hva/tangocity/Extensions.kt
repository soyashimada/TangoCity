package nl.hva.tangocity

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Pie
import com.google.android.material.snackbar.Snackbar
import com.kizitonwose.calendar.core.daysOfWeek
import nl.hva.tangocity.model.Card
import nl.hva.tangocity.model.Review
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

fun Fragment.snackBar (text: String) {
    Snackbar.make(requireView(), text, Snackbar.LENGTH_SHORT)
        .setBackgroundTint(Color.parseColor(getColorCode(requireContext(), R.color.snack_bar_back)))
        .setActionTextColor(Color.parseColor(getColorCode(requireContext(), R.color.normal_text)))
        .show()
}

fun getColorCode(context: Context, color: Int, percent: Int? = null): String{
    return ("#" + percentToHex(percent) + Integer.toHexString(
        ContextCompat.getColor(
            context, color
        ) and 0x00ffffff).uppercase(Locale.getDefault()))
}

private fun percentToHex(percent: Int? = null): String{
    if (percent == null) {
        return ""
    }
    return (255.0 * (percent / 100.0)).roundToInt().toString( 16 )
        .uppercase(Locale.getDefault()).padStart(2, '0')
}

// ---- Calendar part ---- //

fun Calendar.resetTime () {
    this.set(Calendar.MILLISECOND, 0)
    this.set(Calendar.SECOND, 0)
    this.set(Calendar.MINUTE, 0)
    this.set(Calendar.HOUR_OF_DAY, 0)
}

fun Calendar.convertToDateText(): String {
    return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(this.time)
}

fun com.kizitonwose.calendar.view.CalendarView.setReviewCalendar(thisMonth: YearMonth, titlesContainer: ViewGroup) {
    val startMonth = thisMonth.minusMonths(100)
    val endMonth = thisMonth.plusMonths(100)
    val daysOfWeek = daysOfWeek()

    titlesContainer.children
        .map { it as TextView }
        .forEachIndexed { index, textView ->
            val dayOfWeek = daysOfWeek[index]
            val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            textView.text = title
        }

    this.setup(startMonth, endMonth, daysOfWeek.first())
    this.scrollToMonth(thisMonth)
}

fun ImageView.showCircleOnCalendar(reviews: List<Review>) {
    // the target how many user want to memorize
    val targetNum = 100.0
    // the number to do this day / the target
    var percent = ((reviews.size/targetNum)*60.0).toInt()
    percent = percent.let {p -> if (p < 20) 20 else if (p > 60) 60 else p }

    //update layout
    val colorCode = getColorCode(context, R.color.normal_icon, percent)

    this.setColorFilter(Color.parseColor(colorCode))
    this.visibility = View.VISIBLE
}

// ---- Graph part ---- //
fun Pie.makeDeckChart(context: Context, cards: ArrayList<Card>): Map<String, Int>{
    this.background().fill(getColorCode(context, R.color.background))
    this.innerRadius("85%")
    this.legend(false)
    this.labels(false)
    this.width("105%")
    this.height("105%")

    val data: MutableList<DataEntry> = ArrayList()
    var newCount = 0
    var matureCount = 0
    var youngCount = 0
    cards.forEach{ card ->
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

    this.data(data)
    return mapOf(
        "New" to newCount,
        "Young" to youngCount,
        "Mature" to matureCount
    )
}

private fun getDataEntry( x: String, value: Int, color: String): ValueDataEntry {
    val dataEntry = DataEntry()
    dataEntry.setValue("fill", color)
    val chartDataEntry = ValueDataEntry(x,value)
    chartDataEntry.setValue("normal", dataEntry)

    return chartDataEntry
}