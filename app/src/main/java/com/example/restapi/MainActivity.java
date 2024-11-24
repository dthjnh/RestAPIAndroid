package com.example.restapi;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String json_string = "";
    private ListView list;
    private Button btnAddStudent;
    private JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = findViewById(R.id.list);
        btnAddStudent = findViewById(R.id.btnAddStudent);

        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddStudent.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetStudent().execute();
    }

    private class GetStudent extends AsyncTask<Void, Void, Void> {

        private static final String STUDENT_API = "http://10.0.2.2:3000/students";

        @Override
        protected Void doInBackground(Void... voids) {
            json_string = HttpHandler.getJson(STUDENT_API);
            Log.d("API_RESPONSE", "Response: " + json_string);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (json_string == null || json_string.isEmpty()) {
                Toast.makeText(MainActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> students = new ArrayList<>();
            list = findViewById(R.id.list);

            try {
                jsonArray = new JSONArray(json_string);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString("name");
                    String year = jsonObject.getString("year");
                    students.add(name + " (Year: " + year + ")");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, students);
                list.setAdapter(adapter);

                // Set delete functionality on item click
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        try {
                            JSONObject selectedStudent = jsonArray.getJSONObject(position);
                            String studentId = selectedStudent.getString("id");
                            String studentName = selectedStudent.getString("name");

                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Delete Student")
                                    .setMessage("Are you sure you want to delete \"" + studentName + "\"?")
                                    .setPositiveButton("Yes", (dialog, which) -> new DeleteStudent().execute(studentId))
                                    .setNegativeButton("No", null)
                                    .show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error retrieving student data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    private class DeleteStudent extends AsyncTask<String, Void, String> {

        private static final String DELETE_API = "http://10.0.2.2:3000/students/";

        @Override
        protected String doInBackground(String... params) {
            String studentId = params[0];
            return HttpHandler.deleteJson(DELETE_API + studentId);
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
            // Refresh the student list
            new GetStudent().execute();
        }
    }
}
