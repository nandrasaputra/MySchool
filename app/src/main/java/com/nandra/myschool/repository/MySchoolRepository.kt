package com.nandra.myschool.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MySchoolRepository {
    fun getSubjectList() : DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("subject").child("third_grade")
    }
}