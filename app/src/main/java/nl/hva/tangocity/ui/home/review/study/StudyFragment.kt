package nl.hva.tangocity.ui.home.review.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import nl.hva.tangocity.databinding.FragmentStudyBinding
import nl.hva.tangocity.model.Card
import nl.hva.tangocity.viewModel.DeckViewModel
import nl.hva.tangocity.viewModel.ReviewViewModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.round

class StudyFragment : Fragment() {
    private var _binding: FragmentStudyBinding? = null
    private val binding get() = _binding!!
    private var studyCards: ArrayList<Card> = arrayListOf()
    private var studyPosition: Int = 0

    private val viewModel: ReviewViewModel by activityViewModels()
    private val deckViewModel: DeckViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudyBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.studyCards.observe(viewLifecycleOwner) {
            studyCards = it
        }

        viewModel.currentCardSide.observe(viewLifecycleOwner) { side ->
            viewModel.studyPosition.observe(viewLifecycleOwner) { position ->
                studyPosition = position
                if (studyCards.size <= (position ?: 0)) {
                    // in the case that cards aren't left
                    binding.finishImage.visibility = View.VISIBLE
                    binding.finishText.visibility = View.VISIBLE
                    binding.cardLayout.visibility = View.INVISIBLE
                    changeButtonsVisible(View.INVISIBLE)

                }else {
                    binding.questionContent.text = if (side) {
                        //answer
                        changeButtonsVisible(View.VISIBLE)
                        studyCards[studyPosition].answer

                    } else {
                        //question
                        changeButtonsVisible(View.INVISIBLE)
                        studyCards[studyPosition].question
                    }
                }
            }
        }

        binding.questionContent.setOnClickListener{
            viewModel.showAnswer()
        }

        binding.buttonAnswer1.setOnClickListener{
            calculateCardResult(1, viewModel.selectedDeckPosition.value!!, studyCards[studyPosition])
        }

        binding.buttonAnswer2.setOnClickListener{
            calculateCardResult(2, viewModel.selectedDeckPosition.value!!, studyCards[studyPosition])
        }

        binding.buttonAnswer3.setOnClickListener{
            calculateCardResult(3, viewModel.selectedDeckPosition.value!!, studyCards[studyPosition])
        }

        binding.buttonAnswer4.setOnClickListener{
            calculateCardResult(4, viewModel.selectedDeckPosition.value!!, studyCards[studyPosition])
        }

        binding.buttonAnswer5.setOnClickListener{
            calculateCardResult(5, viewModel.selectedDeckPosition.value!!, studyCards[studyPosition])
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun calculateCardResult(result: Int, deckPosition: Int, card: Card) {
        var repetition = card.repetition
        var ease: Double =  card.easinessFactor
        var interval = card.interval

        if (result >= 3) {
            //correct response
            interval = when (repetition) {
                0 -> 1
                1 -> 6
                else -> round(ease * interval).toInt()
            }
            repetition += 1
        } else {
            //incorrect response
            repetition = 0
            interval = 1
        }
        ease += (0.1 - (5 - result) * (0.08 + (5 - result) * 0.02))
        if (ease < 1.3) {
            ease = 1.3
        }

        card.repetition = repetition
        card.easinessFactor = ease
        card.interval = interval
        card.nextDate.add(Calendar.DATE, interval)
        card.lastResult = result

        deckViewModel.editCard(deckPosition, card) {
            deckViewModel.createReview(deckPosition, card.id, card.lastResult)
            viewModel.nextCard()
        }
    }

    private fun changeButtonsVisible(mode: Int) {
        binding.answer1.visibility = mode
        binding.answer2.visibility = mode
        binding.answer3.visibility = mode
        binding.answer4.visibility = mode
        binding.answer5.visibility = mode
    }
}