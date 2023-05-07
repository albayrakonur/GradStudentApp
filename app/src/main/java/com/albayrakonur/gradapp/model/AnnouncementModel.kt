package com.albayrakonur.gradapp.model

import com.google.firebase.Timestamp

data class AnnouncementModel(
    var uid: String,
    var fullName: String,
    var entryDateTime: Timestamp,
    var description: String
)
