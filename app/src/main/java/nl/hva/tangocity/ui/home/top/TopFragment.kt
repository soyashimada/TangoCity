package nl.hva.tangocity.ui.home.top

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.MonthDayBinder
import nl.hva.tangocity.*
import nl.hva.tangocity.databinding.FragmentTopBinding
import nl.hva.tangocity.ui.home.top.calendar.DayViewContainer
import nl.hva.tangocity.viewModel.DeckViewModel
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*


class TopFragment : Fragment() {

    private var _binding: FragmentTopBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DeckViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (activity as AppCompatActivity).setSupportActionBar(binding.toolBar)

        val calendarView = binding.reviewCalendar
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
                viewModel.reviews.observe(viewLifecycleOwner){
                    // the list of Review at this day
                    val reviews = it.filter {review -> review.date >= thisDay && review.date < nextDay }

                    // if user study at this day
                    if (reviews.isNotEmpty()) {
                        val circle = container.view.findViewById(R.id.calendarDayCircle) as ImageView
                        circle.showCircleOnCalendar(reviews)
                        container.textView.setTextColor(Color.parseColor(getColorCode(requireContext(), R.color.normal_text)))
                    }
                }
            }
        }

        // binding
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

        navController = findNavController()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set recyclerView
        viewModel.decks.observe(viewLifecycleOwner){
            val deckAdapter = DeckAdapter(viewModel.decks.value ?: arrayListOf(), requireContext()) { deckId: Int ->
                deckClicked(deckId)
            }
            binding.rvDecks.layoutManager =
                StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
            binding.rvDecks.adapter = deckAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun deckClicked(position: Int) {
        setFragmentResult("selectedDeck", bundleOf("position" to position))
        navController.navigate(
            R.id.action_TopFragment_to_ReviewFragment
        )
    }
}