package au.com.zacher.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * The service which allows the sync adapter framework to access the authenticator.
 */
public class AuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private Authenticator authenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        this.authenticator = new Authenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return this.authenticator.getIBinder();
    }
}