package com.nandra.myschool.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class MySchoolRepository {

    val database = FirebaseDatabase.getInstance().reference

    fun getThirdGradeSubjectQuery(subjectID: Int) : Query {
        return database.child("subject").child("third_grade").orderByChild("subject_id").equalTo(subjectID.toDouble())
    }

    fun getSubjectDatabaseReference(userID: String) : DatabaseReference {
        return database.child("users").child(userID).child("subject")
    }

    fun getScheduleDatabaseReference(userID: String) : DatabaseReference {
        return database.child("users").child(userID).child("schedule")
    }

    fun getThirdGradeSessionQuery(subjectCode: String) : Query {
        return database.child("session").child("third_grade").child(subjectCode).limitToLast(20)
    }

    fun getUserDatabaseReference(userID: String) : DatabaseReference {
        return database.child("users").child(userID)
    }

    fun getAttendanceDatabaseReference(subjectCode: String, sessionKey: String) : DatabaseReference {
        return database.child("session").child("third_grade").child(subjectCode).child(sessionKey).child("session_attendance")
    }

    fun getMaterialDatabaseReference(subjectCode: String) : DatabaseReference {
        return database.child("material").child("third_grade").child(subjectCode)
    }
}