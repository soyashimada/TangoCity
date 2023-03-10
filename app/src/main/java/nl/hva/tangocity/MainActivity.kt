package nl.hva.tangocity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import nl.hva.tangocity.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        val navView: BottomNavigationView = binding.navView
        navView.setupWithNavController(navController)

        navView.setOnItemSelectedListener { item ->
            return@setOnItemSelectedListener onNavItemDestinationSelected(
                item,
                navController
            )
        }

        navController.addOnDestinationChangedListener {_, destination, _ ->
            if(destination.id == R.id.navigation_create) {
                navView.visibility = View.GONE
            } else {
                navView.visibility = View.VISIBLE
            }
        }
    }

    private fun onNavItemDestinationSelected(
        item: MenuItem,
        navController: NavController
    ): Boolean {
        return try {
            when(item.itemId)
            {
                R.id.navigation_create -> {
                    // notify that user come from bottom bar to CreateFragment
                    supportFragmentManager.setFragmentResult("new_deck", bundleOf())
                    onCreateFragment(findNavController(R.id.nav_host_fragment_activity_main))
                }

                else -> navController.navigate(item.itemId)
            }
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun onCreateFragment(navController: NavController) {
        navController.navigate(R.id.navigation_create)
    }
}