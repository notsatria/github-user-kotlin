package com.notsatria.githubusers.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.notsatria.githubusers.adapter.GithubUserItemAdapter
import com.notsatria.githubusers.databinding.ActivityMainBinding
import com.notsatria.githubusers.response.User
import com.notsatria.githubusers.viewmodel.DetailViewModel
import com.notsatria.githubusers.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var rvGithubUser: RecyclerView
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvGithubUser = binding.rvSearchResult

        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MainViewModel::class.java)

        mainViewModel.searchUser("a", applicationContext)


        mainViewModel.userList.observe(this) {
            it.getContentIfNotHandled()?.let { userList ->
                showRecyclerList(userList)
            }
        }

        with(binding) {
            searchView.setupWithSearchBar(searchBar)

            searchView
                .editText
                .setOnEditorActionListener { textView, _, _ ->
                    val query = textView.text.toString()
                    if (query.isNotEmpty()) {
                        mainViewModel.searchUser(query, applicationContext)
                    }
                    searchView.hide()

                    false
                }
        }

        mainViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        mainViewModel.isError.observe(this) { isError ->
            showError(isError)
        }

        mainViewModel.isNotFound.observe(this) { isNotFound ->
            showNotFound(isNotFound)
        }

    }

    private fun showRecyclerList(userList: List<User>) {
        val adapter = GithubUserItemAdapter(userList)
        rvGithubUser.layoutManager = LinearLayoutManager(this@MainActivity)
        rvGithubUser.adapter = adapter
        rvGithubUser.setHasFixedSize(true)

        adapter.setOnItemClickCallback(object: GithubUserItemAdapter.OnItemClickCallback {
            override fun onItemClicked(data: String) {
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                intent.putExtra(DetailViewModel.EXTRA_USERNAME, data)
                Log.d("MainActivity", "onItemClicked: $data")
                startActivity(intent)
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(isError: Boolean) {
        binding.rlError.visibility = if (isError) View.VISIBLE else View.GONE
    }

    private fun showNotFound(isError: Boolean) {
        binding.rlEmptySearch.visibility = if (isError) View.VISIBLE else View.GONE
    }
}