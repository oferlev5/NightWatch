package com.example.tutorial6;

import java.util.HashMap;

public interface CredentialCallback {
    void onSuccess(HashMap<String, String> credentials);
    void onFailure(String errorMessage);
}
