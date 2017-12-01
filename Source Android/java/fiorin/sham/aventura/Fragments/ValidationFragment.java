package fiorin.sham.aventura.Fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import fiorin.sham.aventura.Model.Banco;
import fiorin.sham.aventura.Model.Foto;
import fiorin.sham.aventura.R;
import fiorin.sham.aventura.View.ZoomableImageView;

/**
 * Created by sham on 30/11/17.
 */

public class ValidationFragment extends Fragment implements View.OnClickListener {

    private View view;
    private LinearLayout layout;
    private LinearLayout.LayoutParams lp;
    private LinearLayout.LayoutParams lpTexto;
    private Banco banco;
    private ZoomableImageView imageButton;
    private LinearLayout imgLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.validation_layout, container, false);
        banco = Banco.getInstance();
        layout = (LinearLayout) view.findViewById(R.id.validationLayout);
        imageButton = (ZoomableImageView) view.findViewById(R.id.fotoImgButton);
        //imageButton.setOnClickListener(this);
        imgLayout = (LinearLayout) view.findViewById(R.id.imgLayout);
        this.popularLayout();
        return view;

    }

    public void popularLayout(){
        List<Foto> fotoList = banco.getFotosValidar();
        if(fotoList != null){
            for(Foto f : fotoList){
                adicionarLayout(f);
            }
        }
    }

    public void adicionarLayout(Foto foto){
        LinearLayout linearLayout = new LinearLayout(getActivity());
        TextView textView = new TextView(getActivity());
        lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(8,8,8,8);
        lp.weight = 1;
        textView.setText(foto.getNome());
        textView.setTextColor(getResources().getColor(R.color.textoNormal, null));
        Button button = new Button(getActivity());
        int id = banco.getFotosValidar().indexOf(foto);
        textView.setId(id);
        textView.setTextSize(22);
        textView.setTag("txt");
        button.setId(id);
        button.setText("Validar");
        button.setTag("btn");
        button.setVisibility(View.GONE);
        button.setTextColor(getResources().getColor(R.color.textoNormal, null));
        textView.setOnClickListener(this);
        button.setOnClickListener(this);
        button.setBackgroundColor(getResources().getColor(R.color.colorButton, null));
        linearLayout.addView(textView, lp);
        linearLayout.addView(button, lp);
        layout.addView(linearLayout, lp);
    }

    @Override
    public void onClick(View view) {
        if(view.getTag().equals("btn")){
            /*VisualizarImagemFragment visualisar = new VisualizarImagemFragment();
            Bundle bundle = new Bundle();
            int VALIDAR = 3;
            bundle.putInt("id", view.getId());
            bundle.putInt("tipo", VALIDAR);
            visualisar.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, visualisar).addToBackStack(null).commit();*/

        } else if(view.getTag().equals("txt")){
            layout.setVisibility(View.GONE);
            imgLayout.setVisibility(View.VISIBLE);
            Bitmap foto = banco.buscaFotoValidation(view.getId());
            if(foto != null ){
                imageButton.setImageBitmap(foto);
            }
        }
        switch (view.getId()){
            case R.id.fotoImgButton:
                imgLayout.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
                break;
        }
    }
}

