package com.dicoding.picodiploma.loginwithanimation.view.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.databinding.FragmentUploadQuestionBinding
import com.dicoding.picodiploma.loginwithanimation.ml.OCRHelper
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class UploadQuestionFragment : Fragment() {
    private var _binding: FragmentUploadQuestionBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: Uri? = null
    private val viewModel: MainViewModel by activityViewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var ocrHelper: OCRHelper
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            binding.ivPreview.setImageURI(uri)

            processOCR(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize OCR Helper
        ocrHelper = OCRHelper(requireContext())

        setupActions()
        observeViewModel()
    }

    private fun setupActions() {
        binding.apply {
            btnGallery.setOnClickListener { openGallery() }
            btnSave.setOnClickListener { uploadQuestion() }
        }
    }

    private fun processOCR(uri: Uri) {
        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE

                // Convert URI to Bitmap
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)

                // Run OCR
                val ocrResult = ocrHelper.runMLKitInference(bitmap)

                // Set the OCR result to description EditText
                binding.etDescription.setText(ocrResult)

            } catch (e: Exception) {
                Toast.makeText(context, "OCR Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun openGallery() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun uploadQuestion() {
        val description = binding.etDescription.text.toString()

        when {
            // Jika ada gambar
            selectedImageUri != null -> {
                selectedImageUri?.let { uri ->
                    uriToFile(uri)?.let { file ->
                        viewModel.uploadImage(file)
                    }
                }
            }
            // Jika hanya ada teks
            description.isNotEmpty() -> {
                viewModel.uploadText(description)
            }
            // Jika tidak ada input sama sekali
            else -> {
                Toast.makeText(requireContext(), "Masukkan gambar atau teks soal", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fungsi untuk membuat file dari teks
    private fun createTextFile(text: String): File {
        val file = File(requireContext().cacheDir, "text_input_${System.currentTimeMillis()}.txt")
        file.writeText(text)
        return file
    }

    // Update fungsi observeViewModel untuk menangani hasilnya
    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            // Disable input saat loading
            binding.btnSave.isEnabled = !isLoading
            binding.btnGallery.isEnabled = !isLoading
            binding.etDescription.isEnabled = !isLoading
        }

        viewModel.uploadResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                // Tambahkan soal baru ke list
                viewModel.addQuestion(it)

                Toast.makeText(
                    context,
                    "Upload berhasil! Tingkat kesulitan: ${it.classification?.difficulty}",
                    Toast.LENGTH_SHORT
                ).show()

                // Kembali ke HomeFragment
                parentFragmentManager.popBackStack()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uriToFile(uri: Uri): File? {
        val contentResolver = requireContext().contentResolver
        val myFile = createCustomTempFile(requireContext())

        try {
            val inputStream = contentResolver.openInputStream(uri) as InputStream
            val outputStream = FileOutputStream(myFile)
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
            outputStream.close()
            inputStream.close()
            return myFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun createCustomTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "TEMP_",
            ".jpg",
            storageDir
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}