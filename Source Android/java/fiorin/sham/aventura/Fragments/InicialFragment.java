package fiorin.sham.aventura.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import fiorin.sham.aventura.Model.Banco;
import fiorin.sham.aventura.R;


/**
 * Created by SHAMVINICIUSFIORIN on 26/10/2017.
 */

public class InicialFragment extends Fragment {

    private View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.inicial_layout, container, false );
        return myView;
    }
}
