package com.nandra.myschool.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class MySchoolRepository {
    fun getThirdGradeSubjectListReference() : DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("subject").child("third_grade")
    }

    fun getThirdGradeSubjectById(subjectID: Int) : Query {
        return FirebaseDatabase.getInstance().reference.child("subject").child("third_grade").orderByChild("subject_id").equalTo(subjectID.toDouble())
    }

    fun getSubjectByUserId(userID: String) : DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("users").child(userID).child("subject")
    }

    fun getScheduleListByUserId(userID: String) : DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("users").child(userID).child("schedule")
    }
}