package net.pengcook.android.presentation.onboarding

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.pengcook.android.BuildConfig
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentOnboardingBinding
import net.pengcook.android.domain.model.auth.Platform
import net.pengcook.android.presentation.DefaultPengcookApplication

class OnboardingFragment : Fragment() {
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleGoogleSignInResult(task)
            }
        }
    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                .requestEmail()
                .build()
        GoogleSignIn.getClient(requireContext(), gso)
    }

    private var _binding: FragmentOnboardingBinding? = null
    private val binding: FragmentOnboardingBinding
        get() = _binding!!

    private val viewModel: OnboardingViewModel by viewModels {
        val application = (requireContext().applicationContext) as DefaultPengcookApplication
        val module = application.appModule
        OnboardingViewModelFactory(
            module.authorizationRepository,
            module.tokenRepository,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        observeUiEvents()
        observeLoadingStatus()
        initializeSignInButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeLoadingStatus() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                binding.pbOnboarding.visible = isLoading
            }
        }
    }

    private fun observeUiEvents() {
        viewModel.uiEvent.observe(viewLifecycleOwner) { uiEvent ->
            val event = uiEvent.getContentIfNotHandled() ?: return@observe
            when (event) {
                is OnboardingUiEvent.Error -> {
                    Snackbar.make(
                        binding.root,
                        R.string.onboarding_network_error,
                        Snackbar.LENGTH_SHORT,
                    ).show()
                }

                is OnboardingUiEvent.NavigateToMain -> {
                    findNavController().navigate(R.id.action_onboardingFragment_to_homeFragment)
                }

                is OnboardingUiEvent.NavigateToSignUp -> {
                    val action =
                        OnboardingFragmentDirections.actionOnboardingFragmentToSignUpFragment(event.platformName)
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun initializeSignInButton() {
        val signInIntent = googleSignInClient.signInIntent
        binding.btnGoogleSignIn.btnSignIn.setOnClickListener {
            googleSignInLauncher.launch(signInIntent)
        }
    }

    private fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.result
            val token = account.idToken ?: return
            firebaseAuthWithGoogle(token)
        } catch (e: ApiException) {
            showSnackBar(getString(R.string.onboarding_network_error))
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task ->
            signInWithServer(task)
        }
    }

    private fun signInWithServer(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            auth.currentUser?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                val platform = Platform.GOOGLE.platformName
                val token = tokenTask.result.token ?: return@addOnCompleteListener
                viewModel.signIn(platform, token)
            }
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}
