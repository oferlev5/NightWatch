package com.example.tutorial6.ui.home;


import com.example.tutorial6.CredentialCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
import java.util.concurrent.CountDownLatch;

import io.grpc.internal.DnsNameResolver;

public class DBOperations {
    public HashMap<String,String> helper;

    public FirebaseFirestore db = FirebaseFirestore.getInstance();;

    public void insertUsernameData(HashMap<String,String> dataToEnter) {
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
    
    public void readData() {
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

    public void getDocumentsByEmailAndPassword(String email, String password, final FirestoreCallback callback) {
        db.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            HashMap<String, Object> documents = new HashMap<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                documents.put(document.getId(), document.getData());
                            }
                            callback.onSuccess(documents);
                        } else {
                            Exception exception = task.getException();

                        }
                    }
                });
    }


    public void getEvents( final FirestoreCallback callback) {
        db.collection("events ")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            HashMap<String, Object> documents = new HashMap<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                Map<String, Object> documentData = document.getData();

                                System.out.println("Document ID: " + documentId);
                                System.out.println("Document Data: " + documentData);

                                documents.put(documentId, documentData);
                            }
                            System.out.println(task.getResult().size());
                            System.out.println("documentsev = " + documents);
                            callback.onSuccess(documents);
                        } else {
                            Exception exception = task.getException();
                            System.out.println("failed");

                        }
                    }
                });
    }


    public interface FirestoreCallback {
        void onSuccess(HashMap<String, Object> documents);

    }
}












