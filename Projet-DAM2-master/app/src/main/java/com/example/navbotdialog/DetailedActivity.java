package com.example.navbotdialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.navbotdialog.databinding.ActivityDetailedBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class DetailedActivity extends AppCompatActivity {

    ActivityDetailedBinding binding;
    BottomNavigationView deleteEditnav;
    private MyDBHelper dbHelper;
    private String mat;
    ArrayList<ListData> dataArrayList = new ArrayList<>();
    ListData listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new MyDBHelper(getApplicationContext());

        // Initialize data from Intent
        Intent intent = getIntent();
        if (intent != null) {
            mat = intent.getStringExtra("Matricule");
            String job = intent.getStringExtra("Job");
            String firstname = intent.getStringExtra("Firstname");
            String lastname = intent.getStringExtra("Lastname");
            String numberPhone = intent.getStringExtra("Number phone");
            String email = intent.getStringExtra("Email");
            String dateOfBirth = intent.getStringExtra("Date of birth");
            String address = intent.getStringExtra("Address");
            String image = intent.getStringExtra("Image");

            binding.detailFirstname.setText(firstname);
            binding.detailLastname.setText(lastname);
            binding.addressDetailed.setText("Address: " + address);
            binding.emailDetailed.setText("Email: " + email);
            binding.NumberPhoneDetailed.setText("Phone: " + numberPhone);
            binding.dateOfBirthDetailed.setText("Date of birth: " + dateOfBirth);
            binding.matriculeDetailed.setText("Matricule: " + mat);
            binding.jobDetailed.setText("Job: " + job);
            if (image.equals(""))
            {
                binding.detailImage.setImageResource(R.drawable.person);
            }
            else
            {
                Uri uri = Uri.parse(image);
                binding.detailImage.setImageURI(uri);
            }
        }

        // Initialize deleteEditnav by finding the view in the layout XML
        deleteEditnav = findViewById(R.id.deleteEditnav);

        int s = dbHelper.getEmployeeId(mat);

        // Check if deleteEditnav is not null before performing operations on it
        if (deleteEditnav != null) {
            deleteEditnav.setOnItemSelectedListener(item -> {
                if (item.getItemId() == R.id.delete) {
                    // Show the delete confirmation dialog
                    showDeleteConfirmationDialog();
                } else if (item.getItemId() == R.id.edit) {
                    replaceFragment(intent, new ModifyFragment(dbHelper));
                }
                return true;
            });
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                goToMainActivity();
            }
        });

        ImageView cancelButton = findViewById(R.id.cancelButton_Detailed);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMainActivity();
            }
        });
    }

    private void goToMainActivity() {
        // Back is pressed... Starting the MainActivity
        Intent intent = new Intent(DetailedActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Finish the current activity to remove it from the stack
    }

    private void replaceFragment(Intent intent, Fragment fragment) {
        Bundle bundle = new Bundle();

        bundle.putString("Matricule", mat);
        bundle.putString("Job", intent.getStringExtra("Job"));
        bundle.putString("Firstname", intent.getStringExtra("Firstname"));
        bundle.putString("Lastname", intent.getStringExtra("Lastname"));
        bundle.putString("NumberPhone", intent.getStringExtra("Number phone"));
        bundle.putString("Email", intent.getStringExtra("Email"));
        bundle.putString("DateOfBirth", intent.getStringExtra("Date of birth"));
        bundle.putString("Address", intent.getStringExtra("Address"));
        bundle.putInt("Image", intent.getIntExtra("Image", R.drawable.user));

        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_Home, fragment);
        fragmentTransaction.commit();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the message and title for the dialog
        builder.setMessage("Are you sure you want to delete?")
                .setTitle("Confirmation");

        // Add "Yes" button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Yes button, perform delete operation
                if (mat != null) {
                    String message = dbHelper.deleteData(mat);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    if (message.equals("Employee deleted successfully")) {
                        goToMainActivity();
                    }
                }
                dialog.dismiss(); // Dismiss the dialog
            }
        });

        // Add "No" button
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog, do nothing or handle accordingly
                dialog.dismiss(); // Dismiss the dialog
            }
        });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
