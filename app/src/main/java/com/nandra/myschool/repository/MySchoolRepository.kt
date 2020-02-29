package com.nandra.myschool.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class MySchoolRepository {

    fun getThirdGradeSubjectQuery(subjectID: Int) : Query {
        return FirebaseDatabase.getInstance().reference.child("subject").child("third_grade").orderByChild("subject_id").equalTo(subjectID.toDouble())
    }

    fun getSubjectDatabaseReference(userID: String) : DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("users").child(userID).child("subject")
    }

    fun getScheduleDatabaseReference(userID: String) : DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("users").child(userID).child("schedule")
    }

    fun getThirdGradeSessionQuery(subjectCode: String) : Query {
        return FirebaseDatabase.getInstance().reference.child("session").child("third_grade").child(subjectCode).limitToLast(20)
    }

    fun getUserDatabaseReference(userID: String) : DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("users").child(userID)
    }

    fun getAttendanceDatabaseReference(subjectCode: String, sessionKey: String) : DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("session").child("third_grade").child(subjectCode)
            .child(sessionKey).child("session_attendance")
    }

    fun getMaterialDatabaseReference(subjectCode: String) : DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("material").child("third_grade").child(subjectCode)
    }
}