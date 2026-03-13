package com.example.healthgenieai.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.healthgenieai.LoginActivity
import com.example.healthgenieai.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private lateinit var profileImage: ImageView

    private var imageUri: Uri? = null
    private val PICK_IMAGE = 101

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        profileImage = view.findViewById(R.id.profileImage)

        val name = view.findViewById<TextView>(R.id.profileName)
        val email = view.findViewById<TextView>(R.id.profileEmail)

        val height = view.findViewById<EditText>(R.id.etHeight)
        val weight = view.findViewById<EditText>(R.id.etWeight)

        val btnUpdate = view.findViewById<Button>(R.id.btnUpdate)
        val btnPhoto = view.findViewById<Button>(R.id.btnChangePhoto)
        val btnPassword = view.findViewById<Button>(R.id.btnChangePassword)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        val user = auth.currentUser

        name.text = user?.displayName ?: "User"
        email.text = user?.email ?: ""

        loadUserData(height, weight)
        loadProfileImage()

        // ---------------- CHANGE PHOTO ----------------

        btnPhoto.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"

            startActivityForResult(intent, PICK_IMAGE)
        }

        // ---------------- UPDATE PROFILE ----------------

        btnUpdate.setOnClickListener {

            val h = height.text.toString()
            val w = weight.text.toString()

            val map = hashMapOf(
                "height" to h,
                "weight" to w
            )

            db.collection("users")
                .document(auth.uid!!)
                .update(map as Map<String, Any>)
                .addOnSuccessListener {

                    Toast.makeText(
                        requireContext(),
                        "Profile Updated",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            val prefs = requireContext()
                .getSharedPreferences("user_profile", Context.MODE_PRIVATE)

            prefs.edit()
                .putFloat("height", h.toFloat())
                .putFloat("weight", w.toFloat())
                .apply()
        }

        // ---------------- CHANGE PASSWORD ----------------

        btnPassword.setOnClickListener {
            showPasswordDialog()
        }

        // ---------------- LOGOUT ----------------

        btnLogout.setOnClickListener {

            auth.signOut()

            startActivity(
                Intent(requireContext(), LoginActivity::class.java)
            )

            requireActivity().finish()
        }


        return view
    }

    // ---------------- LOAD USER DATA ----------------

    private fun loadUserData(height: EditText, weight: EditText) {

        val userId = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener {

                height.setText(it.getString("height"))
                weight.setText(it.getString("weight"))
            }
    }

    // ---------------- LOAD PROFILE IMAGE ----------------

    private fun loadProfileImage() {

        val ref = storage.reference.child("profile_images/${auth.uid}.jpg")

        ref.downloadUrl.addOnSuccessListener { uri ->

            Glide.with(requireContext())
                .load(uri)
                .circleCrop()
                .into(profileImage)
        }
    }

    // ---------------- IMAGE PICK RESULT ----------------

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {

            imageUri = data?.data

            profileImage.setImageURI(imageUri)

            uploadImage()
        }
    }

    // ---------------- UPLOAD IMAGE ----------------

    private fun uploadImage() {

        val ref = storage.reference.child("profile_images/${auth.uid}.jpg")

        imageUri?.let {

            ref.putFile(it)
                .addOnSuccessListener {

                    Toast.makeText(
                        requireContext(),
                        "Image Updated",
                        Toast.LENGTH_SHORT
                    ).show()

                    loadProfileImage()
                }
        }
    }

    // ---------------- PASSWORD DIALOG ----------------

    private fun showPasswordDialog() {

        val dialogView = layoutInflater.inflate(
            R.layout.dialog_change_password,
            null
        )

        val etCurrent = dialogView.findViewById<EditText>(R.id.etCurrentPassword)
        val etNew = dialogView.findViewById<EditText>(R.id.etNewPassword)
        val etConfirm = dialogView.findViewById<EditText>(R.id.etConfirmPassword)

        val btnUpdate = dialogView.findViewById<Button>(R.id.btnUpdatePassword)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnUpdate.setOnClickListener {

            val current = etCurrent.text.toString()
            val newPass = etNew.text.toString()
            val confirm = etConfirm.text.toString()

            if (newPass.length < 6) {
                etNew.error = "Minimum 6 characters"
                return@setOnClickListener
            }

            if (newPass != confirm) {
                etConfirm.error = "Passwords do not match"
                return@setOnClickListener
            }

            val user = auth.currentUser
            val email = user?.email ?: return@setOnClickListener

            val credential =
                EmailAuthProvider.getCredential(email, current)

            user.reauthenticate(credential)
                .addOnSuccessListener {

                    user.updatePassword(newPass)
                        .addOnSuccessListener {

                            Toast.makeText(
                                requireContext(),
                                "Password Updated",
                                Toast.LENGTH_SHORT
                            ).show()

                            dialog.dismiss()
                        }
                }
                .addOnFailureListener {

                    Toast.makeText(
                        requireContext(),
                        "Current password incorrect",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }

        dialog.show()
    }
}