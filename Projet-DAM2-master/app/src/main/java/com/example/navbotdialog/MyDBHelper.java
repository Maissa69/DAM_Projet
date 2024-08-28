package com.example.navbotdialog;

import android.database.SQLException;
import android.util.Log;
import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

import java.util.ArrayList;

public class MyDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "project.db";
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create your table here
        String query = "CREATE TABLE IF NOT EXISTS employees (" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " mat TEXT UNIQUE NOT NULL," +
                " firstname TEXT NOT NULL," +
                " lastname TEXT NOT NULL," +
                " dateOfBirth DATE NOT NULL," +
                " address TEXT NOT NULL," +
                " job TEXT NOT NULL," +
                " numberPhone TEXT NOT NULL," +
                " email TEXT UNIQUE NOT NULL," +
                " image TEXT);";
        db.execSQL(query);
    }

    public String insertData(Employee employee) {
        long rowsAffected = -1;
        String mat = employee.getMat();
        if (mat.isEmpty())
            return "missing matricule";

        String firstname = employee.getFirstname();
        if (firstname.isEmpty())
            return "missing firstname";

        String lastname = employee.getLastname();
        if (lastname.isEmpty())
            return "missing lastname";

        String dateOfBirth = employee.getDateOfBirth();
        if (dateOfBirth.isEmpty())
            return "missing dateOfBirth";

        String address = employee.getAddress();
        if (address.isEmpty())
            return "missing address";

        String job = employee.getJob();
        if (job.isEmpty())
            return "missing job";

        String num = employee.getNumberPhone();
        if (num.isEmpty())
            return "missing numberPhone";

        String email = employee.getEmail();
        if (email.isEmpty() ||
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return "missing or invalid email";
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("mat", mat);
            values.put("firstname", firstname);
            values.put("lastname", lastname);
            values.put("dateOfBirth", dateOfBirth);
            values.put("address", address);
            values.put("job", job);
            values.put("numberPhone", num);
            values.put("email", email);
            values.put("image", employee.getImage());
            rowsAffected = db.insert("employees", null, values);
            db.close();
        } catch (SQLException e) {
            // Handle SQLExceptions
            return "Database error: " + e.getMessage();
        }

        if (rowsAffected > 0)
            return "Employee added successfully";

        return "Failed to add employee";
    }

    public String updateData(Employee employee) {
        String mat = employee.getMat();
        if (mat.isEmpty())
            return "missing matricule";

        String firstname = employee.getFirstname();
        if (firstname.isEmpty())
            return "missing firstname";

        String lastname = employee.getLastname();
        if (lastname.isEmpty())
            return "missing lastname";

        String address = employee.getAddress();
        if (address.isEmpty())
            return "missing address";

        String job = employee.getJob();
        if (job.isEmpty())
            return "missing job";

        String num = employee.getNumberPhone();
        if (num.isEmpty())
            return "missing numberPhone";

        String email = employee.getEmail();
        if (email.isEmpty() ||
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return "missing or invalid email";

        int rowsAffected = -1;
        try {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("firstname", firstname);
        values.put("lastname", lastname);
        values.put("address", address);
        values.put("job", job);
        values.put("numberPhone", num);
        values.put("email", email);
        values.put("image", employee.getImage());
        rowsAffected = db.update("employees", values, "id = ?", new String[]{String.valueOf(getEmployeeId(mat))});
        db.close();
        } catch (SQLException e) {
            // Handle SQLExceptions
            return "Database error: " + e.getMessage();
        }
        if (rowsAffected > 0)
            return "Employee updated successfully";

        return "Failed to update employee";
    }


    // Delete data from the table
    public String deleteData(String mat) {
        int rowsAffected = -1;

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            rowsAffected = db.delete("employees", "id = ?", new String[]{String.valueOf(getEmployeeId(mat))});
            db.close();
        } catch (SQLException e) {
            // Handle SQLExceptions
            return "Database error: " + e.getMessage();
        }

        if (rowsAffected > 0)
            return "Employee deleted successfully";

        return "Failed to delete employee";
    }

    public int getEmployeeId(String mat) {
        int employeeId = -1; // Default value if not found
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database you will actually use after this query.
        String[] projection = {"id"};

        // Filter results WHERE "mat" = ?
        String selection = "mat = ?";
        String[] selectionArgs = {mat};

        Cursor cursor = db.query(
                "employees",   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null                    // don't need the sort order since we are only retrieving the ID
        );

        if (cursor.moveToFirst()) {
            employeeId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        }
        cursor.close();
        return employeeId;
    }

    public ArrayList<Employee> filterEmployees(String value, String column) {
        // Define a base query
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM employees WHERE 1=1");

        // prepare for the searching
        ArrayList<Employee> employees = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // search by name
        if (column.equals("name")) {
            queryBuilder.append(" AND (firstname LIKE '%").append(value).append("%' OR lastname LIKE '%").append(value).append("%')");
        }

        // search by matricule or phone
        else if (column.equals("mat") || column.equals("numberPhone")) {
            queryBuilder.append(" AND ").append(column).append(" LIKE '").append(value).append("%'");
        }

        // search by email or address or job
        else if (column.equals("email") || column.equals("address") || column.equals("job")) {
            queryBuilder.append(" AND ").append(column).append(" LIKE '%").append(value).append("%'");
        }

        Cursor cursor = db.rawQuery(queryBuilder.toString(), null);

        while (cursor.moveToNext()) {
            String tmpMat = cursor.getString(cursor.getColumnIndexOrThrow("mat"));
            String tmpFirstname = cursor.getString(cursor.getColumnIndexOrThrow("firstname"));
            String tmpLastname = cursor.getString(cursor.getColumnIndexOrThrow("lastname"));
            String tmpDateOfBirth = cursor.getString(cursor.getColumnIndexOrThrow("dateOfBirth"));
            String tmpAddress = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            String tmpJob = cursor.getString(cursor.getColumnIndexOrThrow("job"));
            String tmpNumberPhone = cursor.getString(cursor.getColumnIndexOrThrow("numberPhone"));
            String tmpEmail = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String tmpImage = cursor.getString(cursor.getColumnIndexOrThrow("image"));

            // Create an Employee object
            Employee employee = new Employee(tmpMat, tmpFirstname, tmpLastname, tmpDateOfBirth, tmpAddress, tmpJob,
                    tmpNumberPhone, tmpEmail, tmpImage);
            employees.add(employee);
        }
        cursor.close();
        return employees;
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // If you need to upgrade the database version, handle it here
    }
}
