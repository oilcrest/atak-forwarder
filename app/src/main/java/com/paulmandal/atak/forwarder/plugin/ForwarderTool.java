package com.paulmandal.atak.forwarder.plugin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ViewGroup;

import com.atakmap.android.ipc.AtakBroadcast;
import com.paulmandal.atak.forwarder.R;
import com.paulmandal.atak.forwarder.plugin.ui.ForwarderDropDownReceiver;

import transapps.mapi.MapView;
import transapps.maps.plugin.tool.Group;
import transapps.maps.plugin.tool.Tool;
import transapps.maps.plugin.tool.ToolDescriptor;

public class ForwarderTool extends Tool implements ToolDescriptor {

    private final Context context;

    public ForwarderTool(Context context) {
        this.context = context;
    }

    @Override
    public String getDescription() {
        return context.getString(R.string.app_name);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public Drawable getIcon() {
        return (context == null) ? null : context.getResources().getDrawable(R.drawable.ic_launcher);
    }

    @Override
    public Group[] getGroups() {
        return new Group[] {
                Group.GENERAL
        };
    }

    @Override
    public String getShortDescription() {
        return context.getString(R.string.app_name);
    }

    @Override
    public Tool getTool() {
        return this;
    }

    @Override
    public void onActivate(Activity activity, MapView mapView, ViewGroup viewGroup,
                           Bundle bundle,
                           ToolCallback toolCallback) {
        // Hack to close the dropdown that automatically opens when a tool
        // plugin is activated.
        if (toolCallback != null) {
            toolCallback.onToolDeactivated(this);
        }
        // Intent to launch the dropdown or tool
        Intent i = new Intent(ForwarderDropDownReceiver.SHOW_PLUGIN);
        AtakBroadcast.getInstance().sendBroadcast(i);
    }

    @Override
    public void onDeactivate(ToolCallback toolCallback) {}
}
