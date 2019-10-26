package ru.verbitsky.facerecognition

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.*
import com.google.firebase.ml.vision.FirebaseVision

import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions

import kotlinx.android.synthetic.main.activity_main.*



open class MainActivity : GeneralClass() {

    private val FINAL_TAKE_PHOTO = 1
    private val FINAL_CHOOSE_PHOTO = 2
    private val PERMISSION_REQUEST_CODE_CAMERA: Int = 1
    private val PERMISSION_REQUEST_CODE_GALLERY: Int = 2

    var isPhoto = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .enableTracking()
            .build()

        val detector = FirebaseVision.getInstance()
            .getVisionFaceDetector(options)

        find.setOnClickListener{
            showDialogChoise()
        }

        register.setOnClickListener{
            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val firebaseML = FirebaseML()
        firebaseML.progressBar = progressBar

        if(resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                FINAL_TAKE_PHOTO ->
                    if (resultCode == Activity.RESULT_OK) {
                        if(imagePath!=null) firebaseML.MLFaces(this, imagePathToBitmap(imagePath), imagePath, false)
                        progressBar.visibility = View.VISIBLE

                    }
                FINAL_CHOOSE_PHOTO ->
                    if (resultCode == Activity.RESULT_OK) {
                        imagePath = handleImageOn(data)
                        if(imagePath!=null) firebaseML.MLFaces(this, imagePathToBitmap(imagePath), imagePath, false)
                        progressBar.visibility = View.VISIBLE
                    }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        progressBar.visibility = View.GONE
    }

    private fun showDialogChoise() {
        var dialogs = Dialog(this)
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogs.setContentView(R.layout.custom_dialog)

        val buttonCamera = dialogs.findViewById(R.id.btCamera) as Button
        val buttonGallery = dialogs.findViewById(R.id.btGallery) as Button
        buttonCamera.setOnClickListener {
            if (checkPersmission(this)) takePicture() else requestPermission(PERMISSION_REQUEST_CODE_CAMERA, this)
            dialogs.dismiss()
        }

        buttonGallery.setOnClickListener{
            if (checkPersmission(this)) openAlbum() else requestPermission(PERMISSION_REQUEST_CODE_GALLERY, this)
            dialogs.dismiss()
        }

        dialogs.show()

    }

}
