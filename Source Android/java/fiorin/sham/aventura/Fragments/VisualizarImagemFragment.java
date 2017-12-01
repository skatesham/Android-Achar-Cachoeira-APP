package fiorin.sham.aventura.Fragments;

import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import fiorin.sham.aventura.Model.Banco;
import fiorin.sham.aventura.R;
import fiorin.sham.aventura.View.ZoomableImageView;


/**
 * Created by SHAMVINICIUSFIORIN on 26/10/2017.
 */

public class VisualizarImagemFragment extends Fragment {

    private int id;
    private View myView;
    private Banco banco;
    private ImageView imageView;
    private Boolean isMapa;
    private int idFoto;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.visualizar_layout, container, false);
        banco = Banco.getInstance();
        imageView = (ImageView) myView.findViewById(R.id.foto);
        Bundle bundle = getArguments();
        int tipo = bundle.getInt("tipo");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("fotos");
        switch (tipo){
            case 1:
                imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mapavisual));
                break;
            case 2:
                id = bundle.getInt("id");
                idFoto = bundle.getInt("idFoto");
                imageView.setImageBitmap(banco.buscaFoto(id, idFoto));
                break;
            case 3:
                id = bundle.getInt("id");
                break;
        }
        return myView;
    }

}

