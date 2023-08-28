package com.kys.broadcastapp.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.kys.broadcastapp.MainActivity
import com.kys.broadcastapp.data.modals.dataModals.User
import com.kys.broadcastapp.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {


    private var selectedProfileUri: Uri?=null
    private lateinit var binding: FragmentProfileBinding

    private lateinit var viewModel: ProfileViewModel
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        binding.apply {
            viewModel.getUser {
                it?.let {
                    user = it
                    profileName.setText(it.userName)
                    profileEmail.setText(it.email)
                }
            }
            ivProfilePhoto.setOnClickListener {
                openImagePicker(profilePhotoPickerResult)
            }
            btnLogout.setOnClickListener {
                viewModel.signOut()
                MainActivity.activity.finish()
                startActivity(MainActivity.activity.intent)
            }
            btnUpdateProfile.setOnClickListener {
                val dob = profileDateOfBirth.text.toString()
                val name = profileName.text.toString()
                if(!TextUtils.isEmpty(dob)){
                    user.dob = dob
                }
                if(!TextUtils.isEmpty(name)){
                    user.userName = name
                }
                Toast.makeText(requireContext(), "Updating ... ", Toast.LENGTH_SHORT).show()
                viewModel.updateUser(user, selectedProfileUri){
                    if(it)
                    Toast.makeText(requireContext(), "Profile Updated.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }

    private fun navigateToDashboardFragment() {
//        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToDashboardFragment())
    }

    private fun openImagePicker(
        result: ActivityResultLauncher<Intent>, selectMultiple: Boolean = false
    ) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (selectMultiple) intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        result.launch(intent)
    }

    private var profilePhotoPickerResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let {
                    binding.ivProfilePhoto.setImageURI(it)
                    selectedProfileUri = it
                }
            }
        }
}