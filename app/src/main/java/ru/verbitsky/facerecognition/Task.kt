package ru.verbitsky.facerecognition

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.registration.*
import java.io.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.Socket
import java.net.URL

class Task(context: Context, booleanRegistration: Boolean, textView: TextView?, dialog: Dialog?) : AsyncTask<String, Void, String>() {
    val url = "http://ac7f1898.ngrok.io"
    val context = context
    var result: String = ""
    var input = ""
    var boolRegis = booleanRegistration
    var textView = textView
    var dialog = dialog

    override fun doInBackground(vararg params: String): String? { //Соединение с сервером, отправка информации и получение ответа
        input = params[0]
        try{
            sendPostRequest(params[0])
        }catch(e: Exception){
            e.printStackTrace()
          }catch (e: IOError) {
             e.printStackTrace()
          } finally {

        }
        return result
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)

        if(!result.equals("error")){
            if(result.equals("No user")){
                Toast.makeText(context, "User did not find!", Toast.LENGTH_LONG).show()
            }else {
                if(result.contains("added")) {
                    Toast.makeText(context, "Thanks, new face added!", Toast.LENGTH_LONG).show()
                }else{
                    if (!boolRegis) {
                        dialog?.dismiss()
                        val intent: Intent = Intent(context, InfoOfUser::class.java)
                        intent.putExtra("info", result)
                        intent.putExtra("face", input.split(" &@# ")[1])
                        context.startActivity(intent)
                    } else {
                        textView?.visibility = View.VISIBLE
                        textView?.text = "Done!"
                    }
                }
            }
        }else{
            println("Error!")
            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
        }
    }

    fun sendPostRequest(str:String) {

        val mURL = URL(url)

        with(mURL.openConnection() as HttpURLConnection) {
            // optional default is GET
            requestMethod = "POST"

            val wr = OutputStreamWriter(getOutputStream());
            wr.write(str);
            wr.flush();

            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                it.close()
                println("Response : $response")
                result = response.toString()

            }
        }
    }
}