package com.example.tutorial6.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutorial6.R;
import com.example.tutorial6.databinding.FragmentGalleryBinding;
import com.example.tutorial6.ui.home.DBOperations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HistoryFragment extends Fragment {

    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        DBOperations db = new DBOperations();
        db.getEvents( new DBOperations.FirestoreCallback() {
            @Override
            public void onSuccess(HashMap<String, Object> documents) {
                ArrayList<Item>  items= new ArrayList<>();
                // Process the retrieved documents
                for (HashMap.Entry<String, Object> entry : documents.entrySet()) {
                    String documentId = entry.getKey();
                    HashMap<String, String> documentData = (HashMap<String, String>) entry.getValue();
                    Item item = new Item(documentData.get("startTime"), documentData.get("stopTime"),documentData.get("startTime"),R.drawable.bab3n);
                    items.add(item);
                }
                loadHistory(items);

                // Call another function or return the HashMap as needed

            }


        });
//        final TextView textView = binding.textGallery;
//        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    public void loadHistory( HashMap<String, Object> documentData) {
//        RecyclerView recyclerView = binding.recycleview;
        List<Item> items = new ArrayList<Item>();
//        items.add(new Item("ofer","olev@campus", R.drawable.bab3n));
//        items.add(new Item("ofer","olev@campus", R.drawable.bab3n));
//        items.add(new Item("ofer","olev@campus", R.drawable.bab3n));
//        items.add(new Item("ofer","olev@campus", R.drawable.bab3n));
//        items.add(new Item("ofer","olev@campus", R.drawable.bab3n));


//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        recyclerView.setAdapter(new MyAdapter(getActivity(),items));

    }
    public void loadHistory(ArrayList<Item> items) {
        RecyclerView recyclerView = binding.recycleview;
        System.out.println("started loading history");
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new MyAdapter(getActivity(),items));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}