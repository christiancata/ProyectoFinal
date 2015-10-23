package org.geekcorp.proyectofinal.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.geekcorp.proyectofinal.data.JobPostDbContract.*;

/**
 * Created by USUARIO on 14/10/2015.
 */
public class JobPostDbHelper  extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "job_post.db";
    private static final int VERSION = 1;

    public JobPostDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String SQL_CREATE_JOB_TABLE = "CREATE TABLE " + JobEntry.TABLE_NAME + "("
                + JobEntry._ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, "
                + JobEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + JobEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                + JobEntry.COLUMN_POSTED_DATE + " TEXT NOT NULL);";
        final String SQL_CREATE_CONTACT_TABLE = "CREATE TABLE " + ContactEntry.TABLE_NAME + "("
                + ContactEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + ContactEntry.COLUMN_JOB_ID + " INTEGER NOT NULL, "
                + ContactEntry.COLUMN_NUMBER + " TEXT NOT NULL, "
                + "FOREIGN KEY (" + ContactEntry.COLUMN_JOB_ID + ") REFERENCES "
                + JobEntry.TABLE_NAME + " (" + JobEntry._ID + "), "
                + "UNIQUE (" + ContactEntry.COLUMN_JOB_ID + ", "
                + ContactEntry.COLUMN_NUMBER + ") ON CONFLICT REPLACE);";
        db.execSQL(SQL_CREATE_JOB_TABLE);
        db.execSQL(SQL_CREATE_CONTACT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + ContactEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + JobEntry.TABLE_NAME);
        onCreate(db);
    }
}