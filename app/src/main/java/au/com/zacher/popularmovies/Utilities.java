/*
 * Copyright 2015 Brad Zacher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package au.com.zacher.popularmovies;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncStatusObserver;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

import au.com.zacher.popularmovies.api.Configuration;
import au.com.zacher.popularmovies.model.DisplayItem;
import au.com.zacher.popularmovies.model.DisplayItemViewHolder;
import au.com.zacher.popularmovies.sync.SyncAdapter;

/**
 * Created by Brad on 9/07/2015.
 */
public final class Utilities {
    private static String providerAuthority;
    private static String providerAccountType;
    private static String providerAccountName;
    private static Account accountInstance;

    private static final LinkedList<Object> listenerHandles = new LinkedList<>();

    private static String baseImageUrl;
    private static String posterSize;
    private static String backdropSize;
    public static String getBasePosterUrl() {
        return baseImageUrl + posterSize;
    }
    public static String getBaseBackdropUrl() {
        return baseImageUrl + backdropSize;
    }
    private static int listColumnCount = 2;
    public static void setListColumnCount(int value) {
        Utilities.listColumnCount = value;
    }
    public static int getListColumnCount() {
        return Utilities.listColumnCount;
    }

    private static Configuration lastConfig;

    private static Context context;
    public static Context getApplicationContext() {
        return context;
    }
    public static void setApplicationContext(Context c) {
        context = c;

        // setup the variables for the sync
        providerAuthority = c.getResources().getString(R.string.provider_authority);
        providerAccountType = c.getResources().getString(R.string.provider_account_type);
        providerAccountName = "dummy_account";
        accountInstance = createSyncAccount();
    }

    /**
     * Loads an image in the background and switches it in when it is ready
     * @param context the context to use
     * @param item the item containing the image url to load
     * @param holder the holder to load the image into
     */
    public static void backgroundLoadImage(final Context context, final DisplayItem item, final DisplayItemViewHolder holder) {
        Picasso.with(context).cancelRequest(holder.image); // make sure to cancel any existing requests so that we don't get accidental overwrites
        Picasso.with(context)
                .load(Utilities.getBasePosterUrl() + item.imageUrl)
                .placeholder(R.drawable.ic_local_movies_black_48dp)
                .error(R.drawable.ic_error_outline_black_48dp)
                .into(holder.image);
    }

    /**
     * Gets the api key
     */
    public static String getApiKey() {
        return context.getResources().getString(R.string.api_key);
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
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifiInfo != null && wifiInfo.isConnected()) ||
                (mobileInfo != null && mobileInfo.isConnected());
    }

    /**
     * Converts an object to its json representation
     */
    public static String getObjectJson(Object o) {
        Gson gson = new Gson();
        return gson.toJson(o);
    }

    /**
     * Triggers an immediate sync
     */
    public static void triggerSync(int syncType, final SyncStatusObserver callback) {
        String type = "unknown";
        if (Logger.VERBOSE) {
            switch (syncType) {
                case SyncAdapter.SYNC_TYPE_CONFIGURATION:
                    type = "Configuration";
                    break;

                case SyncAdapter.SYNC_TYPE_DISCOVER_MOVIES:
                    type = "Popular Movies";
                    break;
            }
            Logger.v(R.string.log_sync_start, type);
        }
        final String finalType = type;

        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putInt(SyncAdapter.KEY_SYNC_TYPE, syncType);
        ContentResolver.requestSync(accountInstance, providerAuthority, bundle);
        final int i = listenerHandles.size();
        Object handle = ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE, new SyncStatusObserver() {
            @Override
            public void onStatusChanged(int which) {
                // we only want to notify them of the sync once
                Utilities.removeStatusChangeListener(i);
                callback.onStatusChanged(which);

                if (Logger.VERBOSE) {
                    Logger.v(R.string.log_sync_end, finalType);
                }
            }
        });
        listenerHandles.add(handle);
    }
    private static void removeStatusChangeListener(int i) {
        ContentResolver.removeStatusChangeListener(listenerHandles.get(i));
        listenerHandles.set(i, null);
    }

    private static Account createSyncAccount() {
        // FROM https://developer.android.com/training/sync-adapters/creating-sync-adapter.html

        // Create the account type and default account
        Account newAccount = new Account(providerAccountName, providerAccountType);
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            ContentResolver.setIsSyncable(newAccount, providerAuthority, 1);
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
            Account[] accounts = accountManager.getAccountsByType(providerAccountType);
            if (accounts.length == 0) {
                Logger.e(R.string.log_account_add_error);
                return null;
            } else {
                newAccount = accounts[0];
            }
        }
        return newAccount;
    }

    /**
     * Runs the specified sync every frequency intervals (i.e. if frequency = 2, and interval = DAY, the sync will run every 2 days)
     */
    public static void addPeriodicSync(int syncType, Bundle bundle, int frequency, SyncInterval interval) {
        long syncSeconds = 1L;
        final long secPerMin = 60L;
        final long minPerHour = 60L;
        final long hourPerDay = 24L;
        final long dayPerWeek = 7L;

        switch(interval) {
            case MINUTE:
                syncSeconds = secPerMin;
                break;

            case HOUR:
                syncSeconds = secPerMin * minPerHour;
                break;

            case DAY:
                syncSeconds = secPerMin * minPerHour * hourPerDay;
                break;

            case WEEK:
                syncSeconds = secPerMin * minPerHour * hourPerDay * dayPerWeek;
                break;
        }

        if (bundle == Bundle.EMPTY) {
            bundle = new Bundle();
        }
        if (!bundle.containsKey(SyncAdapter.KEY_SYNC_TYPE)) {
            bundle.putInt(SyncAdapter.KEY_SYNC_TYPE, syncType);
        }

        ContentResolver.addPeriodicSync(accountInstance, providerAuthority, bundle, frequency * syncSeconds);
    }

    /**
     * Calculates the images sizes to be most efficient (effects image loads and displaying)
     */
    public static void recalculateImageSizes() {
        // store the base image URL
        baseImageUrl = Utilities.lastConfig.images.base_url;

        // get the resolution width of the screen
        int screenWidth = Utilities.getScreenWidth();

        // look at the poster sizes and determine which is the most fitting for our screen
        int desiredPosterWidth = screenWidth / Utilities.listColumnCount;
        posterSize = Utilities.getClosestWidth(Utilities.lastConfig.images.poster_sizes, desiredPosterWidth);

        // do the same for the backdrop sizes
        //noinspection UnnecessaryLocalVariable
        int desiredBackdropWidth = screenWidth;
        backdropSize = Utilities.getClosestWidth(Utilities.lastConfig.images.backdrop_sizes, desiredBackdropWidth);
    }

    /**
     * Grabs the screen's width
     */
    public static int getScreenWidth() {
        Point p = new Point();
        ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(p);
        return p.x;
    }
    /**
     * Calculates the poster height using the given width
     */
    public static int calculatePosterHeight(int width) {
        return (int)Math.ceil(((double)width) * 1.5);
    }
    /**
     * Calculates the backdrop height using the given width
     */
    public static int calculateBackdropHeight(int width) {
        return (int)Math.ceil(((double)width) / (16f / 9f));
    }

    /**
     * Sets up the utilities based off of the given configuration
     */
    public static void initFromConfig(Configuration configuration) {
        Utilities.lastConfig = configuration;

        Utilities.recalculateImageSizes();
    }
    private static String getClosestWidth(String[] widthStrs, int desiredWidth) {
        int[] widths = new int[widthStrs.length];
        int i = 0;
        for (String width : widthStrs) {
            try {
                widths[i++] = Integer.parseInt(width.substring(1));
            } catch (NumberFormatException ignored) { } // any invalid widths we don't care for (esp you, "original"...)
        }
        Arrays.sort(widths);


        String size = "original";
        for (int width : widths) {
            if (width >= desiredWidth) {
                size = "w" + width;
                break;
            }
        }
        return size;
    }

    /**
     * Gets a string from the resources
     */
    public static String getString(int id) {
        return context.getResources().getString(id);
    }

    /**
     * Gets a preference value
     */
    public static String getPreference(int keyStringId, int defaultValueId) {
        return Utilities.getPreference(Utilities.getString(keyStringId), Utilities.getString(defaultValueId));
    }

    /**
     * Gets a preference value
     */
    public static String getPreference(String key, String defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, defaultValue);
    }

    public enum SyncInterval {
        MINUTE, HOUR, DAY, WEEK
    }

    /**
     * Compares two objects
     */
    public static boolean areEqual(Object a, Object b) {
        if (a == null || b == null) {
            return a == b;
        }
        return a.equals(b);
    }

    /**
     * The map of letters to the unicode regional indicator symbols
     */
    private static final HashMap<Character, char[]> regionalIndicatorSymbols = new HashMap<>(26);
    static {
        int unicodeA = 127462;
        char charA = 'A';
        for (int i = 0; i < 26; i++) {
            regionalIndicatorSymbols.put((char)(charA + i), Character.toChars(unicodeA + i));
        }
        /*
        regionalIndicatorSymbols.put('A', "\u1f1e6");
        regionalIndicatorSymbols.put('B', "\u1f1e7");
        regionalIndicatorSymbols.put('C', "\u1f1e8");
        regionalIndicatorSymbols.put('D', "\u1f1e9");
        regionalIndicatorSymbols.put('E', "\u1f1ea");
        regionalIndicatorSymbols.put('F', "\u1f1eb");
        regionalIndicatorSymbols.put('G', "\u1f1ec");
        regionalIndicatorSymbols.put('H', "\u1f1ed");
        regionalIndicatorSymbols.put('I', "\u1f1ee");
        regionalIndicatorSymbols.put('J', "\u1f1ff");
        regionalIndicatorSymbols.put('K', "\u1f1f0");
        regionalIndicatorSymbols.put('L', "\u1f1f1");
        regionalIndicatorSymbols.put('M', "\u1f1f2");
        regionalIndicatorSymbols.put('N', "\u1f1f3");
        regionalIndicatorSymbols.put('O', "\u1f1f4");
        regionalIndicatorSymbols.put('P', "\u1f1f5");
        regionalIndicatorSymbols.put('Q', "\u1f1f6");
        regionalIndicatorSymbols.put('R', "\u1f1f7");
        regionalIndicatorSymbols.put('S', "\u1f1f8");
        regionalIndicatorSymbols.put('T', "\u1f1f9");
        regionalIndicatorSymbols.put('U', "\u1f1fa");
        regionalIndicatorSymbols.put('V', "\u1f1fb");
        regionalIndicatorSymbols.put('W', "\u1f1fc");
        regionalIndicatorSymbols.put('X', "\u1f1fd");
        regionalIndicatorSymbols.put('Y', "\u1f1fe");
        regionalIndicatorSymbols.put('Z', "\u1f1ff");
        */
    }

    /**
     * Gets the related flag emoji for the given country code
     */
    public static String getFlagEmoji(String countryCode) {
        if (countryCode.length() != 2) {
            throw new IllegalArgumentException("Must be 2-character length code.");
        }
        //noinspection StringBufferReplaceableByString
        StringBuilder sb = new StringBuilder();
        sb.append(regionalIndicatorSymbols.get(countryCode.charAt(0)));
        sb.append(regionalIndicatorSymbols.get(countryCode.charAt(1)));
        return  sb.toString();
    }

    /**
     * Checks if the phone is in landscape orientation
     */
    public static boolean isLandscape() {
        return Utilities.context.getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE;
    }

    private static int lastOrientation = Integer.MIN_VALUE;
    /**
     * Will recalculate the required image sizes if the orientation has changed since the last recalculation
     */
    public static void recalculateImageSizesIfOrientationChanged() {
        int currentOrientation = Utilities.context.getResources().getConfiguration().orientation;
        if (Utilities.lastConfig == null && Utilities.lastOrientation == Integer.MIN_VALUE) {
            Utilities.lastOrientation = currentOrientation;
            return;
        }

        if (Utilities.lastOrientation != currentOrientation) {
            Utilities.recalculateImageSizes();
            Utilities.lastOrientation = currentOrientation;
        }
    }
}
