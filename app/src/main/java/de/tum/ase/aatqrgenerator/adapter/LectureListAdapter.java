package de.tum.ase.aatqrgenerator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.tum.ase.aatqrgenerator.R;
import de.tum.ase.aatqrgenerator.model.Lecture;

public class LectureListAdapter extends ArrayAdapter<Lecture> {
    private Context context;

    public LectureListAdapter(Context context, int resource, List<Lecture> items) {
        super(context, resource, items);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.lecture_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.lecture_title);
        textView.setText(getItem(position).title);

        TextView semesterView = (TextView) rowView.findViewById(R.id.lecture_semester);
        semesterView.setText(getItem(position).semester.title);

        return rowView;
    }
}
