package com.dicoding.picodiploma.loginwithanimation.view.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.ml.OCRHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class FeedsFragment : Fragment() {
    private lateinit var ocrHelper: OCRHelper
    private lateinit var ocrResultText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var imagePreview: ImageView
    private lateinit var uploadHint: TextView
    private lateinit var uploadImageButton: Button
    private lateinit var fabAddQuestion: FloatingActionButton
    private lateinit var forumButton: Button // Tambahkan deklarasi tombol Forum Diskusi

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_feeds, container, false)
        initializeViews(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun initializeViews(view: View) {
        ocrHelper = OCRHelper(requireContext())
        ocrResultText = view.findViewById(R.id.ocr_result_text)
        progressBar = view.findViewById(R.id.progress_bar)
        imagePreview = view.findViewById(R.id.image_preview)
        uploadHint = view.findViewById(R.id.upload_hint)
        uploadImageButton = view.findViewById(R.id.upload_image_button)
        fabAddQuestion = view.findViewById(R.id.fab_add_question)
        forumButton = view.findViewById(R.id.button_forum_discussion) // Inisialisasi tombol Forum Diskusi
    }

    private fun setupClickListeners() {
        uploadImageButton.setOnClickListener { pickImageFromGallery() }
        fabAddQuestion.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, UploadQuestionFragment())
                .addToBackStack(null)
                .commit()
        }
        forumButton.setOnClickListener { // Tambahkan listener untuk tombol Forum Diskusi
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://histudent-web.web.app/"))
            startActivity(intent)
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                try {
                    val inputStream = requireContext().contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    updateUIWithImage(bitmap)
                    processImage(bitmap)
                } catch (e: Exception) {
                    showError("Error loading image: ${e.message}")
                }
            }
        }
    }

    private fun updateUIWithImage(bitmap: Bitmap) {
        imagePreview.setImageBitmap(bitmap)
        imagePreview.visibility = View.VISIBLE
        uploadHint.visibility = View.GONE
    }

    private fun processImage(bitmap: Bitmap) {
        progressBar.visibility = View.VISIBLE
        ocrResultText.text = ""

        lifecycleScope.launch {
            try {
                val ocrResult = ocrHelper.runMLKitInference(bitmap)
                val validatedResult = ocrHelper.validateWithLabels(ocrResult)
                ocrResultText.text = validatedResult
            } catch (e: Exception) {
                showError("OCR failed: ${e.message}")
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
