package com.example.navbotdialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.VibrationEffect;
import android.os.VibratorManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.dhaval2404.imagepicker.ImagePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.widget.Toast;
import android.content.Context;
import android.os.Vibrator;
import androidx.activity.OnBackPressedCallback;

public class MainActivity extends AppCompatActivity  implements SearchFragment.OnSearchListener {

    private String path = "";
    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    ImageView imageView;
    FloatingActionButton button;
    private DatePickerDialog datePickerDialog;
    private Button dateButton, save;

    private MyDBHelper dbHelper;
    private boolean backPressedOnce = false;
    private Executor executor = Executors.newSingleThreadExecutor();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        dbHelper = new MyDBHelper(getApplicationContext());

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new AboutFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        onSearch("", "");

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home){
                onSearch("", "");
            } else if (item.getItemId() == R.id.shorts) {
                replaceFragment(new SearchFragment(bottomNavigationView));
            } else if (item.getItemId() == R.id.subscriptions) {
                replaceFragment(new NightFragment());
            } else if (item.getItemId() == R.id.library) {
                replaceFragment(new AboutFragment());
            }
            return true;
        });

        fab.setOnClickListener(view -> showBottomDialog());

        // Create a callback for the back button press
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                handleBackPress();
            }
        };
        // Add the callback to the back button dispatcher
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    // methode de button +
    private void showBottomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomshetlayout);

        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        initDatePicker();
        dateButton = dialog.findViewById(R.id.datePicker);
        dateButton.setText(getTodaysDate());

        // Initialiser les vues de la boîte de dialogue
        imageView = dialog.findViewById(R.id.imageView);
        button = dialog.findViewById(R.id.floatingActionButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(MainActivity.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });

        Button saveButton = dialog.findViewById(R.id.loginButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEmployee(dialog);
            }
        });
/**/

        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            // Image Uri will not be null for RESULT_OK
            Uri uri = data.getData();

            // Use Uri object instead of File to avoid storage permissions
            imageView.setImageURI(uri);

            path = uri.toString();

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String date = makeDateString(day, month, year);
            dateButton.setText(date);
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = DatePickerDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        switch (month) {
            case 1:
                return "JAN";
            case 2:
                return "FEB";
            case 3:
                return "MAR";
            case 4:
                return "APR";
            case 5:
                return "MAY";
            case 6:
                return "JUN";
            case 7:
                return "JUL";
            case 8:
                return "AUG";
            case 9:
                return "SEP";
            case 10:
                return "OCT";
            case 11:
                return "NOV";
            case 12:
                return "DEC";
            default:
                return "JAN";
        }
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    private void addEmployee(Dialog dialog) {
        // Gather data from input fields
        String mat = ((EditText) dialog.findViewById(R.id.Mat)).getText().toString();
        String name = ((EditText) dialog.findViewById(R.id.Name)).getText().toString();
        String prenom = ((EditText) dialog.findViewById(R.id.Prenom)).getText().toString();
        String address = ((EditText) dialog.findViewById(R.id.address)).getText().toString();
        String email = ((EditText) dialog.findViewById(R.id.email)).getText().toString();
        String numtel = ((EditText) dialog.findViewById(R.id.numtel)).getText().toString();
        String job = ((EditText) dialog.findViewById(R.id.job)).getText().toString();
        String dateOfBirth = ((Button) dialog.findViewById(R.id.datePicker)).getText().toString();

        // Create an Employee object
        Employee employee = new Employee(mat, name, prenom, dateOfBirth, address, job, numtel, email, path);

        // Call insertData method from DbHelper
        String message = dbHelper.insertData(employee);

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

        // Close the dialog
        if (message.equals("Employee added successfully")) {
            dialog.dismiss();
            onSearch("", "");
        }
    }

    private void handleBackPress() {
        if (backPressedOnce) {
            // Exit the app if back button pressed twice
            finish();
            return;
        }

        backPressedOnce = true;
        Toast.makeText(MainActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        // Reset the flag after 2 seconds to allow the user to press back again
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    backPressedOnce = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onSearch(String information, String filterBy) {
        // Perform fragment change to HomeFragment and pass the information
        HomeFragment homeFragment = new HomeFragment(dbHelper);
        Bundle bundle = new Bundle();
        bundle.putString("information", information);
        bundle.putString("filterBy", filterBy);
        homeFragment.setArguments(bundle);
        replaceFragment(homeFragment);
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
