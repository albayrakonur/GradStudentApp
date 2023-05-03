package com.albayrakonur.gradapp.model

data class UserModel(
    var uid: String,
    var fullName: String,
    var email: String,
    var entryYear: String,
    var gradYear: String,
    var number: String,
    var photo: String,
    var education: String,
    var workPlace: String
) {}