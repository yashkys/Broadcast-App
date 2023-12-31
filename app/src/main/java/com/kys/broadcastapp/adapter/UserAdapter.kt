package com.kys.broadcastapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kys.broadcastapp.R
import com.kys.broadcastapp.data.modals.dataModals.User
import com.kys.broadcastapp.databinding.UserItemBinding
import com.kys.broadcastapp.utils.onClick
import okhttp3.internal.notify
import javax.inject.Inject

class UserAdapter @Inject constructor() :
RecyclerView.Adapter<UserAdapter.UserItemBindViewHolder>()
{

    private var userList = arrayListOf<User>()

    private var isSelected = false

    var onClick: (User) -> Unit = {
        Log.d("Test", "onClick List Adapter Item: User->")
        //load data
    }

    fun setAdapterList(newList: List<User>) {
        Log.d("Test", "In User Adapter\n ==> Data in List<User> :: $newList")
        userList = newList as ArrayList<User>
        notifyItemRangeChanged(0, userList.size)
    }

    fun updateAdapterList(user: User, isAddOrRemove : Boolean) {
        if(isAddOrRemove){
            userList.add(user)
        }else {
            userList.remove(user)
        }
        notifyItemRangeChanged(0, userList.size)
    }

    inner class UserItemBindViewHolder(
        val binding: UserItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: User, position: Int) {
            binding.apply {
                val image  = item.image
                image?.let {
                    Glide
                        .with(ivImage.context)
                        .load(image)
                        .centerCrop()
                        .placeholder(R.drawable.ic_profile)
                        .into(ivImage)
                }
                tvName.text = item.userName

                llUserItemContainer.onClick {
//                    userList.remove(item)
                    userList.removeAt(position)
//                    updateAdapterList(item,false)
                    notifyItemRemoved(position)
                    onClick(item)
                }
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserItemBindViewHolder {
        val binding =
            UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return UserItemBindViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserItemBindViewHolder, position: Int) {
        holder.bind(userList[position], position)
    }


}
