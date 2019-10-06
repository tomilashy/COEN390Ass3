package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static DatabaseHelper db;
    Context context = MainActivity.this;
    ListView listView;
    List<String> courseList = new ArrayList<>();
    ArrayAdapter adapter;
    List<AssignmentSql> allTags; //list of course


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new DatabaseHelper(context);

        // Creating tags
//        AssignmentSql tag1 = new AssignmentSql("Shopping");
//        // Inserting tags in db
//        long tag1_id = db.createTag(tag1);
        allTags = db.getAllTags();
        courseList.clear();
        for (AssignmentSql tag : allTags) {
//            Log.d("Tag Name", tag.getAssignmentName());
            courseList.add(tag.getAssignmentName());
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCourseDialog();
            }
        });


        adapter = new ArrayAdapter<String>(this, R.layout.listview, courseList);
        listView = findViewById(R.id.listView1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                Log.d("listener", o.toString());
                try {
//                    deleteDialog(o.toString());
                    AssignmentViewer(o.toString().split("\n\n")[0] + "\t\t" + o.toString().split("\n\n")[1]);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        listView.setAdapter(adapter);
    }

    private void addCourseDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Courses");
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        input.setHint("Title");
        layout.addView(input);
        final EditText input1 = new EditText(this);
        input1.setHint("Course code");
        input1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        layout.addView(input1);
        builder.setView(layout);
// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                courseList.add(input.getText().toString()+"\n\n"+input1.getText().toString());
                AssignmentSql tag1 = new AssignmentSql(input.getText().toString() + " \n\n" + input1.getText().toString());
                // Inserting tags in db
                long tag1_id = db.createTag(tag1);
                tag1.setId(tag1_id);
                courseList.clear();
                for (AssignmentSql tag : allTags) {
//            Log.d("Tag Name", tag.getAssignmentName());
                    courseList.add(tag.getAssignmentName());
                }
                adapter.notifyDataSetChanged();
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
                listView = findViewById(R.id.listView1);
                listView.setAdapter(adapter);
                Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void AssignmentViewer(String title) {
        Intent intent = new Intent(this, AssignmentActivity.class);
        intent.putExtra("Title", title);

        startActivity(intent);
    }
    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }


}
