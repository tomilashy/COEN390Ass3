package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static DatabaseHelper db;
    Context context = MainActivity.this;
    ListView listView;
    List<String> courseList = new ArrayList<>();
    ArrayAdapter adapter;
    List<AssignmentSql> allTags; //list of course
    double overallAvg=0,count=0;
    TextView textView ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new DatabaseHelper(context);
        textView=findViewById(R.id.textView1);


        // Creating tags
//        AssignmentSql tag1 = new AssignmentSql("Shopping");
//        // Inserting tags in db
//        long tag1_id = db.createTag(tag1);
        allTags = db.getAllTags();
        courseList.clear();
        overallAvg=0;
        count=0;
        for (AssignmentSql tag : allTags) {
//            Log.d("Tag Name", tag.getAssignmentName());
            courseList.add(tag.getAssignmentName() + "\nAverage assignment: " + AssignmentAverage(tag));
            if(AssignmentAverage(tag)!=0) {
                count++;
                overallAvg += AssignmentAverage(tag);
            }
        }
        textView.setText("Average of all Assignment: "+(overallAvg/count));

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
                    AssignmentViewer(o.toString().split("\n")[0] + "\t\t" + o.toString().split("\n")[1]);

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
                AssignmentSql tag1 = new AssignmentSql(input.getText().toString() + "\n" + input1.getText().toString());
                // Inserting tags in db
                long tag1_id = db.createTag(tag1);
                tag1.setId(tag1_id);
                courseList.clear();
                overallAvg=0;
                count=0;
                for (AssignmentSql tag : allTags) {
//            Log.d("Tag Name", tag.getAssignmentName());
                    courseList.add(tag.getAssignmentName() + "\nAverage assignment: " + AssignmentAverage(tag));
                    if(AssignmentAverage(tag)!=0) {
                        count++;
                        overallAvg += AssignmentAverage(tag);
                    }
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
                listView = findViewById(R.id.listView1);
                listView.setAdapter(adapter);
//                Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }


    public void AssignmentViewer(String title) {
        Intent intent = new Intent(this, AssignmentActivity.class);
        intent.putExtra("Title", title);

        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        courseList.clear();
        overallAvg=0;
        count=0;
        for (AssignmentSql tag : allTags) {
//            Log.d("Tag Name", tag.getAssignmentName());
            courseList.add(tag.getAssignmentName() + "\nAverage assignment: " + AssignmentAverage(tag));
            if(AssignmentAverage(tag)!=0) {
                count++;
                overallAvg += AssignmentAverage(tag);
            }
        }
        textView.setText("Average of all Assignment: "+(overallAvg/count));
        adapter.notifyDataSetChanged();
        listView = findViewById(R.id.listView1);
        listView.setAdapter(adapter);
    }

    public double AssignmentAverage(AssignmentSql tag) {
        double avg = 0;
        List<CoursesSql> tagsWatchList = db.getAllToDosByTag(tag.getAssignmentName());
        if (tagsWatchList.size() != 0) {
            for (CoursesSql todo : tagsWatchList) {
                avg += Integer.parseInt(todo.getCourseName().split("\n")[1].split("%")[0]);
            }
            avg /= tagsWatchList.size();
        }

        return avg;
    }

}
