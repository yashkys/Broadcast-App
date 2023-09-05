package com.kys.broadcastapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kys.broadcastapp.R
import com.kys.broadcastapp.data.modals.dataModals.ChatRoom
import com.kys.broadcastapp.databinding.ChatroomItemBinding
import com.kys.broadcastapp.utils.FireStoreDocumentField
import javax.inject.Inject

class ChatroomAdapter @Inject constructor() :
    RecyclerView.Adapter<ChatroomAdapter.ChatroomItemBindViewHolder>()
{

    private var chatroomList = listOf<ChatRoom>()
    var onClick: (String) -> Unit = {
        Log.d("Test", "onClick List Adapter Item: CategoryName->")
        //load data
    }

    fun setAdapterList(newList: List<ChatRoom>) {
        Log.d("Test", "In Chatroom Adapter\n ==> Data in List<ChatRoom> :: $newList")
        chatroomList = newList
        notifyItemRangeChanged(0, chatroomList.size)
    }

    inner class ChatroomItemBindViewHolder(
        private val binding: ChatroomItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChatRoom, position: Int) {
            binding.apply {
                val image: String  = item.image
                Glide
                    .with(ivImage.context)
                    .load(image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_profile)
                    .into(ivImage)
                tvName.text = item.name
                tvLastMessage.text = if (item.lastMessage?.messageType == FireStoreDocumentField.MESSAGE_TYPE_TEXT) item.lastMessage!!.message else "Photo"
                llUserList.setOnClickListener {
                    onClick(item.id)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatroomItemBindViewHolder {
        val binding =
            ChatroomItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ChatroomItemBindViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return chatroomList.size
    }

    override fun onBindViewHolder(holder: ChatroomItemBindViewHolder, position: Int) {
        holder.bind(chatroomList[position], position)
    }


}
