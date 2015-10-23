package org.geekcorp.proyectofinal;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class JobPostActivity extends AppCompatActivity
{
    private final String LOG_TAG = JobPostActivity.class.getSimpleName();
    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextContact;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_post);
        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);
        editTextContact = (EditText) findViewById(R.id.editTextContact);
    }

    public void buttonPost_Click(View view)
    {
        if (validate())
        {
            ArrayList<String> params = new ArrayList<String>();
            params.add(editTextTitle.getText().toString().trim());
            params.add(editTextDescription.getText().toString().trim());
            params.add(editTextContact.getText().toString().trim());
            JobPostAsyncTask jobPostAsyncTask = new JobPostAsyncTask();
            jobPostAsyncTask.execute(params);
        }
    }

    private boolean validate()
    {
        if (!editTextTitle.getText().toString().trim().equals(""))
        {
            if (!editTextDescription.getText().toString().trim().equals(""))
            {
                if(!editTextContact.getText().toString().trim().equals(""))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    private class JobPostAsyncTask extends AsyncTask<ArrayList<String>, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(ArrayList<String>... params)
        {
            boolean result = false;
            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;
            Uri uri = Uri.parse("http://dipandroid-ucb.herokuapp.com").buildUpon()
                    .appendPath("work_posts.json").build();
            try
            {
                URL url = new URL(uri.toString());
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.addRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                JSONArray contacts = new JSONArray();
                JSONObject job = new JSONObject();
                JSONObject workPost = new JSONObject();
                job.put("title", params[0].get(0));
                job.put("description", params[0].get(1));
                contacts.put(params[0].get(2));
                job.put("contacts", contacts);
                workPost.put("work_post", job);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream());
                outputStreamWriter.write(workPost.toString());
                outputStreamWriter.flush();

                int HttpResult = httpURLConnection.getResponseCode();
                if(HttpResult == HttpURLConnection.HTTP_CREATED)
                {
                    result = true;
                }
                else
                {
                    Log.w(LOG_TAG, httpURLConnection.getResponseMessage());
                }
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
                if (httpURLConnection != null)
                {
                    httpURLConnection.disconnect();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            if (result)
            {
                Toast.makeText(JobPostActivity.this, getString(R.string.job_posted_success),
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(JobPostActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            else
            {
                Toast.makeText(JobPostActivity.this, getString(R.string.job_posted_error),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}