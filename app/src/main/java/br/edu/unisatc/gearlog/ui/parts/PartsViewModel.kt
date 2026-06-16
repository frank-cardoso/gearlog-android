package br.edu.unisatc.gearlog.ui.parts

import android.util.Log
import androidx.lifecycle.ViewModel
import br.edu.unisatc.gearlog.model.Part
import br.edu.unisatc.gearlog.model.PartStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class PartsViewModel : ViewModel() {

    private val _partsList = MutableStateFlow<List<Part>>(emptyList())
    val partsList: StateFlow<List<Part>> = _partsList.asStateFlow()

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var snapshotListener: ListenerRegistration? = null

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                fetchParts(currentUser.uid)
            } else {
                _partsList.value = emptyList()
                snapshotListener?.remove()
            }
        }
    }

    private fun fetchParts(userId: String) {
        snapshotListener?.remove()

        snapshotListener = db.collection("users").document(userId).collection("parts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("PartsViewModel", "Erro ao buscar peças.", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val parts = snapshot.documents.mapNotNull { doc ->
                        try {
                            val id = doc.getString("id") ?: ""
                            val name = doc.getString("name") ?: ""
                            val brand = doc.getString("brand") ?: ""
                            val price = doc.getDouble("price") ?: 0.0
                            val quantity = doc.getLong("quantity")?.toInt() ?: 1
                            val statusString = doc.getString("status") ?: "INVENTORY"
                            val status = PartStatus.valueOf(statusString)
                            val photoUrl = doc.getString("photoUrl") ?: ""

                            Part(id, name, brand, price, quantity, status, photoUrl)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    _partsList.value = parts
                }
            }
    }

    fun addPart(name: String, brand: String, price: Double, status: PartStatus, photoUrl: String) {
        val userId = auth.currentUser?.uid ?: return
        val newPartId = UUID.randomUUID().toString()

        val partData = hashMapOf(
            "id" to newPartId,
            "name" to name,
            "brand" to brand,
            "price" to price,
            "quantity" to 1,
            "status" to status.name,
            "photoUrl" to photoUrl
        )

        db.collection("users").document(userId).collection("parts").document(newPartId).set(partData)
    }

    fun deletePart(partId: String) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("parts").document(partId).delete()
    }

    fun updatePart(partId: String, name: String, brand: String, price: Double, status: PartStatus, photoUrl: String) {
        val userId = auth.currentUser?.uid ?: return
        val partData = hashMapOf(
            "id" to partId,
            "name" to name,
            "brand" to brand,
            "price" to price,
            "quantity" to 1,
            "status" to status.name,
            "photoUrl" to photoUrl
        )
        db.collection("users").document(userId).collection("parts").document(partId).set(partData)
    }

    fun getPartById(partId: String): Part? {
        return _partsList.value.find { it.id == partId }
    }
}