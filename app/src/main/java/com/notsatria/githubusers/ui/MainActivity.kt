package com.notsatria.githubusers.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.notsatria.githubusers.adapter.GithubUserItemAdapter
import com.notsatria.githubusers.data.User
import com.notsatria.githubusers.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var rvGithubUser: RecyclerView

    companion object {
        private const val TAG = "MainActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val list = ArrayList<User>()

        val adapter = GithubUserItemAdapter(list)

        for (i in 0..10) {
            list.add(User("username $i", "name $i", i, i, i))
        }

        adapter.notifyDataSetChanged()

        with(binding) {
            showRecyclerList(adapter, rvSearchResult)

            searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText != null) {
                        adapter.filterList(newText)
                    }
                    return true
                }
            })

        }

    }

    private fun showRecyclerList(adapter: GithubUserItemAdapter, rvSearchResult: RecyclerView) {
        rvGithubUser = rvSearchResult
        rvGithubUser.layoutManager = LinearLayoutManager(this@MainActivity)
        rvGithubUser.adapter = adapter
        rvGithubUser.setHasFixedSize(true)
    }
}