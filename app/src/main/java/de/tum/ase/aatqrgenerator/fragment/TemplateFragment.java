package de.tum.ase.aatqrgenerator.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.tum.ase.aatqrgenerator.R;

/**
 * Created by Dat on 15.1.2017.
 */

public class TemplateFragment extends Fragment {

    public TemplateFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_template, container, false);
    }
}
