package org.geekcorp.proyectofinal;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Christian Cata on 14/10/2015.
 */
public class JobAdapter extends CursorAdapter
{

    public JobAdapter(Context context, Cursor c, boolean autoRequery)
    {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        return LayoutInflater.from(context).inflate(R.layout.job_layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        TextView textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        TextView textViewPosDate = (TextView) view.findViewById(R.id.textViewPosDate);
        textViewTitle.setText(cursor.getString(1));
        textViewPosDate.setText(cursor.getString(2));
    }
}