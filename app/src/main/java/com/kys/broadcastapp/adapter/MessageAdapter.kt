package com.kys.broadcastapp.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kys.broadcastapp.R
import com.kys.broadcastapp.data.modals.dataModals.Message
import com.kys.broadcastapp.databinding.MessageLayoutBinding
import com.kys.broadcastapp.utils.CurrentUserIDProvider
import com.kys.broadcastapp.utils.FireStoreDocumentField
import javax.inject.Inject

class MessageAdapter @Inject constructor() :
    RecyclerView.Adapter<MessageAdapter.MessageItemBindViewHolder>()
{

    private var messageList = listOf<Message>()
    var onClick: (String) -> Unit = {
        Log.d("Test", "onClick List Adapter Item: Message->")
        //load data
    }

    fun setAdapterList(newList: List<Message>) {
        Log.d("Test", "In Message Adapter\n ==> Data in List<Message> :: $newList")
        messageList = newList
        notifyItemRangeChanged(0, messageList.size)
    }

    inner class MessageItemBindViewHolder(
        val binding: MessageLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Message, position: Int) {
            binding.apply {
                // if message is clicked
                layoutMessage.setOnClickListener {
                    onClick(item.messageID)
                }

                // if current user send message
                if(item.senderID == CurrentUserIDProvider.getCurrentUserId()){
                    llReceive.visibility = View.GONE
                    if(item.messageType == FireStoreDocumentField.MESSAGE_TYPE_TEXT){
                        tvSentMessage.text = item.message
                        ivSent.visibility = View.GONE
                    }else{
                        tvSentMessage.visibility = View.GONE
                        val image: String  = item.message
                        Glide
                            .with(ivSent.context)
                            .load(image)
                            .centerCrop()
                            .placeholder(R.drawable.ic_profile)
                            .into(ivSent)
                    }
                    tvSentMessageTime.text = item.messageTime
                    if(item.isMessageRead){
                        val unwrappedDrawable = AppCompatResources.getDrawable(messageTick.context, R.drawable.message_send_tick)
                        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
                        DrawableCompat.setTint(wrappedDrawable, Color.BLUE)
                    }
                }
                else{   // if current user receives message
                    llSend.visibility = View.GONE
                    if(item.messageType == FireStoreDocumentField.MESSAGE_TYPE_TEXT){
                        tvReceivedMessage.text = item.message
                        ivReceived.visibility = View.GONE
                    }else{
                        tvReceivedMessage.visibility = View.GONE
                        val image: String  = item.message
                        Glide
                            .with(ivReceived.context)
                            .load(image)
                            .centerCrop()
                            .placeholder(R.drawable.ic_profile)
                            .into(ivReceived)
                    }
                    tvReceivedMessageTime.text = item.messageTime
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageItemBindViewHolder {
        val binding =
            MessageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MessageItemBindViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: MessageItemBindViewHolder, position: Int) {
        holder.bind(messageList[position], position)
    }


}
