package net.pengcook.android.presentation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import net.pengcook.android.R
import net.pengcook.android.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
//    private val

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Security.insertProviderAt(Conscrypt.newProvider(), 1)
        setContentView(binding.root)

        val splashScreen = installSplashScreen()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment

        val navController = navHostFragment.navController
        val bottomNav =
            findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_nav)
        NavigationUI.setupWithNavController(bottomNav, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.searchFragment, R.id.profileFragment, R.id.categoryFragment ->
                    bottomNav.visibility =
                        View.VISIBLE

                else -> bottomNav.visibility = View.GONE
            }
        }

        binding.bottomNav.itemIconTintList = null
    }
}
