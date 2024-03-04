package com.notsatria.githubusers.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.notsatria.githubusers.R
import com.notsatria.githubusers.adapter.UserFollowAdapter
import com.notsatria.githubusers.databinding.FollowFragmentBinding
import com.notsatria.githubusers.response.User
import com.notsatria.githubusers.viewmodel.DetailViewModel

class FollowFragment() : Fragment() {

    companion object {
        const val ARG_SECTION_NUMBER = "section_number"
        const val ARG_USERNAME = "username"
    }

    private lateinit var rvGithubUser: RecyclerView
    private lateinit var binding: FollowFragmentBinding
    private lateinit var detailViewModel: DetailViewModel

    var sectionNumber = 0
    var username: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.follow_fragment, container, false)
        binding = FollowFragmentBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvGithubUser = binding.rvUserList

        arguments?.let {
            sectionNumber = it.getInt(ARG_SECTION_NUMBER, 0)
            username = it.getString(ARG_USERNAME)
        }

        detailViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[DetailViewModel::class.java]

        detailViewModel.getFollowers(username!!, requireContext())
        detailViewModel.getFollowing(username!!, requireContext())

        detailViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Log.d("FollowFragment", "showLoading: $isLoading")
            showLoading(isLoading)
        }

        detailViewModel.isError.observe(viewLifecycleOwner) { isError ->
            Log.d("FollowFragment", "showError: $isError")
            showError(isError)
        }

        if (sectionNumber == 1) {
            detailViewModel.detailFollowerUser.observe(viewLifecycleOwner) {
                Log.d("FollowFragment", "showRecyclerList: $it")
                showRecyclerList(it)
            }
        } else {
            detailViewModel.detailFollowingUser.observe(viewLifecycleOwner) {
                showRecyclerList(it)
            }
        }
    }

    private fun showError(isError: Boolean) {
        binding.rlError.visibility = if (isError) View.VISIBLE else View.GONE
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showRecyclerList(list: List<User>) {
        Log.d("FollowFragment", "showRecyclerList: $list")
        val adapter = UserFollowAdapter(list)
        rvGithubUser.layoutManager = LinearLayoutManager(requireContext())
        rvGithubUser.adapter = adapter
        rvGithubUser.setHasFixedSize(true)
    }

}