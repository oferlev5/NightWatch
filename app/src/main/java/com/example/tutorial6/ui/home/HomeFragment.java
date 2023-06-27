package com.example.tutorial6.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;


import com.example.tutorial6.R;
import com.example.tutorial6.databinding.FragmentHomeBinding;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        System.out.println("idan - in homefragment");

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageButton buttonstart =binding.imageButton;

        DBOperations op = new DBOperations();
//        HashMap<String,String> note = new HashMap<>();
//        note.put("name","tamar");
//        note.put("password", "rtf456");
//        op.insertUsernameData(note);
        /**
         * idan:
         * moved all the functionality from the button outside so it jumps to functionality fragment
         */
        op.getAllUserReferences("tamar");
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment anotherFragment = new FunctionalityFragment();
        fragmentTransaction.replace(R.id.nav_host_fragment_content_drawer,anotherFragment);
//                fragmentTransaction.add(R.id.nav_host_fragment_content_main2, anotherFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


//
//        buttonstart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentManager fragmentManager = getFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                Fragment anotherFragment = new FunctionalityFragment();
//                fragmentTransaction.replace(R.id.nav_host_fragment_content_drawer,anotherFragment);
////                fragmentTransaction.add(R.id.nav_host_fragment_content_main2, anotherFragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
//            }
//        });

//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}