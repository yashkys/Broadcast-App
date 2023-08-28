package com.kys.broadcastapp.ui.createBroadCastGroup

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.kys.broadcastapp.R
import com.kys.broadcastapp.adapter.UserAdapter
import com.kys.broadcastapp.data.modals.dataModals.User
import com.kys.broadcastapp.data.modals.responseModals.UploadResult
import com.kys.broadcastapp.databinding.FragmentCreateNewBroadcastGroupBinding
import com.kys.broadcastapp.ui.UploadViewModel
import com.kys.broadcastapp.utils.ActivityResultHandlers
import com.kys.broadcastapp.utils.CurrentUserIDProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateNewBroadcastGroupFragment : Fragment() {

    private lateinit var binding: FragmentCreateNewBroadcastGroupBinding

    private lateinit var viewModel: CreateNewBroadcastGroupViewModel
    private lateinit var uploadViewModel: UploadViewModel

    private lateinit var activityResultHandlers: ActivityResultHandlers

    @Inject
    lateinit var userAdapter: UserAdapter

    private var userList = arrayListOf<User>()
    private var selectedUserList = arrayListOf<User>()

    private lateinit var currentUserID : String
    private lateinit var currentUserData : User
    private var downloadUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[CreateNewBroadcastGroupViewModel::class.java]
        uploadViewModel = ViewModelProvider(this)[UploadViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateNewBroadcastGroupBinding.inflate(layoutInflater, container, false)
        currentUserID = CurrentUserIDProvider.getCurrentUserId()
        viewModel.userList.observe(viewLifecycleOwner, Observer { users ->
            userAdapter.setAdapterList(users)
            userList.clear()
            userList.addAll(users)
        })
        viewModel.getUserList(currentUserID)
        currentUserID = CurrentUserIDProvider.getCurrentUserId()
        activityResultHandlers = ActivityResultHandlers(this, uploadViewModel)

        viewModel.getCurrentUserData(currentUserID){
            it?.let { currUser->
                currentUserData = currUser
                selectedUserList.add(currUser)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            rvSelectedUserList.adapter = userAdapter
            rvUserFriendList.adapter = userAdapter

            btnAddDetails.setOnClickListener {
                if(selectedUserList.size>2){
                    layoutCreateBroadcastGroupWithData.visibility = View.VISIBLE
                    layoutSelectUsersFromIst.visibility = View.GONE
                    userAdapter.setAdapterList(selectedUserList)
                }else {
                    Toast.makeText(requireContext(),
                        getString(R.string.please_select_more_than_2_people), Toast.LENGTH_SHORT).show()
                }
            }

            ivGroupImage.setOnClickListener {
                if (ActivityCompat.checkSelfPermission(
                        ivGroupImage.context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val intentImage = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    activityResultHandlers.pickImageLauncher.launch(intentImage)
                } else {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        1
                    )
                }
                hideSoftKeyboard()
            }

            uploadViewModel.uploadStatus.observe(viewLifecycleOwner) { status ->
                when (status) {
                    is UploadResult.InProgress -> {
                        // Handle progress update
                        Log.d("Test", "File Upload inProgress ${status.progress}" )
                        llProgress.visibility = View.VISIBLE
                        llProgress.progress = status.progress
                    }

                    is UploadResult.Success -> {
                        Log.d("Test", "File Upload Successful")
                        llProgress.visibility = View.GONE
                        downloadUrl = status.url
                        // Handle upload success and downloadUrl
                        Glide
                            .with(ivGroupImage.context)
                            .load(downloadUrl)
                            .centerCrop()
                            .placeholder(R.drawable.ic_profile)
                            .into(ivGroupImage)

                    }
                    is UploadResult.Failure ->{
                        Log.d("Test", "file Upload Failure ${status.error}")
                        llProgress.visibility = View.GONE

                    }
                    is UploadResult.Cancelled ->{
                        Log.d("Test", "file Upload Cancelled")
                        llProgress.visibility = View.GONE

                    }
                    // Handle other status cases
                    else -> {
                        Log.d("Test", "Hide Progress")
                        llProgress.visibility = View.GONE
                    }
                }
            }

            uploadViewModel.uploadError.observe(viewLifecycleOwner) { error ->
                Log.d("Test", "File Upload Error \n$error" )
                llProgress.visibility = View.GONE
                // Handle upload error
            }

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
                    Log.e("Test", "Query Search Term = $query")

                    if (query.isNotEmpty()) {
                        // Show the chatroom List Based on Searched Query
                        performSearch(query)
                    } else {
                        // Restore the Default Chatroom List
                        userAdapter.setAdapterList(userList)
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            userAdapter.onClick = { it->
                if (it.second ) selectedUserList.add(it.first)
                else selectedUserList.remove(it.first)
            }

            btnCreateNewBroadcastGroup.setOnClickListener {
                val groupName = etGroupName.text.toString().trim()
                if(TextUtils.isEmpty(groupName)){
                    etGroupName.error = getString(R.string.enter_the_group_name)
                }else {
                    viewModel.createBroadcastGroup(groupName, downloadUrl, selectedUserList){
                        if(it){
                            Toast.makeText(requireContext(), "Created broadcast channel", Toast.LENGTH_SHORT).show()
                            Log.d("Test", "Created Broadcast Channel : $groupName")
                            requireActivity().supportFragmentManager.popBackStack()
                        }else{
                            Toast.makeText(requireContext(), "Failed to create broadcast channel, try again later", Toast.LENGTH_SHORT).show()
                            Log.d("Test", "Failed to create Broadcast Channel : $groupName")
                        }
                    }
                    // onCreate perform navigation
                }
            }
        }
    }

    private fun performSearch(query: String) {
        val filteredSearchUser = userList.filter { user ->
            user.userName.contains(query, ignoreCase = true)
        }

        userAdapter.setAdapterList(filteredSearchUser)
    }

    private fun hideSoftKeyboard() {
        val inputMethodManager = ContextCompat.getSystemService(
            requireContext(),
            InputMethodManager::class.java
        )
        inputMethodManager?.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

}