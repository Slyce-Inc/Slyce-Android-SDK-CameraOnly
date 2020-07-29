package it.slyce.slyce_camera_only.main.viewcontroller;

import android.Manifest;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import it.slyce.sdk.Slyce;
import it.slyce.sdk.SlyceActivityMode;
import it.slyce.sdk.SlycePrivacyPolicy;
import it.slyce.sdk.SlyceSearchParameters;
import it.slyce.sdk.SlyceSession;
import it.slyce.sdk.SlyceUI;
import it.slyce.sdk.exception.initialization.SlyceMissingGDPRComplianceException;
import it.slyce.sdk.exception.initialization.SlyceNotOpenedException;
import it.slyce.slyce_camera_only.R;
import it.slyce.slyce_camera_only.core.SettingsManager;
import it.slyce.slyce_camera_only.custom.CustomUIFragment;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};

    // Add your Slyce account credentials here.
    private static final String SLYCE_ACCOUNT_ID = "";
    private static final String SLYCE_API_KEY = "";
    private static final String SLYCE_SPACE_ID = "";

    // Set your privacy policy and compliance flags here.
    private static final boolean REQUIRE_GDPR = false;
    private static final boolean OPT_OUT_AND_REMOVE_DATA = false;
    private static final String SLYCE_LANGUAGE_CODE = "en";
    private static final String SLYCE_COUNTRY_CODE = "US";

    private Slyce slyce;
    private SlyceSession slyceSession;
    private SettingsManager settingsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // A LensView can be added to any layout and is a self-contained Slyce
        // camera experience that is managed through a Slyce session and
        // the configuration given to it at init-time.
        setContentView(R.layout.activity_main);

        // Helper class to manage Slyce session settings
        settingsManager = new SettingsManager(getBaseContext());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // If the Slyce SDK is open and we have camera permissions then
        // create a new session and display the camera
        if (null != slyce && slyce.isOpen() && isCameraPermissionGranted()) {

            // If the Slyce SDK is open and we have camera permissions then
            // kick off a new session.
            initSlyceSession();

            // Display Slyce's camera UI
            displaySlyceCamera();

        } else {

            // The SDK is not open, so open it here.
            openSlyce();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If camera permission has not been granted then there's no reason to continue
        // setting up the SDK.
        if (!isCameraPermissionGranted()) return;

        // If the Slyce SDK is open and we have camera permissions then
        // kick off a new session.
        initSlyceSession();

        // Display Slyce's camera UI
        displaySlyceCamera();
    }

    private void showErrorDialog(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder
                .setTitle(R.string.error_dialog_title)
                .setMessage(error)
                .setNegativeButton(R.string.ok, (dialog, id) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean isCameraPermissionGranted() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PERMISSION_GRANTED;
    }

    private void openSlyce() {
        // If Slyce is already instantiated and open then
        // there is no need to continue.
        if (slyce != null && slyce.isOpen()) return;

        // Acquire a reference to the SDK.
        slyce = Slyce.getInstance(this);

        // Authenticate your app with the Slyce SDK and then configure the session
        slyce.open(SLYCE_ACCOUNT_ID, SLYCE_API_KEY, SLYCE_SPACE_ID, slyceError -> {
            if (slyceError != null) showErrorDialog(slyceError.getDetails());

            // If camera permission has not been granted, request it.
            if (!isCameraPermissionGranted())
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        REQUIRED_PERMISSIONS,
                        PERMISSION_REQUEST_CODE
                );

            // An example of some of the settings that can be set.
            // The usage of these values is demonstrated below.
            settingsManager.setGDPREnabled(false);
            settingsManager.setCountry(SLYCE_COUNTRY_CODE);
            settingsManager.setLanguage(SLYCE_LANGUAGE_CODE);

            // Enable/Disable GDPR compliance support and the language/country code.
            // NOTE: All off these settings are OPTIONAL.  They are being set here for example
            // purposes only and may not be applicable to your own application.
            slyce.getGDPRComplianceManager().setUserRequiresGDPRCompliance(settingsManager.isGDPREnabled());
            slyce.getGDPRComplianceManager().setLanguageCode(settingsManager.getLanguage());
            slyce.getGDPRComplianceManager().setCountryCode(settingsManager.getCountry());

            // If the Slyce SDK is open and we have camera permissions then
            // kick off a new session.
            // Slyce is open, so begin a new search session
            initSlyceSession();

            // Display Slyce's camera UI
            displaySlyceCamera();
        });
    }

    private void initSlyceSession() {
        if (null == slyce)
            showErrorDialog("The Slyce SDK must be open before attempting to initialize a session.");

        try {
            // Ensure the appropriate compliance measures have been taken (optional)
            handleDataPrivacy();

            // Set global search parameters.  Note that this also provides
            // an opportunity to set values such as the language or country code even
            // if GDPR compliance is not being enforced via the Slyce SDK.
            // This is also where workflow options could be provided to Slyce if the
            // supported workflow has been configured to accept them.
            SlyceSearchParameters parameters = new SlyceSearchParameters();
            parameters.setCountryCode(settingsManager.getCountry());
            parameters.setLanguageCode(settingsManager.getLanguage());
            slyce.setDefaultSearchParameters(parameters);

            // Create a new SDK session.
            slyceSession = slyce.createSession();

        } catch (SlyceMissingGDPRComplianceException | SlyceNotOpenedException e) {
            showErrorDialog(e.getUnderlyingError().getDetails());
        }
    }

    private void displaySlyceCamera() {
        if (null == slyce)
            showErrorDialog("The Slyce SDK must be open before attempting to initialize a session.");

        // We want to use Slyce's "Tap UI", however all further UI/UX
        // should be deferred to the application itself.  Here we load
        // a custom UI as a Fragment so that we can achieve this.
        try {
            // R.id.main_activity_fragment_container should reference a FrameLayout,
            // embedded in the MainActivity's layout, that will contain the SlyceFragment.
            new SlyceUI.FragmentLauncher(slyce, SlyceActivityMode.UNIVERSAL, R.id.main_activity_fragment_container)
                    // Optionally specify a custom SlyceFragment, otherwise; the internal SlyceFragment
                    // will be launched.
                    .customClassName(CustomUIFragment.class.getName())
                    // Set optional tag to be used in the FragmentManager
                    .fragmentTag(CustomUIFragment.TAG)
                    // Launch the Fragment
                    .launch(MainActivity.this);

        } catch (SlyceNotOpenedException e) {
            showErrorDialog(e.getUnderlyingError().getDetails());

        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            showErrorDialog(e.getLocalizedMessage());
        }
    }

    private void handleDataPrivacy() {
        try {
            // Tell Slyce to forget the user and opt them out of the privacy policy so that they
            // will be required to opt-in again the next time they utilize Slyce.
            if (OPT_OUT_AND_REMOVE_DATA) {

                // Prompt the user to confirm
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.privacy_opt_out_title);
                builder.setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();

                // Request that the user be opted out.
                slyce.getGDPRComplianceManager().optOutAndForgetUser(gdprError -> {
                    dialog.dismiss();

                    // Ensure that the process was successful.
                    if (gdprError != null)
                        showErrorDialog(gdprError.getLocalizedMessage());
                });
            }

            // When enforcing GDPR compliance, a user must explicitly agree to the privacy policy.
            // An example interaction occurs here.
            if (REQUIRE_GDPR) {
                // Acquire the GDPR privacy policy that must be presented to the user.
                SlycePrivacyPolicy privacyPolicy = slyce.getGDPRComplianceManager().getPrivacyPolicy();
                if (null == privacyPolicy)
                    return;

                // Here you are required to display the information in the
                // Privacy Policy and capture the user's consent. For more
                // information see `SlycePrivacyPolicy` in the SDK documentation.
                slyce.getGDPRComplianceManager().setUserDidConsent(privacyPolicy);
            }
        } catch (SlyceNotOpenedException e) {
            showErrorDialog(e.getUnderlyingError().getDetails());
        }
    }

}