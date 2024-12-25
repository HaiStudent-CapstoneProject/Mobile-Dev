package com.dicoding.picodiploma.loginwithanimation.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import com.dicoding.picodiploma.loginwithanimation.view.main.MainViewModel

class ProfileFragment : Fragment() {
    private lateinit var viewModel: MainViewModel
    private lateinit var emailTextView: TextView
    private lateinit var userNameTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        )[MainViewModel::class.java]

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize views
        emailTextView = view.findViewById(R.id.emailTextView)
        userNameTextView = view.findViewById(R.id.userNameTextView)
        val logoutButton: Button = view.findViewById(R.id.logoutButton)

        // Setup Logout Button
        logoutButton.setOnClickListener {
            viewModel.logout()
            startActivity(Intent(requireContext(), WelcomeActivity::class.java))
            requireActivity().finish()
        }

        // Observe user session
        viewModel.getSession().observe(viewLifecycleOwner) { userModel ->
            emailTextView.text = userModel.email
            userNameTextView.text = userModel.email?.substringBefore('@') ?: "User"
        }

        return view
    }
}

