package cl.telematica.android.usmvende.Interfaces;

import android.location.Location;

public interface LocationView {

        public void showLocationErrorMsg();

        public void showPermissionErrorMsg();

        public void manageLocationChange(Location mLocation);

        public void manageStatusChange(String provider, int status);

}
