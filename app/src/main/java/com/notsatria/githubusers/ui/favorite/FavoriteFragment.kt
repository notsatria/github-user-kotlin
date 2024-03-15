package com.notsatria.githubusers.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.notsatria.githubusers.adapter.GithubUserItemAdapter
import com.notsatria.githubusers.data.local.entity.UserEntity
import com.notsatria.githubusers.databinding.FragmentFavoriteBinding
import com.notsatria.githubusers.ui.detail.DetailActivity
import com.notsatria.githubusers.ui.detail.DetailViewModel

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private lateinit var rvGithubUser: RecyclerView
    private lateinit var clFavorite: View

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val toolbar = binding.toolbar
        toolbar.title = "Favorite User"
        rvGithubUser = binding.rvFavoriteUser
        clFavorite = binding.clFavorite

        val factory: FavoriteViewModelFactory = FavoriteViewModelFactory.getInstance(requireContext())
        val favoriteViewModel: FavoriteViewModel by viewModels { factory }

        favoriteViewModel.getFavoriteUsers().observe(viewLifecycleOwner) { userList ->
            if (userList.isEmpty()) {
                binding.tvError.text = "No favorite user found"
                binding.rlError.visibility = View.VISIBLE
                rvGithubUser.visibility = View.GONE
            } else {
                binding.rlError.visibility = View.GONE
                rvGithubUser.visibility = View.VISIBLE
                showRecyclerList(userList, favoriteViewModel)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showRecyclerList(userList: List<UserEntity>, favoriteViewModel: FavoriteViewModel) {
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

                if (userEntity.isFavorite) {
                    favoriteViewModel.setFavoriteUser(userEntity)
                } else {
                    favoriteViewModel.deleteFavoriteUser(userEntity)
                }

                val message = if (userEntity.isFavorite) "${userEntity.login} added to favorite" else "${userEntity.login} removed from favorite"

                val snackbar = Snackbar.make(clFavorite, message, Snackbar.LENGTH_SHORT)

                snackbar.show()
            }
        })
    }

}