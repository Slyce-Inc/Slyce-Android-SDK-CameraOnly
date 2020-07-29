package it.slyce.slyce_camera_only.custom;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import it.slyce.sdk.SlyceCustomFragment;
import it.slyce.sdk.SlyceDetectionDescriptor;
import it.slyce.sdk.SlyceItemDescriptor;
import it.slyce.sdk.SlyceSession;
import it.slyce.sdk.SlyceSessionListenerAdapter;
import it.slyce.sdk.SlyceUIDelegate;
import it.slyce.sdk.labs.SlyceListDescriptor;

public class CustomUIFragment extends SlyceCustomFragment implements SlyceUIDelegate {
    public static final String TAG = CustomUIFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Notifies that the user created a list of selected items.
     *
     * @param listDescriptor The list of items that were selected.
     */
    @Override
    public void didCreateList(SlyceListDescriptor listDescriptor) {
        String logMessage = "didCreateList: %s";

        JSONArray jsonArray = new JSONArray();
        for (SlyceItemDescriptor item : listDescriptor.getItems()) jsonArray.put(item.getItem());

        try {
            Log.d("BatchCapture-List", jsonArray.toString(4));
            logMessage = String.format(logMessage, new Gson().toJson(jsonArray));

        } catch (JSONException e) {
            logMessage = String.format(logMessage, e.getLocalizedMessage());
            e.printStackTrace();
        }

        // Display "headless" response
        Toast.makeText(
                getContext(),
                logMessage,
                Toast.LENGTH_LONG
        ).show();
    }

    /**
     * Provides the opportunity to handle an item selection.
     *
     * @param itemDescriptor The item that was selected.
     * @return true if should display default detail layer, false if you will present item detail manually.
     */
    @Override
    public boolean shouldDisplayDefaultItemDetailLayerForItem(SlyceItemDescriptor itemDescriptor) {
        Toast.makeText(
                getContext(),
                String.format("shouldDisplayDefaultItemDetailLayerForItem: %s", new Gson().toJson(itemDescriptor.getItem())),
                Toast.LENGTH_LONG
        ).show();
        return false;
    }

    /**
     * Provides the opportunity to handle an item selection.
     *
     * @param itemDescriptors A list of the items that were returned.
     * @return true if should display default detail layer, false if you will present item detail manually.
     */
    @Override
    public boolean shouldDisplayDefaultListLayerForItems(List<SlyceItemDescriptor> itemDescriptors) {
        JSONArray jsonArray = new JSONArray();
        for (SlyceItemDescriptor itemDescriptor : itemDescriptors)
            jsonArray.put(itemDescriptor.getItem());

        String logMessage = String.format(
                "shouldDisplayDefaultListLayerForItems %s",
                new Gson().toJson(jsonArray)
        );

        Toast.makeText(
                getContext(),
                logMessage,
                Toast.LENGTH_LONG
        ).show();
        Log.d(TAG, logMessage);

        return false;
    }

    /**
     * Notifies that a session was opened.
     *
     * @param session The created {@link SlyceSession}, always non-null.
     */
    @Override
    public void didOpenSession(@NonNull SlyceSession session) {
        session.addListener(new SlyceSessionListenerAdapter() {
            @Override
            public void didPerformDetection(SlyceSession session, SlyceDetectionDescriptor.SlyceBarcodeDetectionDescriptor descriptor) {
                String logMessage = String.format("didPerformDetection: %s", new Gson().toJson(descriptor));
                Toast.makeText(
                        getContext(),
                        logMessage,
                        Toast.LENGTH_LONG
                ).show();
                Log.d(TAG, logMessage);
            }
        });
//        // You can set search specific parameters here.
//        session.setSessionDelegate((slyceSession, slyceSearchImage) -> {
//            SlyceSearchParameters searchParameters = new SlyceSearchParameters();
//            // todo: set search parameters here
//            return searchParameters;
//        });
    }
}