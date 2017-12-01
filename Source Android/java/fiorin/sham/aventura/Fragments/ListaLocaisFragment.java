package fiorin.sham.aventura.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import fiorin.sham.aventura.Model.Banco;
import fiorin.sham.aventura.Model.Local;
import fiorin.sham.aventura.R;

/**
 * Created by SHAMVINICIUSFIORIN on 30/10/2017.
 */

public class ListaLocaisFragment extends Fragment implements View.OnClickListener {

    private View myView;
    private Banco banco;

    private LinearLayout linearLayoutCachu;
    private LinearLayout linearLayoutAtra;
    private  LinearLayout.LayoutParams layout;
    private LinearLayout.LayoutParams layoutMatchWrap;
    private RadioButton radioCachoeria;
    private RadioButton radioAtrativo;
    private ProgressBar progressBar;
    private View.OnClickListener onClickListenerLocal;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.lista_locais_layout, container, false);
        banco = Banco.getInstance();
        //progressBar = (ProgressBar) myView.findViewById(R.id.progressBarListaLocal);
        linearLayoutCachu = (LinearLayout) myView.findViewById(R.id.linear_layout_cachoeira);
        linearLayoutAtra = (LinearLayout) myView.findViewById(R.id.linear_layout_atrativo);

        radioAtrativo = (RadioButton) myView.findViewById(R.id.radio_atrativo);
        radioCachoeria = (RadioButton) myView.findViewById(R.id.radio_cachoeira);
        radioAtrativo.setChecked(false);
        radioCachoeria.setChecked(false);
        radioAtrativo.setOnClickListener(this);
        radioCachoeria.setOnClickListener(this);
        layout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutMatchWrap = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        onClickListenerLocal = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //progressBar.setVisibility(View.VISIBLE);
                //progressBar.bringToFront();
                InfoFragment info = new InfoFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("id", v.getId());
                info.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.content_frame, info).addToBackStack(null).commit();
            }
        };

        adicionarLocais();
        return myView;

    }

    private void adicionarLocais() {
        linearLayoutAtra.removeAllViews();
        linearLayoutCachu.removeAllViews();
        List<Local> listaCachu = new LinkedList<>();
        List<Local> listaAtra = new LinkedList<>();
        if (!banco.getCachoeiras().isEmpty()) {
            for (Local l : banco.getCachoeiras()) {
                if (l.getTipo() == 0) {
                    listaCachu.add(l);
                } else if (l.getTipo() == 1) {
                    listaAtra.add(l);
                }
            }
        }
        //Ordenando Lista de Locais
        Collections.sort(listaCachu);
        Collections.sort(listaAtra);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8,8,8,8);
        for (Local l : listaCachu) {
            LinearLayout lin = adicionarLocal(l);
            lin.setOnClickListener(onClickListenerLocal);
            linearLayoutCachu.addView(lin, layoutParams);
        }
        for (Local l : listaAtra) {
            LinearLayout lin = adicionarLocal(l);
            lin.setOnClickListener(onClickListenerLocal);
            linearLayoutAtra.addView(lin, layoutParams);
        }
    }

    public int trocarCorLacuna(Local local) {
        switch (local.getDificuldade()) {
            case 1:
                return getResources().getColor(R.color.facil, null);
            case 2:
                return getResources().getColor(R.color.medio, null);
            case 3:
                return getResources().getColor(R.color.dificil, null);
        }
        return getResources().getColor(R.color.white, null);
    }

    private LinearLayout adicionarLocal(Local local) {

        LinearLayout lacuna = new LinearLayout(getActivity());
        TextView textoNome = new TextView(getActivity());
        TextView textoDificuldade = new TextView(getActivity());
        TextView textoDistancia = new TextView(getActivity());
        Button btn = new Button(getActivity());
        LinearLayout lacunaInfo = new LinearLayout(getActivity());
        LinearLayout lacunaTexto = new LinearLayout(getActivity());

        lacunaInfo.setOrientation(LinearLayout.HORIZONTAL);
        lacunaTexto.setOrientation(LinearLayout.VERTICAL);
        String distacia = local.getDistanciaTotal()+"";
        Double distanciaReal = local.getLastDistance();
        if(distanciaReal != null && (distanciaReal.intValue() != 0)){
            //distanciaReal /= 1000;
            distacia = String.format("%.0f ", distanciaReal);
        }
        distacia += " KM";
        textoDistancia.setText(distacia);
        textoDistancia.setGravity(View.TEXT_ALIGNMENT_CENTER);
        textoDistancia.setTextColor(getResources().getColor(R.color.textoNormal, null));

        textoDificuldade.setText(local.getDificuldadeString());
        textoDificuldade.setTextColor(trocarCorLacuna(local));

        textoNome.setText(local.getNome());
        textoNome.setTextSize(20);
        textoNome.setGravity(Gravity.LEFT);
        textoNome.setTextColor(getResources().getColor(R.color.textoNormal, null));

        btn.setText("Ir");
        btn.setVisibility(View.INVISIBLE);
        btn.setId(banco.getCachoeiras().indexOf(local));
        lacuna.setId(banco.getCachoeiras().indexOf(local));
        btn.setBackgroundColor(getResources().getColor(R.color.colorButton, null));
        btn.setOnClickListener(onClickListenerLocal);
        layoutMatchWrap.setMargins(8,8,8,8);
        lacunaInfo.addView(textoDificuldade, new LinearLayout.LayoutParams(150, ViewGroup.LayoutParams.WRAP_CONTENT));
        lacunaInfo.addView(textoDistancia, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        lacunaTexto.addView(textoNome, layoutMatchWrap);
        lacunaTexto.addView(lacunaInfo, layoutMatchWrap);
        lacuna.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(520, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8,8,8,8);
        lacuna.addView(lacunaTexto, layoutParams);

        layout.setMargins(8,8,8,8);
        lacuna.addView(btn, layout);
        lacuna.setBackgroundColor(getResources().getColor(R.color.background_special, null));
        return lacuna;
    }

    @Override
    public void onClick(View v) {
        if(banco.getCachoeiras() == null){
            Toast.makeText(getActivity(), "Fazendo Download dos locais", Toast.LENGTH_SHORT).show();
        }
        banco.calcularDistancia();
        Boolean checked = ((RadioButton) v).isChecked();
        switch (v.getId()) {
            case R.id.radio_cachoeira:
                if (checked) {
                    adicionarLocais();
                    linearLayoutAtra.setVisibility(View.GONE);
                    linearLayoutCachu.setVisibility(View.VISIBLE);
                    //Toast.makeText(getActivity(), "Tentativa de Trocar para Cachoeira", Toast.LENGTH_SHORT).show();
                    break;
                }
            case R.id.radio_atrativo:
                if (checked) {
                    adicionarLocais();
                    linearLayoutCachu.setVisibility(View.GONE);
                    linearLayoutAtra.setVisibility(View.VISIBLE);
                    //Toast.makeText(getActivity(), "Tentativa de Trocar para Atrativo", Toast.LENGTH_SHORT).show();
                    break;
                }
        }
    }
}

