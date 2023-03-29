package com.example.todolist;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


// MyAdapter.java
public class Controller extends RecyclerView.Adapter<Controller.ViewHolder> {

    private static List<String> mData = new ArrayList<>();  //Array que se obtiene del shared preference
    private List<Task> tasks = new ArrayList<>(); //Array del objeto Task

    public Controller(SharedPreferences sharedPreferences){
        String jsonString = sharedPreferences.getString("key", "");
        System.out.println("Valor json"+jsonString);
        mData.clear();
        if (!jsonString.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String task = jsonArray.getJSONObject(i).toString();
                    System.out.println("Valor task"+i+" "+task);
                    mData.add(task);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Boolean taskComplete = false;

        tasks.clear();
        // Limpiar la lista tasks antes de agregar nuevos elementos
        for (String jsonString : mData) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                String taskName = jsonObject.getString("TaskName");
                String taskDate = jsonObject.getString("TaskDate");
                try {
                    taskComplete = jsonObject.getBoolean("TaskComplete");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //System.out.println("Nombre"+taskName);
                Task newTask = new Task(taskName, taskDate, taskComplete);
                tasks.add(newTask);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Task task = tasks.get(position);

        Context context = holder.itemView.getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        holder.mTaskName.setText(task.getTaskName() + "\n" + task.getDate());

        if (sharedPreferences.getBoolean(task.getTaskName(), false)){
            holder.mCheckBox.setChecked(true);
        } else {
            holder.mCheckBox.setChecked(false);
        }

        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                task.setComplete(isChecked);

                // Obtener la instancia del SharedPreferences
                Context context = buttonView.getContext();
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

                // Editar el valor en SharedPreference
                int taskPosition = holder.getAdapterPosition();
                Task task = tasks.get(taskPosition);
                String taskName = task.getTaskName();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(taskName, isChecked);
                editor.apply();


                System.out.println("Valor de" + taskName + sharedPreferences.getBoolean(taskName, false));

                for(int i = 0; i<tasks.size(); i++){
                    if (tasks.get(i).getTaskName().equals(taskName)){
                        tasks.get(i).setComplete(isChecked);
                    }
                }
                /*
                if (isChecked) {
                    removeItem(position, sharedPreferences);
                }
                */

            }

        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void removeItem(int position, SharedPreferences sharedPreferences) {
        mData.remove(position);
        notifyItemRemoved(position);

        StringBuilder stringBuilder = new StringBuilder();
        for (String data : mData) {
            stringBuilder.append(data).append(",");
        }
        String concatenatedData = stringBuilder.toString();

        // Guardar la cadena concatenada en el SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        System.out.println(concatenatedData);
        editor.putString("key", concatenatedData);
        editor.apply();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTaskName;
        public CheckBox mCheckBox;
        public ViewHolder(View itemView) {
            super(itemView);
            mTaskName = itemView.findViewById(R.id.TaskName);
            mCheckBox = itemView.findViewById(R.id.checkbox);
        }
    }
}