package com.notsatria.githubusers.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.notsatria.githubusers.adapter.SectionsPagerAdapter
import com.notsatria.githubusers.databinding.ActivityDetailBinding
import com.notsatria.githubusers.viewmodel.DetailViewModel

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var detailViewModel: DetailViewModel

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            com.notsatria.githubusers.R.string.tab_followers,
            com.notsatria.githubusers.R.string.tab_following
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Detail User"

        detailViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(DetailViewModel::class.java)

        val username = intent.getStringExtra(DetailViewModel.EXTRA_USERNAME)

        let {
            if (username != null) {
                detailViewModel.getDetailUser(username, applicationContext)
            }
        }

        detailViewModel.detailUser.observe(this) {
            Log.d(DetailViewModel.TAG, "detailUser: $it")
            with(binding) {
                tvNameDetail.text = detailViewModel.detailUser.value?.name
                tvUsernameDetail.text = "@${detailViewModel.detailUser.value?.login}"
                Glide.with(this@DetailActivity)
                    .load(detailViewModel.detailUser.value?.avatarUrl)
                    .into(ivAvatarDetail)
                tvFollowersDetail.text = detailViewModel.detailUser.value?.followers.toString()
                tvFollowingDetail.text = detailViewModel.detailUser.value?.following.toString()
            }
        }

        detailViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        val sectionsPagerAdapter = SectionsPagerAdapter(this)

        sectionsPagerAdapter.username = username

        val viewPager: ViewPager2 = binding.vpDetail
        viewPager.adapter = sectionsPagerAdapter

        val tabs: TabLayout = binding.tlDetail

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        supportActionBar?.elevation = 0f

        detailViewModel.isError.observe(this) { isError ->
            showError(isError)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(isError: Boolean) {
       binding.rlError.visibility = if (isError) View.VISIBLE else View.GONE
    }
}