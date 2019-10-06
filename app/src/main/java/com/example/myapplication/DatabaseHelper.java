package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Course List";

    // Table Names
    private static final String TABLE_COURSES = "Course";
    private static final String TABLE_ASSG = "Assignment";
    private static final String TABLE_COURSE_ASSG = "Course_Assg";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    // COURSES Table - column names
    private static final String KEY_COURSE = "Course";
    private static final String KEY_STATUS = "status";

    // Assignment Table - column names
    private static final String KEY_ASSIGNMENT = "Assignment";

    // COURSE_ASSG Table - column names
    private static final String KEY_COURSE_ID = "Course_id";
    private static final String KEY_ASSG_ID = "Assignment_id";

    // Table Create Statements
    // CoursesSql table create statement
    private static final String CREATE_TABLE_COURSES = "CREATE TABLE "
            + TABLE_COURSES + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_COURSE
            + " TEXT," + KEY_STATUS + " INTEGER," + KEY_CREATED_AT
            + " DATETIME" + ")";

    // AssignmentSql table create statement
    private static final String CREATE_TABLE_ASSG = "CREATE TABLE " + TABLE_ASSG
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ASSIGNMENT + " TEXT,"
            + KEY_CREATED_AT + " DATETIME" + ")";

    // todo_tag table create statement
    private static final String CREATE_TABLE_COURSE_ASSG = "CREATE TABLE "
            + TABLE_COURSE_ASSG + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_COURSE_ID + " INTEGER," + KEY_ASSG_ID + " INTEGER,"
            + KEY_CREATED_AT + " DATETIME" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_COURSES);
        db.execSQL(CREATE_TABLE_ASSG);
        db.execSQL(CREATE_TABLE_COURSE_ASSG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE_ASSG);

        // create new tables
        onCreate(db);
    }


    /*
     * Creating a course
     */
    public long createToDo(CoursesSql coursesSql, long[] tag_ids) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_COURSE, coursesSql.getCourseName());
        values.put(KEY_STATUS, coursesSql.getStatus());
        values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        long todo_id = db.insert(TABLE_COURSES, null, values);

        // assigning tags to todo
        for (long tag_id : tag_ids) {
            createTodoTag(todo_id, tag_id);
        }

        return todo_id;
    }

    /*
     * get single Course
     */
    public CoursesSql getCourse(long todo_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_COURSES + " WHERE "
                + KEY_ID + " = " + todo_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        CoursesSql td = new CoursesSql();
        td.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        td.setCourseName((c.getString(c.getColumnIndex(KEY_COURSE))));
        td.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

        return td;
    }

    /*
     * getting all todos
     * */
    public List<CoursesSql> getAllCourses() {
        List<CoursesSql> todos = new ArrayList<CoursesSql>();
        String selectQuery = "SELECT  * FROM " + TABLE_COURSES;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                CoursesSql td = new CoursesSql();
                td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                td.setCourseName((c.getString(c.getColumnIndex(KEY_COURSE))));
                td.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

                // adding to todo list
                todos.add(td);
            } while (c.moveToNext());
        }

        return todos;
    }

    /*
     * getting all todos under single tag
     * */
    public List<CoursesSql> getAllToDosByTag(String tag_name) {
        List<CoursesSql> todos = new ArrayList<CoursesSql>();

        String selectQuery = "SELECT  * FROM " + TABLE_COURSES + " td, "
                + TABLE_ASSG + " tg, " + TABLE_COURSE_ASSG + " tt WHERE tg."
                + KEY_ASSIGNMENT + " = '" + tag_name + "'" + " AND tg." + KEY_ID
                + " = " + "tt." + KEY_ASSG_ID + " AND td." + KEY_ID + " = "
                + "tt." + KEY_COURSE_ID;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                CoursesSql td = new CoursesSql();
                td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                td.setCourseName((c.getString(c.getColumnIndex(KEY_COURSE))));
                td.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

                // adding to todo list
                todos.add(td);
            } while (c.moveToNext());
        }

        return todos;
    }

    /**
     * getting todo count
     */
    public int getToDoCount() {
        String countQuery = "SELECT  * FROM " + TABLE_COURSES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    /**
     * Updating a todo
     */
    public int updateToDo(CoursesSql todo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_COURSE, todo.getCourseName());
        values.put(KEY_STATUS, todo.getStatus());

        // updating row
        return db.update(TABLE_COURSES, values, KEY_ID + " = ?",
                new String[] { String.valueOf(todo.getId()) });
    }

    /**
     * Deleting a todo
     */
    public void deleteToDo(long tado_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COURSES, KEY_ID + " = ?",
                new String[] { String.valueOf(tado_id) });
    }

    // ------------------------ "tags" table methods ----------------//

    /**
     * Creating tag
     */
    public long createTag(AssignmentSql tag) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ASSIGNMENT, tag.getAssignmentName());
        values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        long tag_id = db.insert(TABLE_ASSG, null, values);

        return tag_id;
    }

    /**
     * getting all tags
     * */
    public List<AssignmentSql> getAllTags() {
        List<AssignmentSql> tags = new ArrayList<AssignmentSql>();
        String selectQuery = "SELECT  * FROM " + TABLE_ASSG;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                AssignmentSql t = new AssignmentSql();
                t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                t.setAssignmentName(c.getString(c.getColumnIndex(KEY_ASSIGNMENT)));

                // adding to tags list
                tags.add(t);
            } while (c.moveToNext());
        }
        return tags;
    }

    /**
     * Updating a tag
     */
    public int updateTag(AssignmentSql tag) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ASSIGNMENT, tag.getAssignmentName());

        // updating row
        return db.update(TABLE_ASSG, values, KEY_ID + " = ?",
                new String[] { String.valueOf(tag.getId()) });
    }

    /**
     * Deleting a tag
     */
    public void deleteTag(AssignmentSql tag, boolean should_delete_all_tag_todos) {
        SQLiteDatabase db = this.getWritableDatabase();

        // before deleting tag
        // check if todos under this tag should also be deleted
        if (should_delete_all_tag_todos) {
            // get all todos under this tag
            List<CoursesSql> allTagToDos = getAllToDosByTag(tag.getAssignmentName());

            // delete all todos
            for (CoursesSql todo : allTagToDos) {
                // delete todo
                deleteToDo(todo.getId());
            }
        }

        // now delete the tag
        db.delete(TABLE_ASSG, KEY_ID + " = ?",
                new String[] { String.valueOf(tag.getId()) });
    }

    // ------------------------ "todo_tags" table methods ----------------//

    /**
     * Creating todo_tag
     */
    public long createTodoTag(long todo_id, long tag_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_COURSE_ID, todo_id);
        values.put(KEY_ASSG_ID, tag_id);
        values.put(KEY_CREATED_AT, getDateTime());

        long id = db.insert(TABLE_COURSE_ASSG, null, values);

        return id;
    }

    /**
     * Updating a todo tag
     */
    public int updateNoteTag(long id, long tag_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ASSG_ID, tag_id);

        // updating row
        return db.update(TABLE_COURSES, values, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }

    /**
     * Deleting a todo tag
     */
    public void deleteToDoTag(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COURSES, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    /**
     * get datetime
     * */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


}