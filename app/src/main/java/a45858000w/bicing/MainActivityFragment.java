package a45858000w.bicing;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.osmdroid.events.MapEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.api.IMapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;

import java.util.ArrayList;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.util.GeoPoint;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private MapView map;
    private MyLocationNewOverlay myLocationOverlay;
    private MinimapOverlay mMinimapOverlay;
    private ScaleBarOverlay mScaleBarOverlay;
    private CompassOverlay mCompassOverlay;
    private IMapController mapController;
    private RadiusMarkerClusterer markers;
    private View view;
    ActionMode mActionMode;


    private ArrayList<DatosEstacion> datosEstaciones;

    private ProgressDialog dialog;

    public MainActivityFragment() {
    }

    @Override//notificamos al activity quer le añadimos items al menu
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_main, container, false);

        map = (MapView) view.findViewById(R.id.map);

        initializeMap();
        setZoom();
        setOverlays();

        putMarkers();
        map.invalidate();


        //onInfoWindowClick (Marker marker)


        return view;
    }

    private void putMarkers() {
        setupMarkerOverlay();

        if (datosEstaciones != null) {
            for (DatosEstacion estacion : datosEstaciones) {
                Marker marker = new Marker(map);

                GeoPoint point = new GeoPoint(
                        estacion.getLatitude(),
                        estacion.getLongitude()
                );

                marker.setPosition(point);

                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                Drawable icono = null;
                int disponibilidad = (estacion.getBikes() * 100 / (estacion.getBikes() + estacion.getSlots()));

                if (disponibilidad == 0) {
                    icono = getResources().getDrawable(R.drawable.bici0_opt);
                } else if (disponibilidad > 0 && disponibilidad < 25) {
                    icono = getResources().getDrawable(R.drawable.bici25_opt);
                } else if (disponibilidad > 25 && disponibilidad < 50) {
                    icono = getResources().getDrawable(R.drawable.bici50_opt);
                } else if (disponibilidad > 50 && disponibilidad < 75) {
                    icono = getResources().getDrawable(R.drawable.bici75_opt);
                } else if (disponibilidad > 75) {
                    icono = getResources().getDrawable(R.drawable.bici100_opt);
                }

                final String titulo = "-> " + estacion.getStreetName() + ", " + estacion.getStreetNumber() + " [" + estacion.getStatus() + "]";
                final String datos = "\tDisponibles Bicis a Recoger = " + estacion.getBikes() + "," +
                        "\n\t Disponibles Bicis a colocar = " + estacion.getSlots() + "," +
                        "\n\t Tipo de Bici = " + estacion.getType();

                marker.setIcon(icono);
                marker.setTitle(titulo);
                marker.setSnippet(datos);
                marker.setImage(icono);


                //si se pone este evento no funciona lo de arriba de setIcono,setti..., por eso hay que probar si dentro funciona, y ver si funciona lo del boton
                final Drawable finalIcono = icono;
                marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {
                        Log.d("----------", "-------------");
                        marker.setIcon(finalIcono);
                        marker.setTitle(titulo);
                        marker.setSnippet(datos);
                        marker.setImage(finalIcono);

                        //PROBAR!!
                        //al hacer on clicl marker
                        //ActionBar Contextual
                        /*mActionMode = getActivity().startActionMode((ActionMode.Callback) new ActionBarCallBack(marker));
                        mActionMode.setTitle(marker.getTitle());
                        */

                        return false;
                    }
                });


                marker.setAlpha(0.6f);




                markers.add(marker);
                markers.invalidate();
                map.invalidate();
            }
        }
    }


    private void setupMarkerOverlay() {
        markers = new RadiusMarkerClusterer(getContext());
        map.getOverlays().add(markers);

        Drawable clusterIconD = getResources().getDrawable(R.drawable.index_opt);
        Bitmap clusterIcon = ((BitmapDrawable) clusterIconD).getBitmap();


        markers.setIcon(clusterIcon);
        markers.setRadius(100);

    }

    private void initializeMap() {
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        map.setTilesScaledToDpi(true);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
    }

    private void setZoom() {
        //  Setteamos el zoom al mismo nivel y ajustamos la posición a un geopunto
        mapController = map.getController();
        mapController.setZoom(14);
    }

    private void setOverlays() {
        final DisplayMetrics dm = getResources().getDisplayMetrics();

        myLocationOverlay = new MyLocationNewOverlay(getContext(), new GpsMyLocationProvider(getContext()), map);

        myLocationOverlay.enableMyLocation();

        myLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                mapController.animateTo(myLocationOverlay.getMyLocation());
            }
        });

        mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);

        mCompassOverlay = new CompassOverlay(
                getContext(),
                new InternalCompassOrientationProvider(getContext()),
                map
        );
        mCompassOverlay.enableCompass();

        map.getOverlays().add(myLocationOverlay);
        //map.getOverlays().add(this.mMinimapOverlay);
        map.getOverlays().add(this.mScaleBarOverlay);
        map.getOverlays().add(this.mCompassOverlay);
    }


    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }

    private void refresh() {
        RefreshDataTask rdt = new RefreshDataTask();
        rdt.execute();
    }

    private class RefreshDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            //Api api = new Api();
            ArrayList<DatosEstacion> result = null;// api.getAllChampions();

            //Log.d("____________________","1 -------------");
            result = Api.getDatosEstaciones();

            //Log.d("DEBUG", result.toString());
            datosEstaciones = result;
            return null;
        }
    }


    @Override//añadimos items al menu
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }


    //region Click en el boton Actualizar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mostrarEstaciones) {
            putMarkers();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    class ActionBarCallBack implements ActionMode.Callback {
        public Marker marker;

        //Constructor
        public ActionBarCallBack(Marker marker) {
            this.marker = marker;

        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub
            mode.getMenuInflater().inflate(R.menu.map_activity_menu_on_marker_select, menu);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub

            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }


    }
}
