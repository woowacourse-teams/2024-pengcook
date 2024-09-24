package net.pengcook.android.presentation.main

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.pengcook.android.R
import net.pengcook.android.databinding.ActivityMainBinding
import net.pengcook.android.presentation.DefaultPengcookApplication

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewModel: MainViewModel by viewModels {
        val application = application as DefaultPengcookApplication
        MainViewModelFactory(
            application.appModule.authorizationRepository,
            application.appModule.sessionRepository,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeSplashScreen()
        setContentView(binding.root)
        initializeNavigation()
    }

    private fun initializeSplashScreen() {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.uiState.value == null
            }
        }
    }

    private fun initializeNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        val navController = navHostFragment.navController
        initializeStartDestination(navController)
        initializeBottomNavigationView(navController)
    }

    private fun initializeStartDestination(navController: NavController) {
        val navInflater = navController.navInflater
        val graph = navInflater.inflate(R.navigation.nav_graph)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                handleUiEvent(navController, graph)
            }
        }
    }

    private suspend fun handleUiEvent(
        navController: NavController,
        graph: NavGraph,
    ) {
        viewModel.uiState.collect { event ->
            if (event == null) return@collect
            when (event) {
                MainUiEvent.NavigateToMain -> {
                    navController.graph = graphWithStartDestination(graph, R.id.homeFragment)
                }

                MainUiEvent.NavigateToOnboarding -> {
                    navController.graph = graphWithStartDestination(graph, R.id.onboardingFragment)
                }
            }
        }
    }

    private fun initializeBottomNavigationView(navController: NavController) {
        val bottomNav = binding.bottomNav
        NavigationUI.setupWithNavController(bottomNav, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.searchFragment, R.id.profileFragment, R.id.categoryFragment ->
                    bottomNav.visibility = View.VISIBLE

                else -> bottomNav.visibility = View.GONE
            }
        }

        binding.bottomNav.itemIconTintList = null
    }

    private fun graphWithStartDestination(
        graph: NavGraph,
        startDestinationId: Int,
    ): NavGraph = graph.apply { setStartDestination(startDestinationId) }
}
