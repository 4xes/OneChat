package com.eor.onechat.calls;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissions {

    private static boolean addPermission(List<String> permissionsList, String permission, Activity activity) {
        Context context = activity;

        if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))
                return false;
        }
        return true;
    }

    public static boolean requestMultiplePermissions(final Activity activity, boolean onlyCameraStarage) {
        final List<String> permissionsList = new ArrayList<String>();
        String[] requiredPermissions = onlyCameraStarage ? new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        } : new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE
        };

        for (int i = 0; i < requiredPermissions.length; i++) {
            addPermission(permissionsList, requiredPermissions[i], activity);
        }

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(activity,
                    permissionsList.toArray(new String[permissionsList.size()]),
                    111);
            return true;
        } else {
            return false;
        }
    }
}
