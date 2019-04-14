package com.example.apgrate.data;

import com.example.apgrate.model.User;
import com.google.firebase.database.DataSnapshot;

import androidx.lifecycle.LiveData;

public interface FirebaseUserRepository {

    LiveData<User> getCurrentUser();
    User getUserById(String uid);
    LiveData<DataSnapshot> getKeyValid(String key);
    LiveData<DataSnapshot> getTests();
    LiveData<DataSnapshot> getTestById(String id);
    void registerUser(User user, String keyword);
    void signInUser(String keyword);
}
