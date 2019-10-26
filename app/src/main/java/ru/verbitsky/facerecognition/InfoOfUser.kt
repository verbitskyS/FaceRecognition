package ru.verbitsky.facerecognition


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.android.synthetic.main.info_user.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class InfoOfUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.info_user)
        val context = this

        val info = parser(intent.getStringExtra("info"))

        val face = intent.getStringExtra("face") //фотография по которой искали
        println("info " + info)
        val FIO = info[1].split(" ")
        txLastName.text = FIO[0]
        txFirstName.text = FIO[1]
        if (FIO[2]!=null) txFatherName.text = FIO[2]
        txEmail.text = info[2]
        buttonBack.setOnClickListener{
            finish()
        }
        imageView4.setImageBitmap(base64toBitmap(info[3]))



        yes.setOnClickListener{
            var s = "new_face &@# " + info[0] + " &@# " + face
            val task = Task(context, false, null, null).execute(s)
        }

    }

    fun parser(info:String):List<String>{
        return info.split(" &@# ")
    }

    fun base64toBitmap(base64str: String):Bitmap{
        val bytes = base64str.decodeBase64ToByteArray()
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}