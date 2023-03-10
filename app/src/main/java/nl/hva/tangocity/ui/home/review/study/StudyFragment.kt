package nl.hva.tangocity.ui.home.review.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import nl.hva.tangocity.convertToDateText
import nl.hva.tangocity.databinding.FragmentStudyBinding
import nl.hva.tangocity.model.Card
import nl.hva.tangocity.resetTime
import nl.hva.tangocity.snackBar
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

        // the cycle of studying below
        viewModel.currentCardSide.observe(viewLifecycleOwner) { side ->
            viewModel.studyPosition.observe(viewLifecycleOwner) { position ->
                // update study position
                studyPosition = position

                // in the case of finishing studying
                if (studyCards.size <= (position ?: 0)) {
                    val deck = deckViewModel.decks.value?.get(viewModel.selectedDeckPosition.value!!)

                    // update study cards and confirm whether cards are left yet or not
                    viewModel.updateStudyCards(deck?.cards) {
                        if (studyCards.isEmpty()) {
                            // in the case that cards aren't left
                            finishStudying()
                        } else {
                            // in the case that cards are left
                            viewModel.hideAnswer()
                            viewModel.resetStudyPosition()
                        }
                    }
                // in the case to continue
                }else { flipCard(side) }
            }
        }

        bindingAnswerButtons()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bindingAnswerButtons() {
        binding.questionContent.setOnClickListener{
            viewModel.showAnswer()
        }

        binding.buttonAnswer1.setOnClickListener{
            calculateCardResult(1 )
        }

        binding.buttonAnswer2.setOnClickListener{
            calculateCardResult(2 )
        }

        binding.buttonAnswer3.setOnClickListener{
            calculateCardResult(3 )
        }

        binding.buttonAnswer4.setOnClickListener{
            calculateCardResult(4 )
        }

        binding.buttonAnswer5.setOnClickListener{
            calculateCardResult(5 )
        }
    }

    private fun calculateCardResult(result: Int) {
        val deckPosition = viewModel.selectedDeckPosition.value!!
        val card = studyCards[studyPosition]
        var repetition = card.repetition
        var ease: Double =  card.easinessFactor
        var interval = card.interval

        // calculate how long later user will study this card
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
        card.lastResult = result

        val nextDate = Calendar.getInstance()
        nextDate.resetTime()
        nextDate.add(Calendar.DATE, interval)
        card.nextDate = nextDate

        //edit card info
        deckViewModel.editCard(deckPosition, card) {
            deckViewModel.createReview(deckPosition, card.id, card.lastResult)
            viewModel.nextCard()
            snackBar("Next day is " + nextDate.convertToDateText())
        }
    }

    private fun flipCard(side: Boolean) {
        changeButtonsVisible(side)
        binding.questionContent.text = if (side) {
            //answer
            studyCards[studyPosition].answer
        } else {
            //question
            studyCards[studyPosition].question
        }
    }

    private fun finishStudying () {
        binding.finishImage.visibility = View.VISIBLE
        binding.finishText.visibility = View.VISIBLE
        binding.cardLayout.visibility = View.INVISIBLE
        changeButtonsVisible(false)
    }

    private fun changeButtonsVisible(isShowAnswer: Boolean) {
        var mode = View.INVISIBLE
        var isClickable = false
        if (isShowAnswer) {
            mode = View.VISIBLE
            isClickable = true
        }
        binding.buttonCaption.visibility = mode
        binding.buttonCaptionBad.visibility = mode
        binding.buttonCaptionPerfect.visibility = mode

        binding.buttonAnswer1Text.visibility = mode
        binding.buttonAnswer2Text.visibility = mode
        binding.buttonAnswer3Text.visibility = mode
        binding.buttonAnswer4Text.visibility = mode
        binding.buttonAnswer5Text.visibility = mode

        binding.buttonAnswer1.isClickable = isClickable
        binding.buttonAnswer2.isClickable = isClickable
        binding.buttonAnswer3.isClickable = isClickable
        binding.buttonAnswer4.isClickable = isClickable
        binding.buttonAnswer5.isClickable = isClickable
    }
}