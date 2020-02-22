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
}