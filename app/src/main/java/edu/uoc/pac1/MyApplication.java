package edu.uoc.pac1;

import com.facebook.stetho.Stetho;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.orm.SugarApp;

/**
 * Created by laura on 08/08/16.
 */
public class MyApplication extends SugarApp {

    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}