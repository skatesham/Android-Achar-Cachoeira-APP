package fiorin.sham.aventura.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import fiorin.sham.aventura.Model.Banco;
import fiorin.sham.aventura.Model.Foto;
import fiorin.sham.aventura.Model.Local;
import fiorin.sham.aventura.R;

/**
 * Created by sham on 25/11/17.
 */

public class GaleriaFragment extends Fragment implements View.OnClickListener {

    private int id;
    private int idFoto;
    private Local local;
    private View myView;
    private TextView nome;
    private TextView enviado;
    private Button proxButton;
    private Button anteButton;
    private ImageButton imgButton;
    private Banco banco;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.galeria_layout, container, false);
        Bundle b = getArguments();
        nome = (TextView) myView.findViewById(R.id.nomeLocalGaleria);
        enviado = (TextView) myView.findViewById(R.id.nomeEnviadoGaleria);
        proxButton = (Button) myView.findViewById(R.id.buttonProximo);
        anteButton = (Button) myView.findViewById(R.id.buttonAnterior);
        imgButton = (ImageButton) myView.findViewById(R.id.imageButtonGaleria);

        //Ativando Botoes
        proxButton.setOnClickListener(this);
        anteButton.setOnClickListener(this);
        imgButton.setOnClickListener(this);



        //Buscando local
        id = b.getInt("id");
        banco = Banco.getInstance();
        local = banco.getCachoeiras().get(id);

        //Atulizar Texto
        idFoto = 0;

        nome.setText(local.getNome());
        enviado.setText(local.getFotos().get(idFoto).getEnviado());

        //Visibilidade dos Botoes de acordo com Disponibilidade
        if(!banco.getCachoeiras().isEmpty()){
            imgButton.setImageBitmap(banco.buscaFoto(id, idFoto));
        }
        if(idFoto == 0){
            anteButton.setVisibility(View.INVISIBLE);
        }
        if(banco.getCachoeiras().get(id).getFotos().size() < 2){
            proxButton.setVisibility(View.INVISIBLE);
        }

        return myView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonProximo:
                idFoto++;
                imgButton.setImageBitmap(banco.buscaFoto(id, idFoto));
                if(idFoto > 0){
                    anteButton.setVisibility(View.VISIBLE);
                }

                if(idFoto == local.getFotos().size()-1){
                    proxButton.setVisibility(View.INVISIBLE);
                }
                break;

            case R.id.buttonAnterior:
                idFoto--;
                imgButton.setImageBitmap(banco.buscaFoto(id, idFoto));
                if(idFoto == 0){
                    anteButton.setVisibility(View.INVISIBLE);
                }
                if(idFoto < local.getFotos().size()){
                    proxButton.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.imageButtonGaleria:
                Bundle bundle = new Bundle();
                int FOTO = 2;
                bundle.putInt("id", id);
                getArguments().putInt("idFoto", idFoto);
                bundle.putInt("idFoto", idFoto);
                bundle.putInt("tipo", FOTO);
                VisualizarImagemFragment visualisar = new VisualizarImagemFragment();
                visualisar.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.content_frame, visualisar).addToBackStack(null).commit();
                break;
        }
    }
}
