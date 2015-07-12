package au.com.zacher.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * The service which allows the sync adapter framework to access the authenticator.
 * from: https://developer.android.com/training/sync-adapters/creating-authenticator.html
 */
public class StubAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private StubAuthenticator stubAuthenticator;

    @Override
    public void onCreate() {
        // Create a new stubAuthenticator object
        this.stubAuthenticator = new StubAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return this.stubAuthenticator.getIBinder();
    }
}