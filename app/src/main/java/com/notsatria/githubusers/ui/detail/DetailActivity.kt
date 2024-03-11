package com.notsatria.githubusers.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.notsatria.githubusers.R
import com.notsatria.githubusers.adapter.SectionsPagerAdapter
import com.notsatria.githubusers.data.Result
import com.notsatria.githubusers.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f

        val factory: DetailViewModelFactory = DetailViewModelFactory.getInstance(applicationContext)
        val detailViewModel: DetailViewModel by viewModels { factory }

        val username = intent.getStringExtra(DetailViewModel.EXTRA_USERNAME)

        let {
            if (username != null) {
                detailViewModel.getDetailUser(username).observe(this) {result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is Result.Success -> {
                                binding.progressBar.visibility = View.GONE
                                val user = result.data
                                with(binding) {
                                    tvNameDetail.text = user.name
                                    tvUsernameDetail.text = "@${user.login}"
                                    Glide.with(this@DetailActivity)
                                        .load(user.avatarUrl)
                                        .into(ivAvatarDetail)
                                    tvFollowersDetail.text = user.followers.toString()
                                    tvFollowingDetail.text = user.following.toString()
                                }
                            }
                            is Result.Error -> {
                                binding.progressBar.visibility = View.GONE
                                binding.rlError.visibility = View.VISIBLE
                            }

                            else -> {
                                binding.progressBar.visibility = View.GONE
                                binding.rlError.visibility = View.VISIBLE
                            }
                        }
                    }

                }
            }
        }

//        detailViewModel.detailUser.observe(this) {
//            Log.d(DetailViewModel.TAG, "detailUser: $it")
//            with(binding) {
//                tvNameDetail.text = detailViewModel.detailUser.value?.name
//                tvUsernameDetail.text = "@${detailViewModel.detailUser.value?.login}"
//                Glide.with(this@DetailActivity)
//                    .load(detailViewModel.detailUser.value?.avatarUrl)
//                    .into(ivAvatarDetail)
//                tvFollowersDetail.text = detailViewModel.detailUser.value?.followers.toString()
//                tvFollowingDetail.text = detailViewModel.detailUser.value?.following.toString()
//            }
//        }

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.favorite_menu, menu)
        val menuItem = menu?.findItem(R.id.favorite)
        menuItem?.icon?.setTint(ContextCompat.getColor(this, R.color.error))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                return true
            }
            R.id.favorite -> {

            }
        }

        return super.onOptionsItemSelected(item)
    }
}