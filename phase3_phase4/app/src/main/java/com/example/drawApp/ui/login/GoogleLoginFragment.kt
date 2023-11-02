package com.example.drawApp.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.drawApp.MainActivity
import com.example.drawApp.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth.getInstance

class GoogleLoginFragment : Fragment() {
    // Initialize Firebase Auth
    private val auth = getInstance()

    // Register for activity result
    @SuppressLint("SwitchIntDef")
    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val response = IdpResponse.fromResultIntent(result.data)

        if (result.resultCode == Activity.RESULT_OK) {
            // Successfully signed in
            val user = auth.currentUser
            showToast("Sign-in success. Hello ${user?.displayName}")
            // Proceed with the signed-in user
            view?.findViewById<Button>(R.id.login)?.visibility = View.INVISIBLE
            view?.findViewById<Button>(R.id.logout)?.visibility = View.VISIBLE
            findNavController().navigate(R.id.login_redirect)
            (activity as? MainActivity)?.setBottomNavigationVisibility(true)
        } else {
            // Sign in failed, check the error and display a message
            if (response == null) {
                // User pressed back button
                showToast("Sign-in cancelled")
                return@registerForActivityResult
            }

            when (response.error?.errorCode) {
                ErrorCodes.NO_NETWORK -> showToast("No internet connection")
                ErrorCodes.UNKNOWN_ERROR -> showToast("Unknown error occurred")
                ErrorCodes.PLAY_SERVICES_UPDATE_CANCELLED -> showToast("Please update Google Play Services")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this.requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("GoogleLoginFragment", "onCreateView called")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.google_login_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        Create login button
        view.findViewById<Button>(R.id.login).setOnClickListener {
            Log.d("LoginFragment", "Login button clicked!") // Debug log
            showToast("Trying to login!")
            // Choose authentication providers
            val providers = arrayListOf(
                AuthUI.IdpConfig.GoogleBuilder().build()
            )

            // Create and launch sign-in intent
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()
            signInLauncher.launch(signInIntent)
        }

//        Create logout button
        view.findViewById<Button>(R.id.logout).setOnClickListener {
            // Provide the context of the Fragment to the signOut method
            AuthUI.getInstance().signOut(requireContext())
                .addOnCompleteListener {
                    // Handle sign out completion
                    showToast("Signed out successfully!")
                    view.findViewById<Button>(R.id.login).visibility = View.VISIBLE
                    view.findViewById<Button>(R.id.logout).visibility = View.INVISIBLE
                    (activity as? MainActivity)?.setBottomNavigationVisibility(false)
                }
        }

        if (auth.currentUser != null) {
//             User is already signed in. Make BottomNavigationView visible
            (activity as? MainActivity)?.setBottomNavigationVisibility(true)
//            Change login to logout button
            view.findViewById<Button>(R.id.login).visibility = View.INVISIBLE
            view.findViewById<Button>(R.id.logout).visibility = View.VISIBLE
        }
    }
}
