package com.example.irrigationapp

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Api {
    @FormUrlEncoded
    @POST("pump_status.php")
    fun togglePump(
        @Field("status") status: String
    ): Call<PumpStatusResponse>

    @FormUrlEncoded
    @POST("get_pump_status.php")
    fun getPump(
        @Field("status") status: String
    ): Call<PumpStatusResponse>

    @FormUrlEncoded
    @POST("getmoisturevalue.php")
    fun getPercentage(
        @Field("status") status: String
    ): Call<percentageResponse>
}