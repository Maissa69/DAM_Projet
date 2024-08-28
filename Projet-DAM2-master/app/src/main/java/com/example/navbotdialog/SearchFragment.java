package com.example.navbotdialog;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SearchFragment extends Fragment {
    // Declare a listener variable
    private OnSearchListener searchListener;

    // Declare TextViews
    TextView reset, mat, name, email, phone, job, address;
    TextView textView;
    Button search;
    EditText editText;
    private BottomNavigationView bottomNavigationView;

    public SearchFragment(BottomNavigationView bottomNavigationView) {
        this.bottomNavigationView = bottomNavigationView;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("AAAA");
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize TextViews
        reset = rootView.findViewById(R.id.reset);
        mat = rootView.findViewById(R.id.mat);
        name = rootView.findViewById(R.id.name);
        email = rootView.findViewById(R.id.email);
        phone = rootView.findViewById(R.id.phone);
        job = rootView.findViewById(R.id.job);
        address = rootView.findViewById(R.id.address);

        // Initialize the button
        search = rootView.findViewById(R.id.button);

        // Initialize edit text
        editText = rootView.findViewById(R.id.editText);

        textView = rootView.findViewById(R.id.reset);



        // Set OnClickListener to reset
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mat.setTextColor(Color.WHITE);
                name.setTextColor(Color.WHITE);
                email.setTextColor(Color.WHITE);
                phone.setTextColor(Color.WHITE);
                job.setTextColor(Color.WHITE);
                address.setTextColor(Color.WHITE);
                editText.setText("");
            }
        });

        View.OnClickListener textViewClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset text color of all TextViews to black
                mat.setTextColor(Color.BLACK);
                name.setTextColor(Color.BLACK);
                email.setTextColor(Color.BLACK);
                phone.setTextColor(Color.BLACK);
                job.setTextColor(Color.BLACK);
                address.setTextColor(Color.BLACK);

                // Set text color of the clicked TextView to red
                textView = (TextView) v;
                textView.setTextColor(Color.RED);
            }
        };

        // Set the click listener to all TextViews
        mat.setOnClickListener(textViewClickListener);
        name.setOnClickListener(textViewClickListener);
        email.setOnClickListener(textViewClickListener);
        phone.setOnClickListener(textViewClickListener);
        job.setOnClickListener(textViewClickListener);
        address.setOnClickListener(textViewClickListener);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the filter
                KeyboardUtils.hideKeyboard(requireContext(), editText);

                String filterBy = textView.getText().toString();
                // get the value to search for
                String information = editText.getText().toString();

                // if filter is empty but the information is provided don't perform the search
                if (filterBy.equals("RESET") && !information.isEmpty()) {
                    String message = "select the filter or RESET";
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                }
                else {
                    // Pass the information to the activity
                    bottomNavigationView.setSelectedItemId(R.id.home);
                    searchListener.onSearch(information, filterBy);
                }
            }
        });

        return rootView;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Check if the activity implements the interface
        if (context instanceof OnSearchListener) {
            searchListener = (OnSearchListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSearchListener");
        }
    }

    // Define an interface
    public interface OnSearchListener {
        void onSearch(String information, String filterBy);
    }
}
