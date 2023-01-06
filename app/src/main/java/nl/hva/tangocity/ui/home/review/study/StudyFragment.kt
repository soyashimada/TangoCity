package nl.hva.tangocity.ui.home.review.study

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import nl.hva.tangocity.databinding.FragmentStudyBinding
import nl.hva.tangocity.model.Card
import nl.hva.tangocity.viewModel.DeckViewModel
import nl.hva.tangocity.viewModel.ReviewViewModel
import kotlin.collections.ArrayList

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
                Log.i("test", studyPosition.toString())

                if (studyCards.size <= (position ?: 0)) {
                    binding.questionContent.isVisible = false
                }else {
                    binding.questionContent.text = if (side) {
                        //answer
                        studyCards[studyPosition].answer

                    } else {
                        //question
                        studyCards[studyPosition].question
                    }
                }
            }
        }

        binding.questionContent.setOnClickListener{
            viewModel.showAnswer()
        }

        binding.buttonAnswer1.setOnClickListener{
            viewModel.nextCard()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}