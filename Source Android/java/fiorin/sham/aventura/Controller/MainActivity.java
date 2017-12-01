package fiorin.sham.aventura.Controller;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import fiorin.sham.aventura.Fragments.InicialFragment;
import fiorin.sham.aventura.Fragments.ListaLocaisFragment;
import fiorin.sham.aventura.Fragments.MapaGeralFragment;
import fiorin.sham.aventura.Fragments.OpcoesFragment;
import fiorin.sham.aventura.Fragments.VisualizarImagemFragment;
import fiorin.sham.aventura.Model.Banco;
import fiorin.sham.aventura.Model.EnumPapeis;
import fiorin.sham.aventura.Model.Foto;
import fiorin.sham.aventura.Model.Local;
import fiorin.sham.aventura.Model.Papel;
import fiorin.sham.aventura.Model.Usuario;
import fiorin.sham.aventura.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int RC_SIGN_IN = 123;
    private static String ANONYMOUS = "anonymous";
    private Usuario user;
    private LocationManager locationManager;
    private String mUsername;
    private Location location;
    private List<Usuario> usuarios;
    private ImageView img_perfil;
    private TextView perfil_nome;
    private TextView perfil_email;
    private NavigationView navigationView;
    private ProgressBar carregamentoProgressBar;
    private TextView carregamentoTextView;
    private Banco banco;
    private FragmentManager fragmentManager;
    private StorageReference storageReference;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference reference;
    private DatabaseReference referenceUsuario;
    private ChildEventListener mChildEventListener;
    private FirebaseStorage mFirebaseStorage;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Usuario usuario;
    private ChildEventListener validarChildEventListener;
    private ChildEventListener usuariosChildEventListener;
    private DatabaseReference validarFotosReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TENTATIVA CARREGAMENTO
        carregamentoTextView = (TextView) findViewById(R.id.textViewCarregamento);
        carregamentoProgressBar = (ProgressBar) findViewById(R.id.progressBarCarregamento);


        // FIREBASE CORE
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        storageReference = mFirebaseStorage.getReference();
        reference = mFirebaseDatabase.getReference().child("locais");
        referenceUsuario = mFirebaseDatabase.getReference().child("usuarios");
        validarFotosReference = mFirebaseDatabase.getReference().child("fotos");
        //FRAGMENTS
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new InicialFragment()).commit();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View navHeader = navigationView.getHeaderView(0);

        //BD LOCAL
        mUsername = ANONYMOUS;
        banco = Banco.getInstance();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Header Navegaton
        img_perfil = (ImageView) findViewById(R.id.img_perfil);
        perfil_nome = (TextView) navHeader.findViewById(R.id.perfil_1);
        perfil_email = (TextView) navHeader.findViewById(R.id.perfil_2);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);


        //SEM FUNCIONALIDADE
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //Toast.makeText(this, "Bem vindo, vamos explorar?", Toast.LENGTH_SHORT).show();

        //CRIANDO AUTENTICADOR E TELA DE AUTENTICACÃo
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    onSignedInInitialize(user.getDisplayName(), user.getEmail(), user.getPhotoUrl());
                } else {
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())).build(),
                            RC_SIGN_IN);
                }
            }
        };

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                setLocation(location);

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
               /* Intent gpsOptionsIntent = new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(gpsOptionsIntent);*/
            }
        };

// Register the listener with the Location Manager to receive location updates

        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;


        if (permissionGranted) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 10, locationListener);
            if (location != null) {
                locationManager.removeUpdates(locationListener);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    public void setLocation(Location l) {
        this.location = l;
        banco.setLocation(l);
        banco.calcularDistancia();
        Toast.makeText(this, "Local: " + l.getLatitude() + ", " + l.getLongitude(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Requisição Login
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Login com Sucesso!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Cadastre-se e comece a usar agora mesmo", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    //Tratamento de voltar de fragment
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fragmentManager.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fragmentManager.popBackStack();
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mFirebaseAuth.signOut();
            onSignedOutCleanup();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_inicio) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new InicialFragment()).commit();

        } else if (id == R.id.nav_mapa_geral) {
            trocarFragmentMapaGeral();

        } else if (id == R.id.nav_lista_locais) {
            trocarFragmentLista();

        } else if (id == R.id.nav_mapa_visual) {
            trocarFragmentMapaVisual();


        } else if (id == R.id.nav_opcoes) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new OpcoesFragment()).addToBackStack(null).commit();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void buttonMapaLocaisOnClick(View view) {
        trocarFragmentMapaGeral();
    }

    public void buttonListaLocaisOnClick(View view) {
        trocarFragmentLista();
    }

    public void buttonMapaVisualOnClick(View view) {
        trocarFragmentMapaVisual();
    }

    public void trocarFragmentLista() {
        fragmentManager.beginTransaction().replace(R.id.content_frame, new ListaLocaisFragment()).addToBackStack(null).commit();
    }

    public void trocarFragmentMapaGeral() {
        MapaGeralFragment mapa = new MapaGeralFragment();
        Bundle b = new Bundle();
        b.putBoolean("isMapa", true);
        mapa.setArguments(b);
        fragmentManager.beginTransaction().replace(R.id.content_frame, mapa).addToBackStack(null).commit();
    }

    public void trocarFragmentMapaVisual() {
        VisualizarImagemFragment visual = new VisualizarImagemFragment();
        Bundle b = new Bundle();
        int MAPA = 1;
        b.putInt("tipo", MAPA);
        visual.setArguments(b);
        fragmentManager.beginTransaction().replace(R.id.content_frame, visual).addToBackStack(null).commit();
    }

    public void onSignedInInitialize(String username, String email, Uri url) {
        mUsername = username;
        banco.setUsername(username);
        //Toast.makeText(this, "User"+username.toString()+"url"+url.toString()+"email "+email, Toast.LENGTH_SHORT).show();
        perfil_nome.setText(username);
        perfil_email.setText(email);
        attachDatabaseReadListener();
        criarUsuario(username, email, url);

    }

    public void criarUsuario(String username, String email, Uri url) {
        user = new Usuario();
        user.setNome(username);
        user.setEmail(email);
        List<Papel> papeis = new LinkedList<>();
        papeis.add(new Papel(0, EnumPapeis.USUARIO_COMUM));
        //papeis.add(new Papel(1, EnumPapeis.ADMINISTRADOR));
        user.setPapeis(papeis);

    }

    public void onSignedOutCleanup() {
        detachDatabaseListener();
        mUsername = ANONYMOUS;
        banco.setUsuario(null);
        banco.setUsername(ANONYMOUS);
        Toast.makeText(this, "Até logo!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Local local = dataSnapshot.getValue(Local.class);
                    banco.addLocal(local);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Local local = dataSnapshot.getValue(Local.class);
                    int index = (banco.getCachoeiras().indexOf(local));
                    banco.getCachoeiras().set(index, local);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            reference.addChildEventListener(mChildEventListener);

            if (usuariosChildEventListener == null) {
                usuariosChildEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Usuario u = dataSnapshot.getValue(Usuario.class);
                        banco.getUsuarios().add(u);
                        if (u.getNome().equals(mUsername)) {
                            usuario = u;
                            banco.setUsuario(u);
                        }
                        usuarios = banco.getUsuarios();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Usuario u = dataSnapshot.getValue(Usuario.class);
                        int index = (banco.getUsuarios().indexOf(u));
                        banco.getUsuarios().set(index, u);
                        if (u == banco.getUsuario()) {
                            Toast.makeText(MainActivity.this, "Conta de Usuario Foi alterada", Toast.LENGTH_SHORT).show();
                        }
                        if (!dataSnapshot.hasChild(mUsername)) {
                            if (usuario == null) {
                                referenceUsuario.child(mUsername).setValue(user);
                                banco.setUsuario(user);
                                usuario = user;
                            }

                        }
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                referenceUsuario.addChildEventListener(usuariosChildEventListener);
                if (validarChildEventListener == null) {
                    validarChildEventListener = new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Foto foto = dataSnapshot.getValue(Foto.class);
                            List<Foto> valid = banco.getFotosValidar();
                            valid.add(foto);
                            banco.setFotosValidar(valid);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    };
                    validarFotosReference.addChildEventListener(validarChildEventListener);
                }
            }

        }
    }

    private void detachDatabaseListener() {
        if (mChildEventListener != null) {
            reference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
        if (usuariosChildEventListener != null) {
            referenceUsuario.removeEventListener(usuariosChildEventListener);
            usuariosChildEventListener = null;
        }
        if (validarChildEventListener!= null) {
            validarFotosReference.removeEventListener(validarChildEventListener);
            validarFotosReference = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the

                    // contacts-related task you need to do.


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Toast.makeText(this, "You need to grant permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}



