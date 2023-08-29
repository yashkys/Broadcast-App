package com.kys.broadcastapp.api

import android.content.ContentValues.TAG
import android.util.Log
import com.kys.broadcastapp.utils.FireStoreDocumentField
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Query

inline fun <reified T> CollectionReference.getDocument(
    documentId: String, crossinline onComplete: (T?) -> Unit
) {
    this.document(documentId).get().addOnSuccessListener { documentSnapshot ->
        val data = documentSnapshot.toObject(T::class.java)
        Log.d("Test", " Fetched Document for : \nCollection : ${this.id} \ndocID: $documentId \n is: $data")
        onComplete(data)
    }.addOnFailureListener { e ->
        Log.e(TAG, "Error getting document: $e")
        onComplete(null)
    }
}

inline fun <reified T> CollectionReference.getDocumentList(crossinline onComplete: (List<T>?) -> Unit) {
    val list = mutableListOf<T>()
    this.get().addOnSuccessListener { documentSnapshot ->
        for (data in documentSnapshot) {
            list.add(data.toObject(T::class.java))
        }
        onComplete(list)
    }.addOnFailureListener { e ->
        Log.e(TAG, "Error getting document: $e")
        onComplete(null)
    }
}

inline fun <reified T> Query.getDocumentList(crossinline onComplete: (List<T>?) -> Unit) {
    val list = mutableListOf<T>()
    this.get().addOnSuccessListener { documentSnapshot ->
        for (data in documentSnapshot) {
            list.add(data.toObject(T::class.java))
        }
        onComplete(list)
    }.addOnFailureListener { e ->
        Log.e(TAG, "Error getting document: $e")
        onComplete(null)
    }
}

inline fun <reified T> CollectionReference.findDocument(
    filter: Filter, // e.g Filter.equalTo(field, value)
    crossinline onComplete: (T?) -> Unit
) {
    this.where(filter).get().addOnSuccessListener { querySnapshot ->
        if (!querySnapshot.isEmpty) {
            val documentSnapshot = querySnapshot.documents[0]
            val data = documentSnapshot.toObject(T::class.java)
            onComplete(data)
        } else {
            onComplete(null)
        }
    }.addOnFailureListener { e ->
        Log.e(TAG, "Error finding document: $e")
        onComplete(null)
    }
}

fun <T : Any> CollectionReference.saveDocument(
    documentId: String, data: T, onComplete: (Boolean) -> Unit
) {
    this.document(documentId).set(data).addOnSuccessListener {
        onComplete(true)
    }.addOnFailureListener { e ->
        Log.d(TAG, "saveDocument: failure: $e")
        onComplete(false)
    }
}

fun CollectionReference.getDocumentID (
    dataQuery: String,
    searchQuery: String,
    onComplete: (String?) -> Unit
){
   this.whereEqualTo(dataQuery, searchQuery).get()
        .addOnSuccessListener { querySnapshot ->
            if(!querySnapshot.isEmpty){
                val id = querySnapshot.documents[0].id
                onComplete(id)
            } else onComplete(null)

        }
        .addOnFailureListener { e ->
            Log.d("Test", "Exception occurred while getting Document ID")
        }
}
