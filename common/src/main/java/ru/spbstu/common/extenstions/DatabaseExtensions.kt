package ru.spbstu.common.extenstions

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

fun DatabaseReference.subscribe(
    onSuccess: (DataSnapshot) -> Unit,
    onCancelled: (DatabaseError) -> Unit
) {
    addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            onSuccess(snapshot)
        }

        override fun onCancelled(error: DatabaseError) {
            onCancelled(error)
        }
    })
}

fun DatabaseReference.readValue(
    onSuccess: (DataSnapshot) -> Unit,
    onCancelled: (DatabaseError) -> Unit
) {
    addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            onSuccess(snapshot)
        }

        override fun onCancelled(error: DatabaseError) {
            onCancelled(error)
        }
    })
}

