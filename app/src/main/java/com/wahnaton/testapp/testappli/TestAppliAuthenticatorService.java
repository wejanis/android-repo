package com.wahnaton.testapp.testappli;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TestAppliAuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {

        TestAppliAuthenticator authenticator = new TestAppliAuthenticator(this);
        return authenticator.getIBinder();
    }
}
