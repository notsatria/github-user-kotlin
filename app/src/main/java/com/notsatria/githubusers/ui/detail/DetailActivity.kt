package com.notsatria.githubusers.ui.detail

import android.content.Intent
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
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.notsatria.githubusers.R
import com.notsatria.githubusers.adapter.SectionsPagerAdapter
import com.notsatria.githubusers.data.Result
import com.notsatria.githubusers.data.local.entity.UserEntity
import com.notsatria.githubusers.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

//    val factory: DetailViewModelFactory = DetailViewModelFactory.getInstance(applicationContext)
//    val detailViewModel: DetailViewModel by viewModels { factory }

    private lateinit var factory: DetailViewModelFactory
    private val detailViewModel: DetailViewModel by viewModels { factory }

    private var userEntity: UserEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Detail User"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f

        val username = intent.getStringExtra(DetailViewModel.EXTRA_USERNAME)

        factory = DetailViewModelFactory.getInstance(applicationContext)

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

        binding.fabShare.setOnClickListener {
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

        userEntity?.let { user ->
            menuItem?.setIcon(if (user.isFavorite) R.drawable.ic_favorite_24 else R.drawable.ic_favorite_border_24)
        }

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
                userEntity?.let { user ->
                    if (user.isFavorite) {
                        detailViewModel.deleteFavoriteUser(user)
                        showSnackbar("${user.login} removed from favorite")
                    } else {
                        detailViewModel.setFavoriteUser(user)
                        showSnackbar("${user.login} added to favorite")
                    }

                    item.setIcon(if (user.isFavorite) R.drawable.ic_favorite_24 else R.drawable.ic_favorite_border_24)
                    item.icon?.setTint(ContextCompat.getColor(this, R.color.error))
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            com.notsatria.githubusers.R.string.tab_followers,
            com.notsatria.githubusers.R.string.tab_following
        )
    }
}