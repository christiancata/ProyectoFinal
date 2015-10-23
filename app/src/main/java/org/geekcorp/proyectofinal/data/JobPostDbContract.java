package org.geekcorp.proyectofinal.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Christian Cata on 14/10/2015.
 */
public class JobPostDbContract
{
    public static final String CONTENT_AUTHORITY = "org.geekcorp.proyectofinal";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String JOBS_PATH = "jobs";
    public static final String CONTACTS_PATH = "contacts";

    public static class JobEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(JOBS_PATH).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + JOBS_PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + JOBS_PATH;

        public static String TABLE_NAME = "job";
        public static String COLUMN_TITLE = "title";
        public static String COLUMN_DESCRIPTION = "description";
        public static String COLUMN_POSTED_DATE = "posted_date";
    }

    public static class ContactEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(CONTACTS_PATH).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + CONTACTS_PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + CONTACTS_PATH;

        public static String TABLE_NAME = "contact";
        public static String COLUMN_JOB_ID = "job_id";
        public static String COLUMN_NUMBER = "number";
    }
}