package com.example.irrigationapp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    var handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Loading...")
        //progressDialog.show()
        progressDialog.setCancelable(false)

     /*   webview.webViewClient = MyWebViewClient(this,progressDialog)
        val url = "http://heriplex.com/irrig/main.php"
        webview.loadUrl(url)*/


        switchto.setOnClickListener {
            val intent = Intent(this,ManualActivity::class.java)
            startActivity(intent)
            finish()
        }
        getPumpStatus()
        getPercentageValue()
        handler = Handler(Looper.getMainLooper())
    }
    private val updateMap = object : Runnable {
        override fun run() {
            //getPumpStatus()
            getPercentageValue()
            handler.postDelayed(this, 3000)
        }
    }
    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateMap)
    }
    override fun onResume() {
        super.onResume()
        handler.post(updateMap)
    }
    private fun getPercentageValue(){
        if(isConnected()){
            try {
                RetrofitClient.instance.getPercentage("fd")
                    .enqueue(object :Callback<percentageResponse>{
                        override fun onFailure(call: Call<percentageResponse>, t: Throwable) {
                            Toast.makeText(this@MainActivity,"Failed",Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call<percentageResponse>, response: Response<percentageResponse>) {
                            dateandtime.text = response.body()?.idate?.toString() + " " + response.body()?.itime?.toString()
                            circleval.text = response.body()?.moisture?.toString() + "%"
                            if(response.body()?.pumpstatus?.toInt() == 1){
                                pstatus.text = "Pump Status: ON"
                            }else{
                                pstatus.text = "Pump Status OFF"
                            }
                        }

                    })
            }catch (e:Exception){
                Toast.makeText(this,e.message.toString(),Toast.LENGTH_LONG).show()
            }
        }else
        {
            Toast.makeText(this,"Internet access is required",Toast.LENGTH_LONG).show()
        }
    }
    private fun getPumpStatus(){
        if (isConnected()){
            try {
                RetrofitClient.instance.getPump("gg")
                    .enqueue(object :Callback<PumpStatusResponse>{
                        override fun onFailure(call: Call<PumpStatusResponse>, t: Throwable) {
                            Toast.makeText(this@MainActivity,t.message.toString(),Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call<PumpStatusResponse>, response: Response<PumpStatusResponse>) {
                            pstatus.text = "Pump Status : " + response.body()?.pump_status.toString()
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
    class MyWebViewClient(private val mContext: Context, private val progressDialog: ProgressDialog) : WebViewClient(){
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            view?.loadUrl(request?.url.toString())
            return true
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            view?.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            progressDialog.dismiss()
        }
    }
}
