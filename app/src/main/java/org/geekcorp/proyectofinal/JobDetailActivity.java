package org.geekcorp.proyectofinal;

import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import org.geekcorp.proyectofinal.data.JobPostDbContract.*;

public class JobDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final int JOB_LOADER = 0;
    private static final int CONTACTS_LOADER = 1;
    private ContactAdapter contactAdapter;
    private long jobId = 0;
    TextView textViewTitle;
    TextView textViewDescription;
    ListView listViewContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);
        jobId = getIntent().getLongExtra("Id", 0);
        setTitle(getIntent().getStringExtra("Title"));
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);
        contactAdapter = new ContactAdapter(this, null, false);
        listViewContacts = (ListView) findViewById(R.id.listViewContacts);
        listViewContacts.setAdapter(contactAdapter);
        getSupportLoaderManager().initLoader(JOB_LOADER, null, this);
        getSupportLoaderManager().initLoader(CONTACTS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        CursorLoader cursorLoader = null;
        switch (id)
        {
            case 0:
                cursorLoader = new CursorLoader(this,
                        JobEntry.CONTENT_URI.buildUpon().appendPath(Long.toString(jobId)).build(),
                        new String[]{JobEntry.COLUMN_TITLE, JobEntry.COLUMN_DESCRIPTION}, null, null,
                        null);
                break;
            case 1:
                cursorLoader = new CursorLoader(this,
                        ContactEntry.CONTENT_URI.buildUpon().appendPath(Long.toString(jobId)).build(),
                        new String[]{ContactEntry._ID, ContactEntry.COLUMN_NUMBER}, null, null, null);
                break;
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        switch (loader.getId())
        {
            case 0:
                if (data != null && data.moveToFirst())
                {
                    textViewTitle.setText(data.getString(0));
                    textViewDescription.setText(data.getString(1));
                }
                break;
            case 1:
                contactAdapter.swapCursor(data);
                    break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        switch (loader.getId())
        {
            case 0:
                textViewTitle.setText("");
                textViewDescription.setText("");
                break;
            case 1:
                contactAdapter.swapCursor(null);
                break;
        }
    }
}