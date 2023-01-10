package nl.hva.tangocity.ui.home.review.stat

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.anychart.AnyChart
import com.anychart.charts.Pie
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.MonthDayBinder
import nl.hva.tangocity.*
import nl.hva.tangocity.databinding.FragmentStatBinding
import nl.hva.tangocity.model.Deck
import nl.hva.tangocity.ui.home.top.calendar.DayViewContainer
import nl.hva.tangocity.viewModel.DeckViewModel
import nl.hva.tangocity.viewModel.ReviewViewModel
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

class StatFragment : Fragment() {
    private var _binding: FragmentStatBinding? = null
    private val binding get() = _binding!!

    private val deckViewModel: DeckViewModel by activityViewModels()
    private val viewModel: ReviewViewModel by activityViewModels()
    private var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatBinding.inflate(inflater, container, false)
        val root: View = binding.root
        navController = parentFragment?.findNavController()

        val deck = deckViewModel.decks.value?.get(viewModel.selectedDeckPosition.value!!) ?: return root
        initCardCountsView(deck)
        initCalendarView(root, deck)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initCardCountsView(deck: Deck){
        val deckChart = binding.deckChart
        deckChart.setBackgroundColor(getColorCode(requireContext(), R.color.background))
        val chart : Pie = AnyChart.pie()
        val counts = chart.makeDeckChart(requireContext(), deck.cards)
        deckChart.setChart(chart)

        deck.name.let {
            binding.deckTitle.text = if (it.length > 10) {
                it.take(10) + "..."
            }else {
                it
            }
        }
        binding.deckSubTitle.text = String.format("Total: %d", deck.cards.size)
        binding.deckInfo.text = String.format("New: %d\n Young: %d\nMature: %d", counts["New"], counts["Young"], counts["Mature"])
    }

    private fun initCalendarView(root: View, deck: Deck) {
        val calendarView = binding.statCalendar
        val thisMonth = YearMonth.now()
        var currentMonth = thisMonth
        val titlesContainer = root.findViewById<ViewGroup>(R.id.titlesContainer)
        calendarView.setReviewCalendar(thisMonth, titlesContainer)

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                val thisDay = Calendar.getInstance()
                thisDay.set(data.date.year, data.date.monthValue-1, data.date.dayOfMonth)
                thisDay.resetTime()

                val nextDay = thisDay.clone() as Calendar
                nextDay.add(Calendar.DAY_OF_MONTH, 1)

                container.textView.text = data.date.dayOfMonth.toString()
                deckViewModel.reviews.observe(viewLifecycleOwner){
                    // the list of Review at this day
                    val reviews = it.filter {review -> review.date >= thisDay && review.date < nextDay && review.deckId == deck.id }

                    // if user study at this day
                    if (reviews.isNotEmpty()) {
                        val circle = container.view.findViewById(R.id.calendarDayCircle) as ImageView
                        circle.showCircleOnCalendar(reviews)
                        container.textView.setTextColor(Color.parseColor(getColorCode(requireContext(), R.color.normal_text)))
                    }
                }
            }
        }

        val formatter = DateTimeFormatter.ofPattern("LLL yyyy")
        calendarView.monthScrollListener = {
            binding.monthYearTxt.text = it.yearMonth.format(formatter)
        }
        binding.monthYearTxt.text =  thisMonth.format(formatter)
        binding.nextMonthImg.setOnClickListener{
            currentMonth = currentMonth.plusMonths(1)
            calendarView.scrollToMonth(currentMonth)
        }
        binding.previousMonthImg.setOnClickListener{
            currentMonth = currentMonth.minusMonths(1)
            calendarView.scrollToMonth(currentMonth)
        }
    }
}