package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView banner;
    private Button addTask;

    private RecyclerView recyclerView;
    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        banner = findViewById(R.id.banner);

        controller = new Controller(getSharedPreferences("TaskList", MODE_PRIVATE));

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(controller);

        // Agrega el deslizamiento para eliminar elementos
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override

            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                SharedPreferences sharedPreferences = getSharedPreferences("TaskList", Context.MODE_PRIVATE);
                int position = viewHolder.getAdapterPosition();
                controller.removeItem(position, sharedPreferences);
                //controller.removeDataSharedPreference(position);
            }
        };
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        // Verifica si la lista de tareas está vacía y actualiza el banner en consecuencia
        if (controller.getItemCount() == 0){
            banner.setText("No hay tareas pendientes");
        }

        // Agrega el botón para agregar una nueva tarea
        addTask = findViewById(R.id.addTask);
        addTask.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTask.class);
            startActivity(intent);
        });
    }
}