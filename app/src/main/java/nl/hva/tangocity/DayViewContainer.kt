package nl.hva.tangocity

import android.view.View
import com.kizitonwose.calendar.view.ViewContainer
import nl.hva.tangocity.databinding.CalendarDayLayoutBinding

class DayViewContainer(view: View) : ViewContainer(view) {
    val textView = CalendarDayLayoutBinding.bind(view).calendarDayText

}