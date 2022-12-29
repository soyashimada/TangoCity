package nl.hva.tangocity.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import nl.hva.tangocity.R
import nl.hva.tangocity.databinding.FragmentHomeBinding
import nl.hva.tangocity.model.Deck
import nl.hva.tangocity.ui.home.calendar.DayViewContainer
import nl.hva.tangocity.viewModel.DeckViewModel
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*


class HomeFragment : Fragment() {

    private var _binding: nl.hva.tangocity.databinding.FragmentHomeBinding? = null
    private val viewModel: DeckViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.initialize()

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (activity as AppCompatActivity).setSupportActionBar(binding.toolBar)

        val calendarView = binding.reviewCalendar
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)  // Adjust as needed
        val endMonth = currentMonth.plusMonths(100)  // Adjust as needed
        val daysOfWeek = daysOfWeek()// Available from the library

        val titlesContainer = root.findViewById<ViewGroup>(R.id.titlesContainer)
        titlesContainer.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                val dayOfWeek = daysOfWeek[index]
                val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                textView.text = title
            }

        calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        calendarView.scrollToMonth(currentMonth)

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.textView.text = data.date.dayOfMonth.toString()
            }
        }

        navController = findNavController()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.decks.observe(viewLifecycleOwner){
            val deckAdapter = DeckAdapter(viewModel.decks.value ?: arrayListOf<Deck>(), requireContext()) {
                deckClicked()
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

    private fun deckClicked() {
        navController.navigate(
            R.id.action_HomeFragment_to_ReviewFragment
        )
    }
}