package com.example.tutorial6.ui.home;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.tutorial6.R;
import com.example.tutorial6.databinding.FragmentFunctionalityBinding;
import com.example.tutorial6.databinding.FragmentHomeBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FunctionalityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FunctionalityFragment extends Fragment {

    CardView cardHome;
    private FragmentFunctionalityBinding binding;


    public FunctionalityFragment() {
        // Required empty public constructor
    }


    public static FunctionalityFragment newInstance(String param1, String param2) {
        FunctionalityFragment fragment = new FunctionalityFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFunctionalityBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        cardHome = binding.cardHome;

        return root;
    }
}