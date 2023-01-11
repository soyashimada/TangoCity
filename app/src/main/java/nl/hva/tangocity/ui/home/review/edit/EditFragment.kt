package nl.hva.tangocity.ui.home.review.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import nl.hva.tangocity.MainActivity
import nl.hva.tangocity.R
import nl.hva.tangocity.databinding.FragmentEditBinding
import nl.hva.tangocity.viewModel.DeckViewModel
import nl.hva.tangocity.viewModel.ReviewViewModel

class EditFragment : Fragment() {
    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!

    private val deckViewModel: DeckViewModel by activityViewModels()
    private val viewModel: ReviewViewModel by activityViewModels()
    private var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditBinding.inflate(inflater, container, false)
        val root: View = binding.root
        navController = parentFragment?.findNavController()

        //add card button
        binding.addCardFab.setOnClickListener{ cardClicked(null) }

        // set recyclerView
        binding.rvCardList.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        deckViewModel.decks.observe(viewLifecycleOwner){
            val deck = it[viewModel.selectedDeckPosition.value!!]
            val cardListAdapter = CardListAdapter(deck.cards) { position: Int ->
                cardClicked(position)
            }
            binding.rvCardList.adapter = cardListAdapter
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun cardClicked(position: Int?) {
        viewModel.updateSelectedCardPosition(position)
        MainActivity().onCreateFragment(requireActivity().findNavController(R.id.nav_host_fragment_activity_main))
    }
}
