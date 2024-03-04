package com.notsatria.githubusers.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.notsatria.githubusers.R
import com.notsatria.githubusers.response.User


class GithubUserItemAdapter(private var list: List<User>) : RecyclerView.Adapter<GithubUserItemAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.github_user_item, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivAvatar: ImageView = itemView.findViewById(R.id.ivAvatar)
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = list[position]
        holder.tvUsername.text = user.login
        Glide.with(holder.itemView.context)
            .load(user.avatarUrl)
            .into(holder.ivAvatar)

        holder.itemView.setOnClickListener {
            Log.d("GithubUserItemAdapter", "onBindViewHolder: ${list[position].login}")
            onItemClickCallback.onItemClicked(list[position].login.toString())
        }

    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: String)
    }

    override fun getItemCount(): Int = list.size

}