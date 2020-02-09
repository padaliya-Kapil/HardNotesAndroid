package com.padaliya.hardnotes;

import android.content.Intent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.padaliya.hardnotes.dataModel.Category;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView category_list; // additional dependency added

    private List<Category> category_data ;

    private Adapter adapter ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        category_data = new ArrayList<>();
        category_list = findViewById(R.id.category_list);
        category_list.setLayoutManager(new LinearLayoutManager(MainActivity.this , RecyclerView.VERTICAL , false));

    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseManager db = new DatabaseManager(MainActivity.this);
        db.open();
        category_data = db.getCategories() ;
        adapter = new Adapter() ;

        category_list.setAdapter(adapter);


    }


    public void add_category(View view)
    {
        Intent i = new Intent(MainActivity.this , AddCategory.class);
        startActivity(i);
    }

    private class ViewHolder extends RecyclerView.ViewHolder{

        TextView category_name ;

        LinearLayout category_layout ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            category_name = itemView.findViewById(R.id.category_name);
            category_layout = itemView.findViewById(R.id.category_layout);

        }
    }

    private class Adapter extends RecyclerView.Adapter<MainActivity.ViewHolder> {
        @NonNull
        @Override
        public MainActivity.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MainActivity.ViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.category_cell , parent , false));
        }


        @Override
        public void onBindViewHolder(@NonNull MainActivity.ViewHolder holder, int position) {

            final Category category = category_data.get(position);

            holder.category_name.setText(category.CATEGORY_NAME);

            holder.category_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(MainActivity.this , NotesActivity.class);

                    i.putExtra("category_id" , category.CATEGORY_ID);

                    startActivity(i);

                }
            });

        }

        @Override
        public int getItemCount() {
            return category_data.size();
        }
        }
    }

