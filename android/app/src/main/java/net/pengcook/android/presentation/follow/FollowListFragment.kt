package net.pengcook.android.presentation.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import net.pengcook.android.databinding.FragmentFollowListBinding

@AndroidEntryPoint
class FollowListFragment : Fragment() {
    private var _binding: FragmentFollowListBinding? = null
    private val binding: FragmentFollowListBinding
        get() = _binding!!
    private val viewPagerAdapter: ViewPagerAdapter by lazy {
        ViewPagerAdapter(this, listOf(FollowerListFragment(), FollowingListFragment()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFollowListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        val viewPager = binding.vpFollow
        val tabLayout = binding.tlFollow
        viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text =
                when (position) {
                    0 -> "Followers"
                    else -> "Following"
                }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
