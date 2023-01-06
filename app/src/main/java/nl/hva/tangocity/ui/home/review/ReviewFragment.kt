package nl.hva.tangocity.ui.home.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.tabs.TabLayout
import nl.hva.tangocity.R
import nl.hva.tangocity.databinding.FragmentReviewBinding
import nl.hva.tangocity.viewModel.DeckViewModel
import nl.hva.tangocity.viewModel.ReviewViewModel

class ReviewFragment : Fragment() {
    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!

    private val deckViewModel: DeckViewModel by activityViewModels()
    private val viewModel: ReviewViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolBar.setupWithNavController(navController, appBarConfiguration)

        setFragmentResultListener("selectedDeck"){ requestKey, bundle ->
            val position = bundle.getInt("position")
            if (viewModel.selectedDeckPosition.value != position){
                viewModel.resetSelectedTab()
                viewModel.updateSelectedDeckPosition(position)
            }
            val deck = deckViewModel.decks.value?.get(viewModel.selectedDeckPosition.value!!)
            binding.toolBar.title = deck?.name ?: "undefined"
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val nestedNavHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_fragment_review) as? NavHostFragment
        val navController = nestedNavHostFragment?.navController

        viewModel.selectedTab.observe(viewLifecycleOwner){
            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(it))
        }

        binding.tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewModel.updateSelectedTab(tab.position)
                when(tab.position)
                {
                    0 -> {
                        navController?.navigate(R.id.navigation_study)
                    }
                    2 -> {
                        navController?.navigate(R.id.navigation_edit)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }
            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
