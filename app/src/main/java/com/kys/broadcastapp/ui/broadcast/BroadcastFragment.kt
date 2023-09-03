package com.kys.broadcastapp.ui.broadcast

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.kys.broadcastapp.adapter.ChatroomAdapter
import com.kys.broadcastapp.data.modals.dataModals.ChatRoom
import com.kys.broadcastapp.databinding.FragmentBroadcastBinding
import com.kys.broadcastapp.utils.CurrentUserIDProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BroadcastFragment : Fragment() {

    private lateinit var binding: FragmentBroadcastBinding

    @Inject
    lateinit var chatroomAdapter: ChatroomAdapter

    private var chatroomList = arrayListOf<ChatRoom>()

    private lateinit var viewModel: BroadcastViewModel
    private lateinit var currentUserID: String


    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[BroadcastViewModel::class.java]
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBroadcastBinding.inflate(layoutInflater, container, false)


        currentUserID = CurrentUserIDProvider.getCurrentUserId()
        //viewModel.getChatroomIdList(fetchCurrentUserID()) /* from Api */

        viewModel.chatroomList.observe(viewLifecycleOwner, Observer { chatrooms ->
            chatroomAdapter.setAdapterList(chatrooms)
            chatroomList.clear()
            chatroomList.addAll(chatrooms)
        })
        viewModel.getChatroomList(currentUserID)
//            viewModel.getChatroomList(fetchCurrentUserID()) /* from Api */

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            rvChatroomList.adapter = chatroomAdapter

            /* if create new Chatroom  button clicked */
            btnCreateNewBroadcastGroup.setOnClickListener {
                Log.d("Test", "Btn createNewBroadcastGroup clicked")
                navigateToCreateNewBroadcastGroupFragment()
            }

            /* if chatroom is clicked */
            chatroomAdapter.onClick = { navigateToChatroomFragment(it) }

            /* if user is searched */
            etSearchUser.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val query = s.toString().trim()
                    Log.d("Test", "Query Search Term = $query")

                    if (query.isNotEmpty()) {
                        // Show the chatroom List Based on Searched Query
                        performSearch(query)
                    } else {
                        // Restore the Default Chatroom List
                        chatroomAdapter.setAdapterList(chatroomList)
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

        }

    }

    private fun performSearch(query: String) {
        val filteredChatroomSearched = chatroomList.filter { chatroom ->
            chatroom.name.contains(query, ignoreCase = true)
        }

        chatroomAdapter.setAdapterList(filteredChatroomSearched)
    }

    private fun navigateToCreateNewBroadcastGroupFragment() {
        findNavController().navigate(BroadcastFragmentDirections.actionBroadcastFragmentToCreateNewBroadcastGroupFragment())
    }

    private lateinit var chatroomData: ChatRoom
    private fun navigateToChatroomFragment(chatroomId: String) {
        viewModel.chatroomData.observe(viewLifecycleOwner) { data ->
            chatroomData = data
            Log.d("Test", "Opened Chatroom -> ChatroomData for id $chatroomId :  \n$chatroomData")
            findNavController().navigate(
                BroadcastFragmentDirections.actionBroadcastFragmentToChatroomFragment(
                    chatroomData
                )
            )
        }
        viewModel.getChatroomData(chatroomId)
//        {
//            if (it) {
//                Log.e("Test", "Failed to Fetch Chatroom Data with id : $chatroomId")
//            } else {
//                Log.e("Test", "Failed to Fetch Chatroom Data with id : $chatroomId")
//            }
//        }
    }

}