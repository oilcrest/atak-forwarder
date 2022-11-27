package com.paulmandal.atak.forwarder.comm.meshtastic;

import android.content.SharedPreferences;
import android.util.Base64;

import com.geeksville.mesh.ConfigProtos;
import com.google.gson.Gson;
import com.paulmandal.atak.forwarder.plugin.Destroyable;
import com.paulmandal.atak.forwarder.plugin.DestroyableSharedPrefsListener;
import com.paulmandal.atak.forwarder.preferences.PreferencesDefaults;
import com.paulmandal.atak.forwarder.preferences.PreferencesKeys;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class RDeviceConfigObserver extends DestroyableSharedPrefsListener {

    public interface Listener {
        void onSelectedDeviceChanged(MeshtasticDevice meshtasticDevice);
        void onDeviceConfigChanged(ConfigProtos.Config.LoRaConfig.RegionCode regionCode, String channelName, int channelMode, byte[] channelPsk, ConfigProtos.Config.DeviceConfig.Role deviceRole);
    }

    private final Gson mGson;

    private final Set<Listener> mListeners = new CopyOnWriteArraySet<>();

    public RDeviceConfigObserver(List<Destroyable> destroyables,
                                 SharedPreferences sharedPreferences,
                                 Gson gson) {
        super(destroyables,
                sharedPreferences,
                new String[]{
                        PreferencesKeys.KEY_DISABLE_WRITING_TO_COMM_DEVICE
                },
                new String[]{
                        PreferencesKeys.KEY_SET_COMM_DEVICE,
                        PreferencesKeys.KEY_COMM_DEVICE_IS_ROUTER,
                        PreferencesKeys.KEY_CHANNEL_NAME,
                        PreferencesKeys.KEY_CHANNEL_MODE,
                        PreferencesKeys.KEY_CHANNEL_PSK,
                        PreferencesKeys.KEY_REGION
                });

        mGson = gson;
    }

    @Override
    protected void updateSettings(SharedPreferences sharedPreferences) {
        // Do nothing
    }

    @Override
    protected void complexUpdate(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case PreferencesKeys.KEY_SET_COMM_DEVICE:
                String commDeviceStr = sharedPreferences.getString(PreferencesKeys.KEY_SET_COMM_DEVICE, PreferencesDefaults.DEFAULT_COMM_DEVICE);
                MeshtasticDevice meshtasticDevice = mGson.fromJson(commDeviceStr, MeshtasticDevice.class);

                notifySelectedDeviceListeners(meshtasticDevice);
                break;
            case PreferencesKeys.KEY_COMM_DEVICE_IS_ROUTER:
            case PreferencesKeys.KEY_REGION:
            case PreferencesKeys.KEY_CHANNEL_NAME:
            case PreferencesKeys.KEY_CHANNEL_MODE:
            case PreferencesKeys.KEY_CHANNEL_PSK:
                ConfigProtos.Config.LoRaConfig.RegionCode regionCode = ConfigProtos.Config.LoRaConfig.RegionCode.forNumber(Integer.parseInt(sharedPreferences.getString(PreferencesKeys.KEY_REGION, PreferencesDefaults.DEFAULT_REGION)));
                String channelName = sharedPreferences.getString(PreferencesKeys.KEY_CHANNEL_NAME, PreferencesDefaults.DEFAULT_CHANNEL_NAME);
                int channelMode = Integer.parseInt(sharedPreferences.getString(PreferencesKeys.KEY_CHANNEL_MODE, PreferencesDefaults.DEFAULT_CHANNEL_MODE));
                byte[] channelPsk = Base64.decode(sharedPreferences.getString(PreferencesKeys.KEY_CHANNEL_PSK, PreferencesDefaults.DEFAULT_CHANNEL_PSK), Base64.DEFAULT);
                boolean isRouter = sharedPreferences.getBoolean(PreferencesKeys.KEY_COMM_DEVICE_IS_ROUTER, PreferencesDefaults.DEFAULT_COMM_DEVICE_IS_ROUTER);

                notifyConfigListeners(regionCode, channelName, channelMode, channelPsk, isRouter);
                break;
        }
    }

    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    private void notifySelectedDeviceListeners(MeshtasticDevice meshtasticDevice) {
        for (Listener listener : mListeners) {
            listener.onSelectedDeviceChanged(meshtasticDevice);
        }
    }

    private void notifyConfigListeners(ConfigProtos.Config.LoRaConfig.RegionCode regionCode, String channelName, int channelMode, byte[] channelPsk, ConfigProtos.Config.DeviceConfig.Role deviceRole) {
        for (Listener listener : mListeners) {
            listener.onDeviceConfigChanged(regionCode, channelName, channelMode, channelPsk, deviceRole);
        }
    }
}
