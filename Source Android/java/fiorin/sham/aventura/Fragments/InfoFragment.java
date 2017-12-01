package fiorin.sham.aventura.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Locale;

import fiorin.sham.aventura.Model.Banco;
import fiorin.sham.aventura.Model.EnumPapeis;
import fiorin.sham.aventura.Model.Foto;
import fiorin.sham.aventura.Model.Local;
import fiorin.sham.aventura.Model.Papel;
import fiorin.sham.aventura.R;

/**
 * Created by SHAMVINICIUSFIORIN on 30/10/2017.
 */

public class InfoFragment extends Fragment implements View.OnClickListener{

    private final int RC_PHOTO_PICKER = 2;

    private View myView;
    private int id;
    private Banco banco = null;
    private ImageView imageView;
    private TextView nomeCachoeira;
    private TextView distancia;
    private TextView guia;
    private TextView carro;
    private TextView dificuldade;
    private Button buttomIr;
    private FirebaseStorage mFirebaseStorage;
    private FirebaseDatabase mFirebaseDatabase;
    private Button adicionarFotos;
    private DatabaseReference fotosReference;
    private Local local;
    private ProgressBar progressBar;
    private TextView nofiticacaoProgressText;
    private TextView progressText;
    private TextView caminhada;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        myView = inflater.inflate(R.layout.info_layout, container, false );
        Bundle bundle = getArguments();
        id = bundle.getInt("id");

        banco = Banco.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        fotosReference = mFirebaseDatabase.getReference().child("fotos");

        adicionarFotos = (Button) myView.findViewById(R.id.buttonAdicionarFoto);
        nomeCachoeira = (TextView) myView.findViewById(R.id.textNome);
        progressBar = (ProgressBar) myView.findViewById(R.id.barraProgresso);
        nofiticacaoProgressText = (TextView) myView.findViewById(R.id.textoProgresoNotificacao);
        progressText = (TextView) myView.findViewById(R.id.textoProgreso);
        imageView = (ImageView) myView.findViewById(R.id.imgLocal);
        distancia = (TextView) myView.findViewById(R.id.distanciaI);
        dificuldade = (TextView) myView.findViewById(R.id.dificuldadeI);
        caminhada = (TextView) myView.findViewById(R.id.caminhadaI);
        guia = (TextView) myView.findViewById(R.id.guiaI);
        carro = (TextView) myView.findViewById(R.id.carroI);

        buttomIr = (Button) myView.findViewById(R.id.buttonEncontrar);
        local = banco.getCachoeiras().get(id);
        if(local != null){
            setInformacoes();
        }
        funcaoAtivar();
        return myView;
    }

    private void setInformacoes(){
        nomeCachoeira.setText(local.getNome());
        imageView.setImageBitmap(banco.buscaFoto(id, 0));
        guia.setText(local.getGuiaString());
        //guia.setBackgroundColor(trocarCorLacuna(local.getGuia()));
        dificuldade.setText(local.getDificuldadeString());
        //dificuldade.setBackgroundColor(trocarCorLacuna(local.getDificuldade()));
        distancia.setText(local.getDistanciaString());
        //distancia.setText(local.getLastDistance()+" Km");
        caminhada.setText(local.getCaminhadaString());
        carro.setText(local.getCarro4X4String());
    }

    public int trocarCorLacuna(int valor) {
        switch (valor) {
            case 1:
                return getResources().getColor(R.color.facil, null);
            case 2:
                return getResources().getColor(R.color.medio, null);
            case 3:
                return getResources().getColor(R.color.dificil, null);
        }
        return getResources().getColor(R.color.white, null);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        switch (v.getId()){
            case R.id.buttonEncontrar:
                MapaGeralFragment mapa = new MapaGeralFragment();
                mapa.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.content_frame, mapa).addToBackStack(null).commit();
                break;

/*            case R.id.imgLocal:
                VisualizarImagemFragment visualisar = new VisualizarImagemFragment();
                bundle.putBoolean("isMapa", false);
                visualisar.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.content_frame, visualisar).addToBackStack(null).commit();
                break;*/
            case R.id.imgLocal:
                GaleriaFragment galeria = new GaleriaFragment();
                bundle.putBoolean("isMapa", false);
                galeria.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.content_frame, galeria).addToBackStack(null).commit();
                break;

            case R.id.buttonAdicionarFoto:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_PHOTO_PICKER){
            if(resultCode == Activity.RESULT_OK){
                funcaoEspera();
                Uri selectImg = data.getData();

                StorageReference referenceFoto = mFirebaseStorage.getReference().child("fotos").child(id+"");
                UploadTask uploadTask = referenceFoto.putFile(selectImg);
                uploadTask.addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri imgUpload = taskSnapshot.getDownloadUrl();
                        Toast.makeText(getActivity(), "UPLOAD CONCLUIDO!", Toast.LENGTH_SHORT).show();
                        Foto novaFoto = new Foto(local.getNome(),imgUpload.toString(), banco.getUsername(), false);
                        novaFoto.setId(id);
                        fotosReference.push().setValue(novaFoto);

                    }
                });
                uploadTask.addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Falha ao adicionar", Toast.LENGTH_SHORT).show();
                    }
                });
                uploadTask.addOnProgressListener(getActivity(), new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() ) / taskSnapshot.getTotalByteCount();
                        System.out.println("Progresso em : "+progress+"%.");
                        String texto = String.format(Locale.ENGLISH, "%.1f", progress)+"%";
                        progressText.setText(texto);
                        //progressBar.setProgress((int) progress);

                    }
                });
                uploadTask.addOnCompleteListener(getActivity(), new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        funcaoAtivar();
                    }
                });
            }

        }
    }

    private void funcaoEspera(){
        buttomIr.setOnClickListener(null);
        adicionarFotos.setOnClickListener(null);
        imageView.setOnClickListener(null);
        nofiticacaoProgressText.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
    }

    private void funcaoAtivar(){
        buttomIr.setOnClickListener(this);
        adicionarFotos.setOnClickListener(this);
        imageView.setOnClickListener(this);
        nofiticacaoProgressText.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        progressText.setVisibility(View.INVISIBLE);
    }
}


