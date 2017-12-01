package fiorin.sham.aventura.Model;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import fiorin.sham.aventura.Fragments.InfoFragment;
import fiorin.sham.aventura.R;

/**
 * Created by sham on 19/11/17.
 */

public class ListaArray extends ArrayAdapter<Local> implements View.OnClickListener{

    LinearLayout listaLayout;
    TextView nomeLocalTextView;
    TextView dificuldadeTextView;
    TextView distanciaTextView;
    Button irButton;
    Banco banco;
    FragmentManager fragmentManager;


    public ListaArray(@NonNull Context context, int resource, @NonNull List<Local> objects, FragmentManager fragmentManager) {
        super(context, resource, objects);
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        super.getView(position, convertView, parent);
        if(convertView == null){
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_lista, parent, false);
        }

        banco = Banco.getInstance();

        listaLayout = (LinearLayout) convertView.findViewById(R.id.listaLayout);
        nomeLocalTextView = (TextView) convertView.findViewById(R.id.listaNomeLocal);
        dificuldadeTextView = (TextView) convertView.findViewById(R.id.listaDificuldade);
        distanciaTextView = (TextView) convertView.findViewById(R.id.listaDistancia);
        irButton = (Button) convertView.findViewById(R.id.listaButtonIr);

        Local l = getItem(position);

        try{
            nomeLocalTextView.setText(l.getNome());
            distanciaTextView.setText(l.getDistanciaTotal()+" KM");
            dificuldadeTextView.setText(l.getDificuldadeString());
            irButton.setTag(banco.getCachoeiras().indexOf(l));
            irButton.setOnClickListener(this);
        }catch (Exception e){
            System.out.println("Erro ao inserior os nomes no array");

        }

        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.listaButtonIr:
                InfoFragment info = new InfoFragment();
                Bundle b = new Bundle();
                b.putInt("id", (int) v.getTag());
                info.setArguments(b);
                fragmentManager.beginTransaction().replace(R.id.content_frame, info).addToBackStack(null).commit();
        }
    }
}
