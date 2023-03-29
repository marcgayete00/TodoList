package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;



public class AddTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button saveTask;
        TextView banner;
        Button setDate;
        EditText taskName;
        EditText viewDate;
        Calendar taskDate = Calendar.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        banner = findViewById(R.id.bannerAskTask);

        taskName = findViewById(R.id.taskName);
        taskName.setGravity(Gravity.CENTER);

        viewDate = findViewById(R.id.viewText);
        viewDate.setGravity(Gravity.CENTER);

        setDate = findViewById(R.id.setDate);
        viewDate.setGravity(Gravity.CENTER);

        saveTask = findViewById(R.id.saveTask);

        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener la fecha actual

                int year = taskDate.get(Calendar.YEAR);
                int month = taskDate.get(Calendar.MONTH);
                int day = taskDate.get(Calendar.DAY_OF_MONTH);

                // Crear una instancia de DatePickerDialog y mostrarla
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddTask.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                String formattedDate = sdf.format(taskDate.getTime());
                                viewDate.setText(formattedDate);
                            }
                            }, year, month, day);
                datePickerDialog.show();
            }
        });

        saveTask.setOnClickListener(v -> {

            //Obtener valores del shared preference
            SharedPreferences sharedPreferences = getSharedPreferences("TaskList", MODE_PRIVATE);
            String jsonString = sharedPreferences.getString("key", null);
            System.out.println("Sharedese"+jsonString);
            List<Task> taskList = new ArrayList<>();

            if (jsonString != null) {
                try {
                    // Convertir la cadena JSON en una lista de objetos Task
                    JSONArray jsonArray = new JSONArray(jsonString);
                    for (int t = 0; t < jsonArray.length(); t++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(t);
                        String name = jsonObject.getString("TaskName");
                        String date = jsonObject.getString("TaskDate");
                        Task task = new Task(name, date, false);
                        taskList.add(task);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            for(int i = 0; i<taskList.size(); i++){
                System.out.println("TaskList"+taskList.get(i).getTaskName());
            }

            // Verificar si la tarea ya existe en la lista antes de agregarla
            String newTaskName = taskName.getText().toString();
            String newTaskDate = viewDate.getText().toString();
            boolean taskExists = false;
            for (Task task : taskList) {
                if (task.getTaskName().equals(newTaskName) && task.getDate().equals(newTaskDate)) {
                    taskExists = true;
                    Toast.makeText(getApplicationContext(), "La tarea ya existe", Toast.LENGTH_SHORT).show();
                    break;
                }
            }

            // Si no existe, agregar la tarea a la lista
            if (!taskExists) {
                Task task = new Task(newTaskName, newTaskDate, false);
                taskList.add(task);

                try {
                    // Convertir la lista de tareas en un objeto JSON
                    JSONArray jsonArray = new JSONArray();
                    for (int t = 0; t < taskList.size(); t++) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("TaskName", taskList.get(t).getTaskName());
                        jsonObject.put("TaskDate", taskList.get(t).getDate());
                        jsonObject.put("TaskStatus", false);
                        jsonArray.put(jsonObject);
                    }

                    String newJsonString = jsonArray.toString();
                    System.out.println("Provafor" + newJsonString);

                    // Guardar la cadena JSON actualizada en SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("key", newJsonString);
                    editor.apply();

                    Toast.makeText(getApplicationContext(), "Tarea guardada", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddTask.this, MainActivity.class);
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    }
