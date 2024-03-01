package com.notsatria.githubusers.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.notsatria.githubusers.R
import com.notsatria.githubusers.data.User


class GithubUserItemAdapter(private var list: List<User>) : RecyclerView.Adapter<GithubUserItemAdapter.ViewHolder>() {

    private val filteredList = mutableListOf<User>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.github_user_item, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivAvatar: ImageView = itemView.findViewById(R.id.ivAvatar)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (filteredList.isNotEmpty()) {
            val user = filteredList[position]
            holder.tvName.text = user.name
            holder.tvUsername.text = user.username
            return
        }
        val user = list[position]
//        holder.ivAvatar.setImageResource(user.avatar)
        holder.tvName.text = user.name
        holder.tvUsername.text = user.username
    }

    override fun getItemCount(): Int = if (filteredList.isNotEmpty()) filteredList.size else list.size

    fun filterList(query: String) {
       filteredList.clear()
        for (user in list) {
            if (user.username.contains(query, ignoreCase = true)) {
                filteredList.add(user)
                Log.d("GithubUserItemAdapter", "filterList: ${user.username}")
            }
        }
        notifyDataSetChanged()
    }
}