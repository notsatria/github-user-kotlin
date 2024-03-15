package com.notsatria.githubusers.ui.detail

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.notsatria.githubusers.R
import com.notsatria.githubusers.adapter.SectionsPagerAdapter
import com.notsatria.githubusers.data.Result
import com.notsatria.githubusers.data.local.entity.UserEntity
import com.notsatria.githubusers.databinding.ActivityDetailBinding
import com.notsatria.githubusers.ui.setting.SettingViewModel
import com.notsatria.githubusers.ui.setting.SettingViewModelFactory

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private var userEntity: UserEntity? = null
    var darkModeColor: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Detail User"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f

        val username = intent.getStringExtra(DetailViewModel.EXTRA_USERNAME)
        val fab: FloatingActionButton = binding.fabFavorite

        Log.d("DetailActivity", "onCreate: $username")

        fab.imageTintList = ContextCompat.getColorStateList(this, R.color.error)
        fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_purple))

        val detailFactory = DetailViewModelFactory.getInstance(applicationContext)
        val detailViewModel: DetailViewModel by viewModels { detailFactory }

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
                                userEntity = user
                                with(binding) {
                                    tvNameDetail.text = user.name
                                    tvUsernameDetail.text = "@${user.login}"
                                    Glide.with(this@DetailActivity)
                                        .load(user.avatarUrl)
                                        .into(ivAvatarDetail)
                                    tvFollowersDetail.text = user.followers.toString()
                                    tvFollowingDetail.text = user.following.toString()
                                    fab.setImageResource(if (user.isFavorite) R.drawable.ic_favorite_24 else R.drawable.ic_favorite_border_24)
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

        val settingFactory: SettingViewModelFactory = SettingViewModelFactory.getInstance(applicationContext)
        val settingViewModel: SettingViewModel by viewModels{ settingFactory }

        settingViewModel.getThemeSettings().observe(this) {isDarkModeActive ->
            if (isDarkModeActive) {
                darkModeColor = ContextCompat.getColor(this, R.color.light_purple)
            } else {
                darkModeColor = ContextCompat.getColor(this, R.color.dark_purple)
            }

        }

        fab.setOnClickListener {
            favorite(fab, detailViewModel)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(isError: Boolean) {
       binding.rlError.visibility = if (isError) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.share_menu, menu)
        val menuItem = menu?.findItem(R.id.share)

        menuItem?.icon?.setTint(darkModeColor!!)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                return true
            }
            R.id.share -> {
                shareUser()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun shareUser() {
        userEntity?.let { user ->
            val shareText = "Check out this user at Github: ${user.htmlUrl}"
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }

    private fun favorite(fab: FloatingActionButton, viewModel: DetailViewModel) {
        userEntity?.let { user ->
            if (user.isFavorite) {
                viewModel.deleteFavoriteUser(user)
                showSnackbar("${user.login} removed from favorite")
            } else {
                viewModel.setFavoriteUser(user)
                showSnackbar("${user.login} added to favorite")
            }
            fab.setImageResource(if (user.isFavorite) R.drawable.ic_favorite_24 else R.drawable.ic_favorite_border_24)
        }
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            com.notsatria.githubusers.R.string.tab_followers,
            com.notsatria.githubusers.R.string.tab_following
        )
    }
}