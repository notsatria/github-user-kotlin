package com.notsatria.githubusers.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.notsatria.githubusers.adapter.GithubUserItemAdapter
import com.notsatria.githubusers.data.Result
import com.notsatria.githubusers.data.local.entity.UserEntity
import com.notsatria.githubusers.databinding.FragmentHomeBinding
import com.notsatria.githubusers.ui.detail.DetailActivity
import com.notsatria.githubusers.ui.detail.DetailViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var rvGithubUser: RecyclerView
    private lateinit var clMain: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        rvGithubUser = binding.rvSearchResult
        clMain = binding.clMain

        val factory: HomeViewModelFactory = HomeViewModelFactory.getInstance(requireContext())
        val homeViewModel: HomeViewModel by viewModels { factory }

        homeViewModel.searchUser("a").observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.rlError.visibility = View.GONE
                        binding.rlEmptySearch.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE
                        val userList = result.data
                        showRecyclerList(userList, homeViewModel)
                    }
                    is Result.Empty -> {
                        binding.progressBar.visibility = View.GONE
                        binding.rlEmptySearch.visibility = View.VISIBLE
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.rlError.visibility = View.VISIBLE
                    }
                }
            }
        }

        with(binding) {
            searchView.setupWithSearchBar(searchBar)

            searchView
                .editText
                .setOnEditorActionListener { textView, _, _ ->
                    val query = textView.text.toString()
                    if (query.isNotEmpty()) {
                        homeViewModel.searchUser(query).observe(viewLifecycleOwner ) { result ->
                            if (result != null) {
                                when (result) {
                                    is Result.Loading -> {
                                        binding.progressBar.visibility = View.VISIBLE
                                    }
                                    is Result.Success -> {
                                        binding.rlError.visibility = View.GONE
                                        binding.rlEmptySearch.visibility = View.GONE
                                        binding.progressBar.visibility = View.GONE
                                        val userList = result.data
                                        showRecyclerList(userList, homeViewModel)
                                    }
                                    is Result.Empty -> {
                                        binding.progressBar.visibility = View.GONE
                                        binding.rlEmptySearch.visibility = View.VISIBLE
                                    }
                                    is Result.Error -> {
                                        binding.progressBar.visibility = View.GONE
                                        binding.rlError.visibility = View.VISIBLE
                                    }
                                }
                            }

                        }
                    }
                    searchView.hide()

                    false
                }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showRecyclerList(userList: List<UserEntity>, homeViewModel: HomeViewModel) {
        val adapter = GithubUserItemAdapter(userList)
        rvGithubUser.layoutManager = LinearLayoutManager(requireContext())
        rvGithubUser.adapter = adapter
        rvGithubUser.setHasFixedSize(true)

        adapter.setOnItemClickCallback(object: GithubUserItemAdapter.OnItemClickCallback {
            override fun onItemClicked(data: String) {
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra(DetailViewModel.EXTRA_USERNAME, data)
                Log.d("MainActivity", "onItemClicked: $data")
                startActivity(intent)
            }

            override fun onFavoriteClicked(userEntity: UserEntity) {
                userEntity.isFavorite = !userEntity.isFavorite
                adapter.notifyDataSetChanged()

                if (userEntity.isFavorite) {
                    homeViewModel.setFavoriteUser(userEntity)
                } else {
                    homeViewModel.deleteFavoriteUser(userEntity)
                }

                val message = if (userEntity.isFavorite) "${userEntity.login} added to favorite" else "${userEntity.login} removed from favorite"

                showSnackbar(message)
            }
        })
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}