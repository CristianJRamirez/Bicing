package a45858000w.bicing;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
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
    private RadiusMarkerClusterer parkingMarkers;
    private View view;

    private ProgressDialog dialog;

    public MainActivityFragment() {
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

        map.invalidate();

        return view;
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

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //dialog.dismiss();
        }
    }

}
