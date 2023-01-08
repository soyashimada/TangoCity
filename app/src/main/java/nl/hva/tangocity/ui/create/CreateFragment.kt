package nl.hva.tangocity.ui.create

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import nl.hva.tangocity.R
import nl.hva.tangocity.databinding.FragmentCreateBinding
import nl.hva.tangocity.model.Card
import nl.hva.tangocity.snackBar
import nl.hva.tangocity.viewModel.DeckViewModel
import nl.hva.tangocity.viewModel.ReviewViewModel

class CreateFragment : Fragment() {
    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!
    private val deckViewModel: DeckViewModel by activityViewModels()
    private val reviewViewModel: ReviewViewModel by activityViewModels()
    private var deckNameList: MutableList<String> = mutableListOf()
    private var isFromBottomBar: Boolean = false

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
            isFromBottomBar = true
            deckViewModel.decks.observe(viewLifecycleOwner){
                it.forEach{ deck -> deckNameList.add(deck.name) }
                changeDropDown()
            }
            binding.deckNameInputLayout.isVisible = true
            binding.questionInput.setText("")
            binding.answerInput.setText("")
            changeDropDown()
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
            deckViewModel.decks.value?.get(deckPosition)!!.cards[cardPosition]
        } else null

        binding.questionInput.setText(card?.question ?: "")
        binding.answerInput.setText(card?.answer ?: "")

        binding.cancelButton.setOnClickListener{
            findNavController().popBackStack()
        }

        binding.doneButton.setOnClickListener{
            if (validateForm()) {
                val question = binding.questionInput.text.toString()
                val answer = binding.answerInput.text.toString()

                if (isFromBottomBar) {
                    deckViewModel.createDeckAndCard(binding.deckNameInput.text.toString(),
                        question, answer, backPage())
                } else {
                    if (card != null && cardPosition!= null) {
                        card.question = question
                        card.answer = answer
                        deckViewModel.editCard(deckPosition, card, backPage())
                    }else {
                        deckViewModel.createCard(deckPosition, question, answer, backPage())
                    }
                }
            }
        }

        binding.isNewCheckbox.setOnCheckedChangeListener { _, _ ->
            changeDropDown()
        }
    }

    private fun validateForm(): Boolean {
        if (isFromBottomBar) {
            val inputText = binding.deckNameInput.text
            if (inputText.isBlank()) {
                snackBar("You need input 'DeckName'")
                return false
            }else if (binding.isNewCheckbox.isChecked) {
                if (deckNameList.contains(inputText.toString())) {
                    snackBar("You already made '$inputText'")
                    return false
                }
            }
        }

        if (binding.questionInput.text.contentEquals("")) {
            snackBar("You need input 'Question'")
            return false
        }

        return true
    }

    private fun backPage(): () -> Unit{
        return {
            findNavController().popBackStack()
        }
    }

    private fun changeDropDown() {
        val nameInput = binding.deckNameInput
        val checkbox = binding.isNewCheckbox

        checkbox.isVisible = deckNameList.isNotEmpty()

        if (!binding.isNewCheckbox.isChecked && deckNameList.isNotEmpty()) {
            nameInput.inputType = InputType.TYPE_NULL
            val adapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), R.layout.item_deck_name, deckNameList)
            nameInput.setAdapter(adapter)
        } else {
            nameInput.inputType = InputType.TYPE_CLASS_TEXT
            nameInput.setAdapter(null)
        }
    }
}