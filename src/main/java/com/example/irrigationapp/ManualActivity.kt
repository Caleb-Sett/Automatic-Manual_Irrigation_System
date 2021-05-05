package com.example.irrigationapp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_manual.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManualActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual)

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Loading...")
        //progressDialog.show()
        progressDialog.setCancelable(false)

        on.setOnClickListener {
            if (isConnected()) {
                progressDialog.show()
                try {
                    RetrofitClient.instance.togglePump("ON")
                        .enqueue(object : Callback<PumpStatusResponse> {
                            override fun onFailure(call: Call<PumpStatusResponse>, t: Throwable) {
                                Toast.makeText(this@ManualActivity,t.message.toString(),Toast.LENGTH_LONG ).show()
                                progressDialog.dismiss()
                                getPumpStatus()
                            }

                            override fun onResponse(call: Call<PumpStatusResponse>, response: Response<PumpStatusResponse>) {
                                progressDialog.dismiss()
                                pmanualstatus.text = "Pump Status : " + response.body()?.pump_status
                                Toast.makeText(this@ManualActivity,response.body()?.message.toString(),Toast.LENGTH_LONG).show()
                            }

                        })
                }catch (e:Exception){
                    Toast.makeText(this,e.message.toString(),Toast.LENGTH_LONG).show()
                    progressDialog.dismiss()
                    getPumpStatus()
                }
            }else{
                Toast.makeText(this,"Internet access is required",Toast.LENGTH_LONG).show()
                progressDialog.dismiss()
                getPumpStatus()
            }
        }

        off.setOnClickListener {
            if (isConnected()) {
                progressDialog.show()
                try {
                    RetrofitClient.instance.togglePump("OFF")
                        .enqueue(object : Callback<PumpStatusResponse> {
                            override fun onFailure(call: Call<PumpStatusResponse>, t: Throwable) {
                                Toast.makeText(this@ManualActivity,t.message.toString(),Toast.LENGTH_LONG).show()
                                getPumpStatus()
                                progressDialog.dismiss()
                            }

                            override fun onResponse(call: Call<PumpStatusResponse>,response: Response<PumpStatusResponse>) {
                                progressDialog.dismiss()
                                pmanualstatus.text = "Pump Status : " + response.body()?.pump_status
                                Toast.makeText(this@ManualActivity,response.body()?.message.toString(),Toast.LENGTH_LONG).show()
                            }

                        })
                }catch (e:Exception){
                    Toast.makeText(this,e.message.toString(),Toast.LENGTH_LONG).show()
                    progressDialog.dismiss()
                    getPumpStatus()
                }
            }else
            {
                Toast.makeText(this,"Internet access is required",Toast.LENGTH_LONG).show()
                progressDialog.dismiss()
                getPumpStatus()
            }
        }

        HomeArrow.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        getPumpStatus()
    }
    private fun isConnected(): Boolean {
        var connected = false
        try {
            val cm =
                this?.applicationContext
                    ?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val nInfo = cm.activeNetworkInfo
            connected = nInfo != null && nInfo.isAvailable && nInfo.isConnected
            return connected
        } catch (e: java.lang.Exception) {
            /* Log.e("Connectivity Exception", e.message) */
        }
        return connected
    }
    private fun getPumpStatus(){
        if (isConnected()){
            try {
                RetrofitClient.instance.getPump("gg")
                    .enqueue(object :Callback<PumpStatusResponse>{
                        override fun onFailure(call: Call<PumpStatusResponse>, t: Throwable) {
                            Toast.makeText(this@ManualActivity,t.message.toString(),Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call<PumpStatusResponse>, response: Response<PumpStatusResponse>) {
                            pmanualstatus.text = "Pump Status : " + response.body()?.pump_status.toString()
                        }

                    })
            }catch (e:Exception){
                Toast.makeText(this,e.message.toString(),Toast.LENGTH_LONG).show()
            }
        }
        else
        {
            Toast.makeText(this,"Internet access is required",Toast.LENGTH_LONG).show()
        }
    }
}
