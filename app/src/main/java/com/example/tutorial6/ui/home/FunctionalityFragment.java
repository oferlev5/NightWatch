package com.example.tutorial6.ui.home;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.tutorial6.Drawer2Activity;
import com.example.tutorial6.R;
import com.example.tutorial6.TerminalFragment;
import com.example.tutorial6.databinding.FragmentFunctionalityBinding;
import com.example.tutorial6.databinding.FragmentHomeBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FunctionalityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FunctionalityFragment extends Fragment {

    Button startBtn;
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
        System.out.println("idan - in funcfrag");

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
        System.out.println("i am in activityy" + getActivity());
        View root = binding.getRoot();
        startBtn = binding.startBut;

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment anotherFragment = new TerminalFragment();
                Drawer2Activity activity = (Drawer2Activity) getActivity();
                Bundle args = activity.getBundledata();
                System.out.println("args = " + args.toString());
                anotherFragment.setArguments(args);
                fragmentTransaction.replace(R.id.nav_host_fragment_content_drawer,anotherFragment).addToBackStack(null).commit();
//                fragmentTransaction.add(R.id.nav_host_fragment_content_main2, anotherFragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();


            }
        });

        return root;
    }
}