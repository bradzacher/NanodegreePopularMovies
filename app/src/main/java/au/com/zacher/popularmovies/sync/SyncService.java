package au.com.zacher.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Define a Service that returns an IBinder for the sync adapter class, allowing the sync adapter framework to call onPerformSync().
 * From https://developer.android.com/training/sync-adapters/creating-sync-adapter.html
 */
public class SyncService extends Service {
    private static SyncAdapter syncAdapter;
    private static final Object lock = new Object();

    @Override
    public void onCreate() {
        synchronized (lock) {
            if (syncAdapter == null) {
                syncAdapter = new SyncAdapter(this.getApplicationContext(), true, true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}
