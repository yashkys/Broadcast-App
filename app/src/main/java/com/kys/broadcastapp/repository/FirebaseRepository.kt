package com.kys.broadcastapp.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.kys.broadcastapp.api.getDocument
import com.kys.broadcastapp.api.getDocumentList
import com.kys.broadcastapp.api.saveDocument
import com.kys.broadcastapp.api.uploadByteArrayWithCallbacks
import com.kys.broadcastapp.api.uploadFile
import com.kys.broadcastapp.api.uploadFileWithCallbacks
import com.kys.broadcastapp.data.modals.dataModals.ChatRoom
import com.kys.broadcastapp.data.modals.dataModals.Message
import com.kys.broadcastapp.data.modals.dataModals.User
import com.kys.broadcastapp.utils.FireStorageCollection
import com.kys.broadcastapp.utils.FireStoreCollection
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FirebaseRepository @Inject constructor(
    private val database: FirebaseFirestore,
    private val storageReference: StorageReference,
    private val fAuth: FirebaseAuth
) {

    private val userCollection = database.collection(FireStoreCollection.USER)
    private val friendUserCollection = database.collection(FireStoreCollection.FRIEND_USER)
    private val chatroomCollection = database.collection(FireStoreCollection.CHATROOM)
    private val chatroomSubscribedCollection =
        database.collection(FireStoreCollection.CHATROOM_SUBSCRIBED)
    private val messageCollection = database.collection(FireStoreCollection.MESSAGE)
    private val broadcastChannelCollection =
        database.collection(FireStoreCollection.BROADCAST_CHANNEL)
    private val chatroomMessageCollection =
        database.collection(FireStoreCollection.CHATROOM_MESSAGES)

    private val imageStorage = storageReference.child(FireStorageCollection.IMAGE)
    private val videoStorage = storageReference.child(FireStorageCollection.VIDEO)
    private val fileStorage = storageReference.child(FireStorageCollection.FILE)

    fun saveUser(documentId: String, data: User, onComplete: (Boolean) -> Unit) =
        userCollection.saveDocument(documentId, data, onComplete)


    fun getUser(documentId: String, onComplete: (User?) -> Unit) =
        userCollection.getDocument<User>(documentId, onComplete)

    fun saveImage(file: Uri) = imageStorage.uploadFile(file)
    fun saveFile(file: Uri) = videoStorage.uploadFile(file)

    fun saveImageWithCallbacks(
        file: Uri,
        onProgress: (Int) -> Unit,
        onComplete: (String) -> Unit,
        onFailure: (String) -> Unit,
        onCancel: () -> Unit
    ) = imageStorage.uploadFileWithCallbacks(
        file,
        onProgress,
        onComplete,
        onFailure,
        onCancel
    )

    fun saveByteArrayWithCallbacks(
        file: ByteArrayOutputStream,
        onProgress: (Int) -> Unit,
        onComplete: (String) -> Unit,
        onFailure: (String) -> Unit,
        onCancel: () -> Unit
    ) = imageStorage.uploadByteArrayWithCallbacks(
        file,
        onProgress,
        onComplete,
        onFailure,
        onCancel
    )


    fun saveFile(
        file: Uri,
        onProgress: (Int) -> Unit,
        onComplete: (String) -> Unit,
        onFailure: (String) -> Unit,
        onCancel: () -> Unit
    ) = videoStorage.uploadFile(file)

    fun saveProfileImage(file: Uri, onComplete: (String) -> Unit) = imageStorage.uploadFile(file) {
        onComplete(it)
    }

    fun getCurrentUserID() = fAuth.currentUser?.uid.toString()


//    fun uploadAppData(appData: AppData, onComplete: () -> Unit) {
//        appData.developer = fAuth.currentUser!!.uid
//        approvalAppCollection
//            .add(appData)
//            .addOnSuccessListener {
//                Log.d("Test", "AppData uploaded successfully")
//                onComplete()
//            }
//            .addOnFailureListener { exception ->
//                Log.d("Test", "Failed to upload AppData: $exception")
//            }
//    }

    fun uploadImage(
        imagePath: Uri,
        imageName: String,
        onComplete: (String) -> Unit
    ) {
        val imageRef =
            storageReference.child("application/images/$imageName -- ${UUID.randomUUID()}")
        val uploadTask = imageRef.putFile(imagePath)

        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    onComplete(uri.toString())
                }.addOnFailureListener { exception ->
                    Log.d("Test", " Failed to upload image \nException: $exception")
                }
            } else {
                Log.d("Test", " Failed to upload image.")
            }
        }
    }

    fun uploadFile(
        fileUri: Uri,
        onProgress: (Int) -> Unit,
        onComplete: (String) -> Unit,
        onFailure: (String) -> Unit,
        onCancel: () -> Unit
    ) = fileStorage.uploadFileWithCallbacks(
        fileUri,
        onProgress,
        onComplete,
        onFailure,
        onCancel
    )


    fun fetchChatroomData(chatroomID: String, onComplete: (ChatRoom?) -> Unit) =
        chatroomCollection.getDocument<ChatRoom>(chatroomID, onComplete)

    fun fetchBroadcastChannelIDList(currentUserId: String, onComplete: (List<String>?) -> Unit) =
        broadcastChannelCollection.getDocument<List<String>>(currentUserId, onComplete)

    fun getBroadcastChannelIDList(onComplete: (List<List<String>>?) -> Unit) =
        broadcastChannelCollection.getDocumentList(onComplete)

    fun getChatroomIDList(onComplete: (List<List<ChatRoom>>?) -> Unit) =
        chatroomCollection.getDocumentList(onComplete)

    fun getChatroomSubscribedList(currentUserID: String, onComplete: (List<String>?) -> Unit) =
        chatroomSubscribedCollection.getDocument<List<String>>(currentUserID, onComplete)

    fun sendMessageInChatroom(chatroomId: String, messageID: String) {
        chatroomCollection.saveDocument(chatroomId, messageID) {}
    }
    fun saveMessage(message: Message) {
        messageCollection.saveDocument(message.messageID, message) {}
    }

    fun sendMessageInMessageCollection(message: Message) =
        messageCollection.saveDocument(message.messageID, message) {}

    fun sendMessageInMessageCollection(message: Message, onComplete: (Boolean) -> Unit) =
        messageCollection.saveDocument(message.messageID, message,onComplete)

    fun fetchMessageData(messageID: String, onComplete: (Message?) -> Unit) =
        messageCollection.getDocument<Message>(messageID, onComplete)

    fun fetchMessageIDList(chatroomId: String, onComplete: (List<String>?) -> Unit) =
        chatroomMessageCollection.getDocument<List<String>>(chatroomId, onComplete)

    fun fetchUserData(userID: String, onComplete: (User?) -> Unit) =
        userCollection.getDocument<User>(userID, onComplete)

    fun fetchFriendUserIDList(userID: String, onComplete: (List<String>?) -> Unit) =
        friendUserCollection.getDocument<List<String>?>(userID, onComplete)

    fun getChatroomRoomID(currentUserID: String, userId: String, onComplete: (String) -> Unit) {
        getChatroomSubscribedList(userId) {
            it?.let { it1 ->
                for (id in it1) {
                    if (id.contains(currentUserID) && id.contains(userId)) {
                        onComplete(id)
                    }
                }
            }
        }
    } /* chatroom id for 1v1 message is of form :-  userID1_userID2 */

    fun createChatroom(chatRoom: ChatRoom, onComplete: (Boolean) -> Unit) =
        chatroomCollection.saveDocument(chatRoom.id,chatRoom, onComplete)

//    fun updateAppData(appData: AppData, onComplete: (Boolean) -> Unit) {
//        appCollection.getDocumentID(FireStoreDocumentField.DOWNLOAD_URL, appData.download_url) {
//            it?.let { documentId ->
//                appCollection.saveDocument(documentId, appData) {it1->
//                    onComplete(it1)
//                }
//            }
//        }
//    }

}
