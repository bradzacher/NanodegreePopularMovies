package au.com.zacher.popularmovies;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncStatusObserver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import au.com.zacher.popularmovies.model.DisplayItem;
import au.com.zacher.popularmovies.model.DisplayItemViewHolder;
import au.com.zacher.popularmovies.sync.SyncAdapter;

/**
 * Created by Brad on 9/07/2015.
 */
public final class Utilities extends Application {
    /**
     * Loads an image in the background and switches it in when it is ready
     * @param context the context to use
     * @param item the item containing the image url to load
     * @param holder the holder to load the image into
     * @param position the current position of the holder (if this changes before the image is loaded, the image will not be swapped in)
     */
    public static void backgroundLoadImage(Context context, final DisplayItem item, final DisplayItemViewHolder holder, final int position) {
        //noinspection deprecation
        holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_local_movies_black_48dp));
        final ImageView backgroundLoadedImage = new ImageView(context);
        Picasso.with(context)
                .load(item.imageUrl)
                .error(R.drawable.ic_error_outline_black_48dp)
                .into(backgroundLoadedImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        if (holder.position == position) {
                            holder.image.setImageDrawable(backgroundLoadedImage.getDrawable());
                        }
                    }

                    @Override
                    public void onError() {
                        Logger.e(R.string.log_image_load_error, item.imageUrl);
                        if (holder.position == position) {
                            holder.image.setImageDrawable(backgroundLoadedImage.getDrawable());
                        }
                    }
                });
    }

    private static final Application instance = new Utilities();
    /**
     * Gets the api key
     */
    public static String getApiKey() {
        return instance.getResources().getString(R.string.api_key);
    }

    /**
     * Gets the country code from the system
     */
    public static String getCountryCode() {
        return Locale.getDefault().getCountry();
    }

    /**
     * Checks if we are connected to the interwebs
     */
    public static boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) instance.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifiInfo != null && wifiInfo.isConnected()) ||
                (mobileInfo != null && mobileInfo.isConnected());
    }

    public static String getObjectJson(Object o) {
        Gson gson = new Gson();
        return gson.toJson(o);
    }

    private static final String AUTHORITY = "au.com.zacher.popularmovies";
    public static void triggerSync(int syncType, SyncStatusObserver callback) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putInt(SyncAdapter.SYNC_TYPE_BUNDLE_KEY, syncType);
        ContentResolver.requestSync(null, AUTHORITY, bundle);
        ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE, callback);
    }
}
