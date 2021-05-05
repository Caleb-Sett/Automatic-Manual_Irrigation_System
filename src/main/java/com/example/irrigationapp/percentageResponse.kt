package com.example.irrigationapp

data class percentageResponse (
    val error : Boolean,
    val message: String,
    val moisture : String,
    val pumpstatus : String,
    val idate : String,
    val itime : String
)