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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.api.IMapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MinimapOverlay;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_main, container, false);

        map = (MapView) view.findViewById(R.id.map);

        //dialog = new ProgressDialog(getContext());
        //dialog.setMessage("Loading...");

        initializeMap();
        setZoom();
        setOverlays();



        putMarkers();
        map.invalidate();

        return view;
    }

    private void putMarkers() {
        setupMarkerOverlay();

        if (datosEstaciones!=null) {
            for (DatosEstacion estacion : datosEstaciones) {
                Marker marker = new Marker(map);

                GeoPoint point = new GeoPoint(
                        estacion.getLatitude(),
                        estacion.getLongitude()
                );

                marker.setPosition(point);

                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                marker.setIcon(getResources().getDrawable(R.drawable.index_opt));

                String titulo= "-> "+estacion.getStreetName()+", "+estacion.getStreetNumber()+" ["+estacion.getStatus()+"],\n Disponibles Bicis a Recoger = "+ estacion.getBikes()+",\n Disponibles Bicis a colocar = "+estacion.getSlots();

                marker.setTitle(titulo);


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
        Bitmap clusterIcon = ((BitmapDrawable)clusterIconD).getBitmap();

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

        myLocationOverlay = new MyLocationNewOverlay(getContext(),new GpsMyLocationProvider(getContext()),map);

        myLocationOverlay.enableMyLocation();

        myLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                     mapController.animateTo( myLocationOverlay.getMyLocation());
                 }
            });

        /*
+        mMinimapOverlay = new MinimapOverlay(getContext(), map.getTileRequestCompleteHandler());
+        mMinimapOverlay.setWidth(dm.widthPixels / 5);
+        mMinimapOverlay.setHeight(dm.heightPixels / 5);
+*/

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
       /*Api api = new Api();
       String result = api.getChampions();
       Log.d("DEBUG", result);*/
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
            datosEstaciones=result;
            return null;
        }
    }


    @Override//añadimos items al menu
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main,menu);
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



}
