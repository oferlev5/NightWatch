package com.example.tutorial6.ui.home;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBOperations {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();;



    void insertUsernameData(HashMap<String,String> dataToEnter) {
        db.collection("users")
                .add(dataToEnter)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        System.out.println("documentReference = " + documentReference);
//                        Log.d("DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("e = " + e);
                    }
                });

    }
    
    void readData() {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                System.out.println("document = " + document);
                            }
                        } else {
                            System.out.println("task = " + task);                        }
                    }
                });
        
        
        
    }

     void getAllUserReferences(String username) {
        CollectionReference referencesCollection = db.collection("users");

        referencesCollection.whereEqualTo("name", username).whereEqualTo("password","12345")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            List<String> referenceIds = new ArrayList<>();

                            for (DocumentSnapshot document : documents) {
                                referenceIds.add(document.getId());
                                System.out.println("document = " + document);

                                System.out.println(document.getString("name"));
                                System.out.println(document.getString("password"));
                            }

                            // Process the reference IDs as needed
                            for (String referenceId : referenceIds) {
                                System.out.println("succreferenceId = " + referenceId);
                            }
                        } else {
                            System.out.println("error = " + task);
                        }
                    }
                });
    }
}






