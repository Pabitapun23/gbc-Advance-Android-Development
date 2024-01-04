package com.pp.session01_f23apidemok.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pp.session01_f23apidemok.R
import com.pp.session01_f23apidemok.models.User

class UserAdapter(
    private val usersList: MutableList<User>,
    private val rowClickHandler: (Int) -> Unit,
    ) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder (itemView) {
        init {
            itemView.setOnClickListener {
                rowClickHandler(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.user_row_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        // 1. Get the current user
        val currUser:User = usersList.get(position)

        // 2. Populate the UI with the user details
        // 2a. Get the tvLine1 with user's name
        val tvLine1 = holder.itemView.findViewById<TextView>(R.id.tvLine1)
        tvLine1.text = currUser.name

        // 2b. Populate tvLine2 with user's address
        val tvLine2 = holder.itemView.findViewById<TextView>(R.id.tvLine2)
        tvLine2.text = "${currUser.address.street}, ${currUser.address.city}"

    }
}