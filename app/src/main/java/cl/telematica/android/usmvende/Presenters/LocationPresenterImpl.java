package cl.telematica.android.usmvende.Presenters;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import cl.telematica.android.usmvende.Interfaces.LocationView;
import cl.telematica.android.usmvende.Interfaces.LocationPresenter;


public class LocationPresenterImpl implements LocationPresenter, LocationListener {

    private LocationManager mLocationManager;
    private Activity mContext;
    private LocationView mLocationView;

    private final String GPS = LocationManager.GPS_PROVIDER; //Nombre del proveedor de ubicación GPS.
    private final String NET = LocationManager.NETWORK_PROVIDER; //Nombre del proveedor de ubicación de red.

    private static final long minTime = 10000; //Mínimo intervalo de tiempo entre actualizaciones de ubicación, en milisegundos
    private static final float minDistance = 200; //Distancia mínima entre las actualizaciones de ubicación, en metros



    //Constructor de la clase
    public LocationPresenterImpl(Activity mContext, LocationManager mLocationManager, LocationView mLocationView) {
        this.mLocationManager = mLocationManager;
        this.mContext = mContext;
        this.mLocationView = mLocationView;
    }



    //Metodos de LocationPresenter
    @Override
    public void startUpdates() {
        //Condiciones para determinar si los de GPS o NET estan habilitados
        if (!mLocationManager.isProviderEnabled(NET) && !mLocationManager.isProviderEnabled(GPS)) {//Devuelve el estado actual activado / desactivado del proveedor determinado.
            mLocationView.showLocationErrorMsg();
        }
        else {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                mLocationView.showPermissionErrorMsg();
                return;
            }
            if (mLocationManager.isProviderEnabled(NET)) {
                mLocationManager.requestLocationUpdates(NET, minTime, minDistance, this);//Registrarse para actualizaciones de ubicación utilizando el proveedor de llamada, y un LocationListener especificado.
            }
            if(mLocationManager.isProviderEnabled(GPS)){
                mLocationManager.requestLocationUpdates(GPS, minTime, minDistance, this); //Registrarse para actualizaciones de ubicación utilizando el proveedor de llamada, y un LocationListener especificado.
            }
        }
    }

    @Override
    public void stopUpdates() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mLocationView.showPermissionErrorMsg();
            return;
        }
        mLocationManager.removeUpdates(this); //Elimina todas las actualizaciones de ubicación para el LocationListener especificado.
    }




    //Metodos de LocationListener  (permite la conexion con los servicios de ubicacion como GPS y NET)
    //Un LocationListener cuyo onLocationChanged(Location)método será llamado para cada actualización de la localización
    @Override
    public void onLocationChanged(Location location) { // se ejecuta cuando hay cambios de localizacion
        mLocationView.manageLocationChange(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { //se ejecuta cuando hay cambios de proveedores 3G o GPS
        mLocationView.manageStatusChange(provider, status);
    }

    @Override
    public void onProviderEnabled(String provider) { //cuando un proveedor se habilita

    }

    @Override
    public void onProviderDisabled(String provider) { //cuando un proveedor se deshabilita

    }

}