package com.example.mobiletpa.models

class Order {

    lateinit var id: String
    lateinit var gdriveLink: String
    lateinit var paper: String
    lateinit var color: String
    lateinit var desc: String
    lateinit var payment: String
    lateinit var date: String
    var price = 0
    var status = "In Process"
}