package net.pengcook.android.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import net.pengcook.android.MainFragment
import net.pengcook.android.databinding.FragmentDetailRecipeBinding
import net.pengcook.android.domain.model.Recipe
import net.pengcook.android.domain.model.User

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

    fun fetchRecipe() {
        /*val argument = arguments?.getParcelable(RECIPE_KEY) as Recipe?
        if (argument is Recipe) {
            recipe = argument
        }*/
        recipe = SAMPLE_RECIPE
        if (recipe != null) {
            binding.recipe = recipe
        }
    }

    companion object {
        private const val RECIPE_KEY = "recipe_key"
        private val SAMPLE_RECIPE =
            Recipe(
                title = "Spaghetti Carbonara",
                category = "Pasta",
                thumbnail = "https://static01.nyt.com/images/2021/02/14/dining/carbonara-horizontal/carbonara-horizontal-square640-v2.jpg",
                user =
                    User(
                        username = "John Doe",
                        profile = "https://cdn.download.ams.birds.cornell.edu/api/v2/asset/612763581/900",
                    ),
                favoriteCount = 100,
                ingredients =
                    listOf(
                        "Spaghetti",
                        "Egg",
                        "Bacon",
                        "Parmesan Cheese",
                        "Black Pepper",
                    ),
                timeRequired = 30,
                difficulty = "Easy",
                introduction = "Spaghetti Carbonara is a classic Italian pasta dish.",
            )

        fun newInstance(recipe: Recipe): MainFragment =
            MainFragment().apply {
                arguments =
                    Bundle().apply {
                        putParcelable(RECIPE_KEY, recipe)
                    }
            }
    }
}
