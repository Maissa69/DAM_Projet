package com.example.navbotdialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ModifyFragment extends Fragment {
    // Define ActivityResultLauncher for image picking
    private ActivityResultLauncher<String> imagePickerLauncher;
    String path = "";
    ImageView imageView;
    FloatingActionButton button;
    EditText prenomEditText, jobEditText, emailEditText, numtelEditText, addressEditText, nameEditText;
    String matricule;

    private MyDBHelper dbHelper;
    public ModifyFragment(MyDBHelper dbH) {
        // Required empty public constructor
        this.setDBHelper(dbH);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ActivityResultLauncher for image picking
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        // Image Uri will not be null for RESULT_OK
                        imageView.setImageURI(uri);
                        path = uri.toString();
                    } else {
                        Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Use this method to launch the image picker
    private void launchImagePicker() {
        imagePickerLauncher.launch("image/*");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_modify, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            matricule = bundle.getString("Matricule");
            String job = bundle.getString("Job");
            String firstname = bundle.getString("Firstname");
            String lastname = bundle.getString("Lastname");
            String numberPhone = bundle.getString("NumberPhone");
            String email = bundle.getString("Email");
            String address = bundle.getString("Address");
            int image = bundle.getInt("Image", R.drawable.user);

            // Find EditText views
            prenomEditText = view.findViewById(R.id.Prenom_Detailed);
            nameEditText = view.findViewById(R.id.fName_Detailed);
            addressEditText = view.findViewById(R.id.address_Detailed);
            emailEditText = view.findViewById(R.id.email_Detailed);
            numtelEditText = view.findViewById(R.id.numtel_Detailed);
            jobEditText = view.findViewById(R.id.job_Detailed);

            // Set values to EditText fields
            prenomEditText.setText(firstname);
            nameEditText.setText(lastname);
            addressEditText.setText(address);
            emailEditText.setText(email);
            numtelEditText.setText(numberPhone);
            jobEditText.setText(job);

            EditText name = view.findViewById(R.id.detailFirstname);
            name.setText(lastname);
            EditText fname = view.findViewById(R.id.detailLastname);
            fname.setText(firstname);
        }

        // Tu ajoute les fonctions pour recupere les donees modifiers dans cette class
        ImageView cancelButton = view.findViewById(R.id.cancelButton_Detailed);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().remove(ModifyFragment.this).commit();
            }
        });


        // perform update
        Button saveButton = view.findViewById(R.id.loginButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEmployee(view);
            }
        });


        imageView = view.findViewById(R.id.imageView);
        button = view.findViewById(R.id.floatingActionButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the method to launch image picker
                launchImagePicker();
            }
        });
        return view;
    }

    private void updateEmployee(View dialog) {
        // Gather data from input fields
        String name = nameEditText.getText().toString();
        String prenom = prenomEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String numtel = numtelEditText.getText().toString();
        String job = jobEditText.getText().toString();

        // Create an Employee object
        Employee employee = new Employee(matricule, name, prenom, "", address, job, numtel, email, path);

        // Call insertData method from DbHelper
        String message = dbHelper.updateData(employee);

        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();

        // Close the dialog
        if (message.equals("Employee updated successfully")) {
            goToMainActivity();
        }
    }

    private void goToMainActivity() {
        // Back is pressed... Starting the MainActivity
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish(); // Finish the current activity to remove it from the stack
    }

    public void setDBHelper(MyDBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }
}