package com.mindguard.platform.deviceadmin

import android.app.admin.DeviceAdminReceiver

/**
 * Receiver used when the device is provisioned by an administrator.
 * Device Owner provisioning itself must be performed by Android setup/ADB/EMM;
 * an ordinary application cannot grant itself this role.
 */
class MindGuardDeviceAdminReceiver : DeviceAdminReceiver()
