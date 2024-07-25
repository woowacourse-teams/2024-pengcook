package net.pengcook.android.presentation.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentCategoryBinding
import net.pengcook.android.presentation.core.style.GridSpacingItemDecoration

class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val binding: FragmentCategoryBinding
        get() = _binding!!
    private val viewModel: CategoryViewModel by viewModels()
    private val adapter: CategoryAdapter by lazy { CategoryAdapter(viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.adapter = adapter
        val categories =
            List(17) { id ->
                Category(
                    id.toLong(),
                    "category ${id + 1}",
                    "https://www.alphafoodie.com/wp-content/uploads/2021/06/Authentic-Kimchi-1-of-1-2.jpeg",
                )
            }
        adapter.submitList(categories)

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.item_spacing_category)
        binding.rvCategory.addItemDecoration(GridSpacingItemDecoration(3, spacingInPixels, false))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
