package it.slyce.slyce_camera_only.core;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import java.util.HashMap;

import it.slyce.sdk.SlyceOptions;

import static android.content.Context.MODE_PRIVATE;
import static it.slyce.sdk.SlyceOptions.KEY_CAPTURE_MODE;
import static it.slyce.sdk.SlyceOptions.KEY_DETECTION_THRESHOLD;
import static it.slyce.sdk.SlyceOptions.KEY_DISABLE_BARCODE_SEARCH_TASK;
import static it.slyce.sdk.SlyceOptions.KEY_LENSES;
import static it.slyce.sdk.SlyceOptions.LensCaptureMode.BATCH;
import static it.slyce.sdk.SlyceOptions.LensCaptureMode.SINGLE;

public class SettingsManager {
    public static final String PREFS_KEY_SETTINGS_MANAGER = "PREFS_KEY_SETTINGS_MANAGER";
    public static final String KEY_UNIVERSAL_BATCH_CAPTURE_ENABLED = "KEY_UNIVERSAL_BATCH_CAPTURE_ENABLED";
    public static final String KEY_BARCODE_BATCH_CAPTURE_ENABLED = "KEY_BARCODE_BATCH_CAPTURE_ENABLED";
    public static final String KEY_IMAGE_MATCH_BATCH_CAPTURE_ENABLED = "KEY_IMAGE_MATCH_BATCH_CAPTURE_ENABLED";
    public static final String KEY_VISUAL_SEARCH_BATCH_CAPTURE_ENABLED = "KEY_VISUAL_SEARCH_BATCH_CAPTURE_ENABLED";
    public static final String KEY_BARCODE_SEARCH_DISABLED = "KEY_BARCODE_SEARCH_DISABLED";

    public static final String KEY_2D_THRESHOLD = "KEY_2D_THRESHOLD";
    public static final String KEY_VIBRATION_ENABLED = "KEY_VIBRATION_ENABLED";
    public static final String KEY_SOUND_ENABLED = "KEY_SOUND_ENABLED";

    public static final String LENS_ID_1D = "slyce.1D";
    public static final String LENS_ID_2D = "slyce.2D";
    public static final String LENS_ID_3D = "slyce.3D";
    public static final String LENS_ID_UNIVERSAL = "slyce.universal";

    public static final String KEY_GDPR_ENABLED = "KEY_GDPR_ENABLED";
    public static final String KEY_LANGUAGE = "KEY_LANGUAGE";
    public static final String KEY_COUNTRY = "KEY_COUNTRY";

    private final SharedPreferences prefs;

    public SettingsManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_KEY_SETTINGS_MANAGER, MODE_PRIVATE);
    }

    public boolean getBoolForKey(String key) {
        return prefs.getBoolean(key, false);
    }

    public void setBoolForKey(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    @Nullable
    public String getStringForKey(String key) {
        return prefs.getString(key, null);
    }

    public void setStringForKey(String key, @Nullable String value) {
        prefs.edit().putString(key, value).apply();
    }

    public int get2DDetectionThreshold() {
        return prefs.getInt(KEY_2D_THRESHOLD, 40);
    }

    public void set2DDetectionThreshold(int threshold) {
        prefs.edit().putInt(KEY_2D_THRESHOLD, threshold).apply();
    }

    public boolean isGDPREnabled() {
        return prefs.getBoolean(KEY_GDPR_ENABLED, false);
    }

    public void setGDPREnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_GDPR_ENABLED, enabled).apply();
    }

    @Nullable
    public String getLanguage() {
        return prefs.getString(KEY_LANGUAGE, null);
    }

    public void setLanguage(@Nullable String language) {
        prefs.edit().putString(KEY_LANGUAGE, language).apply();
    }

    @Nullable
    public String getCountry() {
        return prefs.getString(KEY_COUNTRY, null);
    }

    public void setCountry(@Nullable String country) {
        prefs.edit().putString(KEY_COUNTRY, country).apply();
    }

    public boolean isVibrationEnabled() {
        return prefs.getBoolean(KEY_VIBRATION_ENABLED, true);
    }

    public void setVibrationEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, enabled).apply();
    }

    public boolean isSoundEnabled() {
        return prefs.getBoolean(KEY_SOUND_ENABLED, true);
    }

    public void setSoundEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply();
    }

    private SlyceOptions.LensCaptureMode captureModeForLensIdentifier(String lensIdentifier) {
        String key;

        switch (lensIdentifier) {
            case LENS_ID_UNIVERSAL:
                key = KEY_UNIVERSAL_BATCH_CAPTURE_ENABLED;
                break;
            case LENS_ID_1D:
                key = KEY_BARCODE_BATCH_CAPTURE_ENABLED;
                break;
            case LENS_ID_2D:
                key = KEY_IMAGE_MATCH_BATCH_CAPTURE_ENABLED;
                break;
            case LENS_ID_3D:
                key = KEY_VISUAL_SEARCH_BATCH_CAPTURE_ENABLED;
                break;
            default:
                return SINGLE;
        }
        if (getBoolForKey(key)) {
            return BATCH;
        }
        return SINGLE;
    }

    public HashMap<String, Object> getAdditionalOptions() {
        HashMap<String, Object> lensOptions = new HashMap<>();

        // Set 1D lens to batch capture
        HashMap<String, Object> lensOptions1d = new HashMap<>();
        lensOptions1d.put(KEY_CAPTURE_MODE, captureModeForLensIdentifier(LENS_ID_1D));
        lensOptions.put(LENS_ID_1D, lensOptions1d);

        // Set 2D lens to batch capture
        HashMap<String, Object> lensOptions2d = new HashMap<>();
        lensOptions2d.put(KEY_CAPTURE_MODE, captureModeForLensIdentifier(LENS_ID_2D));
        lensOptions.put(LENS_ID_2D, lensOptions2d);

        // Set 3D lens to batch capture
        HashMap<String, Object> lensOptions3d = new HashMap<>();
        lensOptions3d.put(KEY_CAPTURE_MODE, captureModeForLensIdentifier(LENS_ID_3D));
        lensOptions.put(LENS_ID_3D, lensOptions3d);

        // Set universal lens to continuous
        HashMap<String, Object> lensOptionsUniversal = new HashMap<>();
        lensOptionsUniversal.put(KEY_CAPTURE_MODE, captureModeForLensIdentifier(LENS_ID_UNIVERSAL));
        lensOptions.put(LENS_ID_UNIVERSAL, lensOptionsUniversal);

        // Add lens options to parent options map
        HashMap<String, Object> options = new HashMap<>();
        options.put(KEY_LENSES, lensOptions);
        options.put(KEY_DISABLE_BARCODE_SEARCH_TASK, getBoolForKey(KEY_BARCODE_SEARCH_DISABLED));

        // Specify a 2D detection threshold (if a valid value has been supplied)
        int detectionThreshold = get2DDetectionThreshold();
        if (detectionThreshold >= 0) options.put(KEY_DETECTION_THRESHOLD, detectionThreshold);

        return options;
    }
}
