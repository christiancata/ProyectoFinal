package org.geekcorp.proyectofinal;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.geekcorp.proyectofinal.data.JobPostDbContract.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>
{
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private JobAdapter jobAdapter;
    private static final int JOBS_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        jobAdapter = new JobAdapter(this, null, false);
        ListView listViewJobs = (ListView)findViewById(R.id.listViewJobs);
        listViewJobs.setAdapter(jobAdapter);
        listViewJobs.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Cursor cursor = (Cursor)parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, JobDetailActivity.class);
                intent.putExtra("Id", cursor.getLong(0));
                intent.putExtra("Title", cursor.getString(1));
                startActivity(intent);
            }
        });
        getSupportLoaderManager().initLoader(JOBS_LOADER, null, this);
        NetworkOperationAsyncTask asyncTask = new NetworkOperationAsyncTask();
        asyncTask.execute();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        return new CursorLoader(this, JobEntry.CONTENT_URI,
                new String[]{JobEntry._ID, JobEntry.COLUMN_TITLE, JobEntry.COLUMN_POSTED_DATE},
                null, null, JobEntry.COLUMN_POSTED_DATE + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        jobAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        jobAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_synchronize:
                NetworkOperationAsyncTask asyncTask = new NetworkOperationAsyncTask();
                asyncTask.execute();
                return true;
            case R.id.action_post_job:
                startActivity(new Intent(MainActivity.this, JobPostActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class NetworkOperationAsyncTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            Uri uri = Uri.parse("http://dipandroid-ucb.herokuapp.com").buildUpon()
                    .appendPath("work_posts.json").build();
            try
            {
                URL url = new URL(uri.toString());
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.addRequestProperty("Content-Type", "application/json");
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    buffer.append(line).append("\n");
                }
                saveJSONToDatabase(buffer.toString());
            }
            catch (IOException e)
            {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            catch (JSONException e)
            {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            finally
            {
                try
                {
                    if (bufferedReader != null)
                    {
                        bufferedReader.close();
                    }
                    if (httpURLConnection != null)
                    {
                        httpURLConnection.disconnect();
                    }
                }
                catch (IOException e)
                {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
            return null;
        }

        private void saveJSONToDatabase(String json) throws JSONException
        {
            JSONArray jsonArray = new JSONArray(json);
            for (int jobColumns = 0; jobColumns < jsonArray.length(); jobColumns++)
            {
                JSONObject jobPostJSON = jsonArray.getJSONObject(jobColumns);
                int id = jobPostJSON.getInt("id");
                String title = jobPostJSON.getString("title");
                String description = jobPostJSON.getString("description");
                String postedDate = jobPostJSON.getString("posted_date");
                ContentValues contentValues = new ContentValues();
                contentValues.put(JobEntry._ID, id);
                contentValues.put(JobEntry.COLUMN_TITLE, title);
                contentValues.put(JobEntry.COLUMN_DESCRIPTION, description);
                contentValues.put(JobEntry.COLUMN_POSTED_DATE, postedDate);
                getContentResolver().insert(JobEntry.CONTENT_URI, contentValues);
                JSONArray contactsJSON = jobPostJSON.getJSONArray("contacts");
                for (int contacts = 0; contacts < contactsJSON.length(); contacts++)
                {
                    String contact = contactsJSON.getString(contacts);
                    ContentValues contactContentValues = new ContentValues();
                    contactContentValues.put(ContactEntry.COLUMN_NUMBER, contact);
                    contactContentValues.put(ContactEntry.COLUMN_JOB_ID, id);
                    getContentResolver().insert(ContactEntry.CONTENT_URI, contactContentValues);
                }
            }
        }

        @Override
        protected void onPostExecute(Void result)
        {
            getSupportLoaderManager().restartLoader(JOBS_LOADER, null, MainActivity.this);
        }
    }
}