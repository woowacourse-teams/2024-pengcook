package net.pengcook.android.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import net.pengcook.android.databinding.FragmentDetailRecipeBinding
import net.pengcook.android.presentation.core.model.Recipe

class DetailRecipeFragment : Fragment() {
    private val binding by lazy { FragmentDetailRecipeBinding.inflate(layoutInflater) }
    private val viewModel by lazy { DetailRecipeViewModel(recipe) }
    private lateinit var recipe: Recipe

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = binding.root

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        fetchRecipe()
    }

    private fun fetchRecipe() {
        val argument = arguments?.getParcelable(RECIPE_KEY) as Recipe?
        if (argument is Recipe) {
            recipe = argument
        }
        binding.recipe = recipe
    }

    companion object {
        private const val RECIPE_KEY = "recipe_key"
    }
}
