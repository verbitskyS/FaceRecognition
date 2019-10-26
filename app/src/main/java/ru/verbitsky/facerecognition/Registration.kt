package ru.verbitsky.facerecognition


import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import kotlinx.android.synthetic.main.registration.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.info_user.*

class Registration : GeneralClass() {

    private val FINAL_TAKE_PHOTO = 1
    private val FINAL_CHOOSE_PHOTO = 2
    private val PERMISSION_REQUEST_CODE_CAMERA: Int = 1
    private val PERMISSION_REQUEST_CODE_GALLERY: Int = 2
    lateinit var firebaseML : FirebaseML

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration)

        tx.visibility = View.GONE

        face.setOnClickListener {
            showDialogChoise()
        }

        firebaseML = FirebaseML()


        buttonSignUp.setOnClickListener{
                if((editTextFirstName.text.toString()!="")&&(editTextLastName.text.toString()!="")
                    &&(editTextEmail.text.toString()!="")&&(firebaseML.isPhoto)){
                    val b = "registration &@# " + editTextLastName.text.toString() +
                            " " + editTextFirstName.text.toString() + " " + editTextFatherName.text.toString() +
                             " &@# " + editTextEmail.text.toString()+ " &@# "+ bitmapToBase64(face.drawable.toBitmap())

                    val len = (b.length+9).toString()
                    val s = len + " &@# " + b
                    val task = Task(this, true, tx, null).execute(s)

                }else{
                    Toast.makeText(this, "is empty", Toast.LENGTH_SHORT)
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        firebaseML.imageView = face
        firebaseML.choise_photo = choise_photo

        if(resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                FINAL_TAKE_PHOTO ->
                    if (resultCode == Activity.RESULT_OK) {
                        if(imagePath!=null) firebaseML.MLFaces(this, imagePathToBitmap(imagePath), imagePath, true)
                        tx.text = "Wait..."

                    }
                FINAL_CHOOSE_PHOTO ->
                    if (resultCode == Activity.RESULT_OK) {
                        imagePath = handleImageOn(data)
                        if(imagePath!=null) firebaseML.MLFaces(this, imagePathToBitmap(imagePath), imagePath, true)
                        tx.text = "Wait..."
                    }
            }
        }
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