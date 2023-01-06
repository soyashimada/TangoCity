package nl.hva.tangocity.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import nl.hva.tangocity.databinding.FragmentCreateBinding
import nl.hva.tangocity.model.Card
import nl.hva.tangocity.viewModel.DeckViewModel
import nl.hva.tangocity.viewModel.ReviewViewModel

class CreateFragment : Fragment() {
    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!
    private val deckViewModel: DeckViewModel by activityViewModels()
    private val reviewViewModel: ReviewViewModel by activityViewModels()
    private var isNewDeck: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initializeView()

        // in the case of creating new deck from bottom bar
        requireActivity().supportFragmentManager.setFragmentResultListener("new_deck", this){requestKey, bundle ->
            isNewDeck = true
            binding.deckNameInputLayout.isVisible = true
            binding.isNewCheckbox.isVisible = true
            binding.questionInput.setText("")
            binding.answerInput.setText("")
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initializeView() {
        val deckPosition: Int = reviewViewModel.selectedDeckPosition.value ?: 0
        val cardPosition: Int? = reviewViewModel.selectedCardPosition.value

        val card: Card? = if (cardPosition != null){
            deckViewModel.decks.value?.let { it[deckPosition].cards[cardPosition] }
        } else null

        binding.questionInput.setText(card?.question ?: "")
        binding.answerInput.setText(card?.answer ?: "")

        binding.cancelButton.setOnClickListener{
            findNavController().popBackStack()
        }

        binding.doneButton.setOnClickListener{
            if (isNewDeck) {
                deckViewModel.createDeckAndCard(binding.deckNameInput.text.toString(),
                    binding.questionInput.text.toString(), binding.answerInput.text.toString(), backPage())

            } else {
                deckViewModel.createCard(deckPosition ?: 0, binding.questionInput.text.toString(),
                    binding.answerInput.text.toString(), cardPosition, backPage())
            }

        }
    }

    private fun backPage(): () -> Unit{
        return {
            findNavController().popBackStack()
        }
    }
}