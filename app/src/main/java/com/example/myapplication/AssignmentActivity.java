package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import static com.example.myapplication.MainActivity.db;

public class AssignmentActivity extends AppCompatActivity {
    Context context = AssignmentActivity.this;
    ListView listView;
    List<String> courseList = new ArrayList<>();
    ArrayAdapter adapter;
    AssignmentSql tag;
    List<CoursesSql> tagsWatchList;
//    DatabaseHelper db;
    List<AssignmentSql> allTags; //list of course
     int Assg =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        String title=getIntent().getStringExtra("Title");
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        tag=new AssignmentSql();
        allTags = db.getAllTags();
        tagsWatchList = new ArrayList<>();


        //back button
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        //

        for (AssignmentSql i : allTags) {
            if (i.getAssignmentName().contains(title.split("\t\t")[0])){
                tag=i;
                Log.d("course tag",tag.getAssignmentName());

            }
        }

        tagsWatchList = db.getAllToDosByTag(tag.getAssignmentName());
        courseList.clear();
        for (CoursesSql todo : tagsWatchList) {
            Log.d("ToDo Watchlist", todo.getCourseName());
            courseList.add(todo.getCourseName());
        }
        //listview
        adapter = new ArrayAdapter<String>(this, R.layout.listview, courseList);
        listView = findViewById(R.id.listView2);
        //clicklistner
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Object o = listView.getItemAtPosition(position);
//                Log.d("listener",o.toString());
//                try {
////                    deleteDialog(o.toString());
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        listView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAssignmentDialog(view);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
//        return super.onCreateOptionsMenu(menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Write your logic here
                this.finish();
                return true;
            case   R.id.action_delete:
                db.deleteTag(tag, true);
                Mainmenu();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void Mainmenu() {
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
    }
    private void addAssignmentDialog(final View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Courses");
        LinearLayout layout= new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        input.setHint("Assignment Title");
        layout.addView(input);
        final EditText input1 = new EditText(this);
        input1.setHint("Score (0-100)");
        input1.setInputType(InputType.TYPE_CLASS_NUMBER);
        input1.setFilters(new InputFilter[] { new InputFilter.LengthFilter(3) });
        layout.addView(input1);
        builder.setView(layout);
// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if ((Integer.parseInt(input1.getText().toString()) >=0 && Integer.parseInt(input1.getText().toString()) <=100)  &&input.getText().toString() != "") {
//                        courseList.add(input.getText().toString() + "\n" + input1.getText().toString());

                        CoursesSql todo1 = new CoursesSql(input.getText().toString() + "\n" + input1.getText().toString()+"%",Assg );
                        // Inserting tags in db
//                        long tag1_id = db.createTag(tag);

                        // Inserting todos under "Shopping" Tag
                        long todo1_id = db.createToDo(todo1, new long[] { tag.getId() });

                        tagsWatchList = db.getAllToDosByTag(tag.getAssignmentName());
                        courseList.clear();
                        for (CoursesSql todo : tagsWatchList) {
                            Log.d("ToDo Watchlist", todo.getCourseName());
                            courseList.add(todo.getCourseName());
                        }

                        Snackbar.make(view, "Assignment Saved", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }else{
                        Toast.makeText(context, "Invalid Input", Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception e){
                    Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                adapter.notifyDataSetChanged();
                listView = findViewById(R.id.listView2);
                listView.setAdapter(adapter);

            }
        });

        builder.show();
    }


}
