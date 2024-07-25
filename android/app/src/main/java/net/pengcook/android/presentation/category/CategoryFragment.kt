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
                    "https://cdn.apartmenttherapy.info/image/upload/f_auto,q_auto:eco,c_fill,g_auto,w_1500,ar_3:2/k%2FPhoto%2FSeries%2F2023-11-how-to-make-kimchi%2Fhow-to-make-kimchi-259",
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
