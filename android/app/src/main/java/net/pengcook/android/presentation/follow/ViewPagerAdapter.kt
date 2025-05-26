package net.pengcook.android.presentation.follow

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    hostFragment: Fragment,
    private val pages: List<Fragment>,
) : FragmentStateAdapter(hostFragment) {
    override fun getItemCount(): Int {
        return pages.size
    }

    override fun createFragment(position: Int): Fragment {
        return pages[position]
    }
}
