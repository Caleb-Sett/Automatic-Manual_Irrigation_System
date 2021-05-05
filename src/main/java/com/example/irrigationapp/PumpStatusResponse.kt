package com.example.irrigationapp

data class PumpStatusResponse (
    val error : Boolean,
    val message : String,
    val pump_status : String
)