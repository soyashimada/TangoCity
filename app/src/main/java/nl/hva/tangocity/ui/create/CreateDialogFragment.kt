package nl.hva.tangocity.ui.create

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import nl.hva.tangocity.databinding.CreateDialogBinding
import nl.hva.tangocity.viewModel.DeckViewModel

class CreateDialogFragment : DialogFragment() {
    private var _binding: CreateDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DeckViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreateDialogBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val transaction = parentFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)

        binding.cancelButton.setOnClickListener{
            findNavController().popBackStack()
            transaction.remove(this).commit()
        }

        binding.doneButton.setOnClickListener{
            viewModel.createDeckAndCard(binding.deckNameInput.text.toString(),
                binding.questionInput.text.toString(), binding.answerInput.text.toString())
            findNavController().popBackStack()
            transaction.remove(this).commit()
        }

        return root
    }

    /** The system calls this only when creating the layout in a dialog. */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}