package com.example.restapi;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

public class AddStudent extends AppCompatActivity {
    private String message = "";
    private Button btnAdd;
    private EditText nameText, yearText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        btnAdd = findViewById(R.id.btnAdd);
        nameText = findViewById(R.id.nameText);
        yearText = findViewById(R.id.yearText);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PostStudent().execute();
            }
        });
    }

    public void onAddStudent(View view) {
        if (nameText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Name cannot be empty!", Toast.LENGTH_SHORT).show();
        } else if (yearText.getText().toString().isEmpty() || !yearText.getText().toString().matches("\\d{4}")) {
            Toast.makeText(this, "Year must be a valid 4-digit number!", Toast.LENGTH_SHORT).show();
        } else {
            new PostStudent().execute();
        }

    }

    private class PostStudent extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String name = nameText.getText().toString();
            String year = yearText.getText().toString();

            if (year.isEmpty()) {
                message = "Year cannot be empty!";
            } else if(name.isEmpty()) {
                message = "Name cannot be empty!";
            } else {
                message = HttpHandler.postJson(name, year);
            }

            Log.d("POST RESPONSE", "message: " + message);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(AddStudent.this, message, Toast.LENGTH_SHORT).show();
            if (!message.equals("Year cannot be empty!")) {
                finish();
            }
        }
    }
}