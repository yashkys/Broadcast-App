package com.kys.broadcastapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kys.broadcastapp.R
import com.kys.broadcastapp.data.modals.dataModals.User
import com.kys.broadcastapp.databinding.SelectedUserItemBinding
import com.kys.broadcastapp.databinding.UserItemBinding
import com.kys.broadcastapp.utils.CurrentUserIDProvider
import com.kys.broadcastapp.utils.onClick
import javax.inject.Inject

class SelectedUserAdapter @Inject constructor() :
RecyclerView.Adapter<SelectedUserAdapter.SelectedUserItemBindViewHolder>()
{

    private var userList = arrayListOf<User>()

    private var isSelected = false

    var onClick: (User?) -> Unit = {
        Log.d("Test", "onClick List Adapter Item: User->")
        //load data
    }

    fun setAdapterList(newList: ArrayList<User>) {
        Log.d("Test", "In Selected User Adapter\n ==> Data in List<User> :: $newList")
        userList = newList
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

    inner class SelectedUserItemBindViewHolder(
        val binding: SelectedUserItemBinding
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
                if(item.userID == CurrentUserIDProvider.getCurrentUserId()){
                    btnClose.visibility = View.GONE
                }
                llUserItemContainer.onClick {
                    if(item.userID == CurrentUserIDProvider.getCurrentUserId()){
                        onClick(null)
                    }else {
                        isSelected = true
//                        userList.remove(item)
                    userList.removeAt(position)
                        notifyItemRemoved(position)
//                        updateAdapterList(item,false)
                        onClick(item)
                    }
                }
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedUserItemBindViewHolder {
        val binding =
            SelectedUserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return SelectedUserItemBindViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: SelectedUserItemBindViewHolder, position: Int) {
        holder.bind(userList[position], position)
    }


}
