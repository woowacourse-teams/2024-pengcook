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
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.pengcook.android.R
import net.pengcook.android.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController: NavController

    private val destinationChangedListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            updateBottomNavVisibility(destination)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeNavController()
        initializeBottomNavigationView()
        if (savedInstanceState == null) {
            initializeStartDestination()
        }
    }

    private fun initializeSplashScreen() {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.uiState.value == null
            }
        }
    }

    private fun initializeNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        navController = navHostFragment.navController

        // 리스너 중복 추가 방지
        navController.removeOnDestinationChangedListener(destinationChangedListener)
        navController.addOnDestinationChangedListener(destinationChangedListener)

        // 현재 목적지에 따라 바텀 네비게이션 바 가시성 업데이트
        updateBottomNavVisibility(navController.currentDestination)
    }

    private fun initializeBottomNavigationView() {
        val bottomNav = binding.bottomNav
        NavigationUI.setupWithNavController(bottomNav, navController)
        binding.bottomNav.itemIconTintList = null
    }

    private fun updateBottomNavVisibility(destination: NavDestination?) {
        destination?.let {
            when (it.id) {
                R.id.homeFragment, R.id.searchFragment, R.id.profileFragment, R.id.categoryFragment ->
                    binding.bottomNav.visibility = View.VISIBLE

                else ->
                    binding.bottomNav.visibility = View.GONE
            }
        }
    }

    private fun initializeStartDestination() {
        val navInflater = navController.navInflater
        val graph = navInflater.inflate(R.navigation.nav_graph)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                handleUiEvent(graph)
            }
        }
    }

    private suspend fun handleUiEvent(graph: NavGraph) {
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

    private fun graphWithStartDestination(
        graph: NavGraph,
        startDestinationId: Int,
    ): NavGraph =
        graph.apply {
            setStartDestination(startDestinationId)
        }
}
