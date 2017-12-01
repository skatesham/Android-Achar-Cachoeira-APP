package fiorin.sham.aventura.Fragments;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.LinkedList;
import java.util.List;

import fiorin.sham.aventura.Model.Banco;
import fiorin.sham.aventura.Model.Local;
import fiorin.sham.aventura.R;

public class MapaGeralFragment extends Fragment implements OnMapReadyCallback {

    private int id;
    private int count;
    private boolean isMapa;
    private List<MarkerOptions> markers = null;
    private View myView;
    private MapView mapView;
    private GoogleMap mMap;
    private Banco banco;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.mapa_geral_layout, container, false);

        banco = Banco.getInstance();
        banco.getCachoeiras();

        mapView = (MapView) myView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bundle bundle = getArguments();
        isMapa = bundle.getBoolean("isMapa");
        if (!isMapa) {
            id = bundle.getInt("id");
        }
        mapView.getMapAsync(this);
        return myView;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        markers = new LinkedList<>();
        mMap = googleMap;
        if (isMapa) {
            count = 0;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Local localAtual : banco.getCachoeiras()) {
                LatLng local = new LatLng(localAtual.getLocal().getLat(), localAtual.getLocal().getLng());
                MarkerOptions markerOptionsmMap = new MarkerOptions().position(local).title(localAtual.getNome());
                switch (localAtual.getDificuldade()) {
                    case 1:
                        markerOptionsmMap.icon(BitmapDescriptorFactory.fromResource(R.drawable.markergreen));
                        break;
                    case 2:
                        markerOptionsmMap.icon(BitmapDescriptorFactory.fromResource(R.drawable.markerorange));
                        break;
                    case 3:
                        if (localAtual.getCarro() == 1) {
                            markerOptionsmMap.icon(BitmapDescriptorFactory.fromResource(R.drawable.markerblack));
                            break;
                        } else {
                            markerOptionsmMap.icon(BitmapDescriptorFactory.fromResource(R.drawable.markerred));
                            break;
                        }
                    default:
                        markerOptionsmMap.icon(BitmapDescriptorFactory.fromResource(R.drawable.markergreen));
                }
                markers.add(markerOptionsmMap);

                mMap.addMarker(markerOptionsmMap);
                GoogleMap.OnInfoWindowClickListener onInfoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        InfoFragment mapa = new InfoFragment();
                        Toast.makeText(getActivity(), marker.getId(), Toast.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putInt("id", banco.findIdLocalByNome(marker.getTitle()));
                        mapa.setArguments(bundle);
                        getFragmentManager().beginTransaction().replace(R.id.content_frame, mapa).addToBackStack(null).commit();
                    }
                };
                mMap.setOnInfoWindowClickListener(onInfoWindowClickListener);
                builder.include(local);
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 14));
        } else {
            Local localAtual = banco.getCachoeiras().get(id);
            final LatLng local = new LatLng(localAtual.getLocal().getLat(), localAtual.getLocal().getLng());
            mMap.addMarker(new MarkerOptions().position(local).title(localAtual.getNome()));
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    if (banco.getGps() == 1) {
                        waseDirection(local);
                    } else {
                        googleDirection(local);
                    }
                }
            });
            mMap.moveCamera(CameraUpdateFactory.newLatLng(local));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(local, 14));
        }
    }

    private void googleDirection(LatLng local) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.google.com").appendPath("maps").appendPath("dir").appendPath("").appendQueryParameter("api", "1")
                .appendQueryParameter("destination", local.latitude + "," + local.longitude);
        String url = builder.build().toString();
        Log.d("Directions", url);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void waseDirection(LatLng local) {
        try {
            //String url = "https://waze.com/ul?ll=-23.185781, -45.861056&navigate=yes";
            String url = "https://waze.com/ul?ll=" + local.latitude + ", " + local.longitude + "&navigate=yes";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            // If Waze is not installed, open it in Google Play:
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
            startActivity(intent);

        }
    }
}
