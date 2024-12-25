package com.dicoding.picodiploma.loginwithanimation.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.adapter.QuestionAdapter
import com.dicoding.picodiploma.loginwithanimation.databinding.FragmentHomeBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainViewModel
import java.io.File

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var questionAdapter: QuestionAdapter
    private lateinit var userRepository: UserRepository  // Inisialisasi repository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi adapter
        questionAdapter = QuestionAdapter()

        // Set adapter pada RecyclerView
        binding.rvQuestions.apply {
            adapter = questionAdapter
            layoutManager = LinearLayoutManager(context)
        }

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.apply {
            fabAddQuestion.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, UploadQuestionFragment())
                    .addToBackStack(null)
                    .commit()
            }

            notificationIcon.setOnClickListener {
                Toast.makeText(context, "Notifications", Toast.LENGTH_SHORT).show()
            }

            menuIcon.setOnClickListener {
                Toast.makeText(context, "Menu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        viewModel.questionsList.observe(viewLifecycleOwner) { questions ->
            // Mengupdate data pada adapter
            questionAdapter.submitList(questions)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Memanggil repository untuk meng-upload image dan mendapatkan ResponseClassification
    private fun getResponseClassification(imageFile: File) {
        userRepository.getResponseClassification(imageFile) { classification ->
            classification?.let {
                // Tampilkan forumLink atau lakukan hal lain dengan data yang diterima
                Toast.makeText(context, "Forum Link: ${it.forumLink}", Toast.LENGTH_SHORT).show()
            } ?: run {
                Toast.makeText(context, "Failed to classify the image.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
