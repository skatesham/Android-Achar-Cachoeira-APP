package fiorin.sham.aventura.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by SHAMVINICIUSFIORIN on 31/08/2017.
 */

public class Banco {

    static private Banco uniqueInstance = null;
    private static String ANONYMOUS = "anonymous";
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    Usuario mUsuario;
    int gps = 0;
    List<Local> cachoeiras = null;
    List<Foto> fotosValidar = null;
    private List<Usuario> usuarios;
    private Location location;
    private String mUsername;
    private Bitmap mapa = null;

    private Banco() {
        this.firebaseStorage = FirebaseStorage.getInstance();
        this.cachoeiras = new LinkedList<>();
        mUsuario = null;
        mUsername = ANONYMOUS;
        this.fotosValidar = new LinkedList<>();
        this.usuarios = new LinkedList<>();
    }

    static public Banco getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new Banco();
        }
        return uniqueInstance;
    }

    public List<Local> getCachoeiras() {
        if (cachoeiras.isEmpty()) {
            try {
                //cachoeiras = connection.sendGet();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cachoeiras;
    }

    public void setCachoeiras(List<Local> cachoeiras) {
        this.cachoeiras = cachoeiras;
    }

    public int buscarLocalLocal(LatLng latLng) {
        int count = 0;
        for (Local l : cachoeiras) {
            if (l.getLocal().getLat() == latLng.latitude && l.getLocal().getLng() == latLng.longitude) {
                return count;
            }
            count++;
        }
        return 0;
    }

    public Bitmap buscaFoto(int id, int idFoto) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        Bitmap bitmap = null;
        if (cachoeiras != null && !cachoeiras.isEmpty()) {
            if (cachoeiras.get(id).getFotos().get(idFoto).getBitmap() == null) {
                try {
                    URL url = new URL(cachoeiras.get(id).getFotos().get(idFoto).getUrl());
                    bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    cachoeiras.get(id).getFotos().get(idFoto).setBitmap(bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Problemas lendo imagem");
                }
            }
        }
        return cachoeiras.get(id).getFotos().get(idFoto).getBitmap();
    }

    public int getGps() {
        return gps;
    }

    public void setGps(int gps) {
        this.gps = gps;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public void addLocal(Local local) {
        cachoeiras.add(local);
    }

    public Usuario getUsuario() {
        return mUsuario;
    }

    public void setUsuario(Usuario mUsuario) {
        this.mUsuario = mUsuario;
    }

    public int findIdLocalByNome(String nome) {
        for (Local l : cachoeiras) {
            if (l.getNome().equals(nome)) {
                return cachoeiras.indexOf(l);
            }
        }
        return 0;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    //Calcular Distancia de uma local
    public void calcularDistancia() {
        if (this.getCachoeiras() != null && location != null) {
            for (Local local : this.getCachoeiras()) {
                //double resultado = DistanceGPS.calculateDistance(local.getLocal().getLat(), local.getLocal().getLng(), location.getLatitude(), location.getLongitude());
                float resultados[] = new float[10];
                System.out.println((local.getLocal().getLat() + " - " + local.getLocal().getLng() + "Local :" + location.getLatitude() + " - " + location.getLongitude()));
                Location.distanceBetween(local.getLocal().getLat(), local.getLocal().getLng(), location.getLatitude(), location.getLongitude(), resultados);
                double resultado = ((double) resultados[0]);
                local.setLastDistance(resultado);
            }
        }
    }

    public Bitmap buscaFotoValidation(final int id) {
        Foto foto = fotosValidar.get(id);
        if (foto.getBitmap() == null) {
            try {
                storageReference = firebaseStorage.getReferenceFromUrl(foto.getUrl());
                final File localFile = File.createTempFile("image", "jpg");
                storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap img = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        getFotosValidar().get(id).setBitmap(img);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        exception.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("##### Problemas lendo imagem na Validation ##########" +foto.getUrl());
            }
        }
        return foto.getBitmap();
    }

    public List<Foto> getFotosValidar() {
        return fotosValidar;
    }

    public void setFotosValidar(List<Foto> fotosValidar) {
        this.fotosValidar = fotosValidar;
    }
}
