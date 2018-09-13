package com.elegion.tracktor;

import android.app.Application;

import com.elegion.tracktor.di.application.ApplicationModule;

import io.realm.Realm;
import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.configuration.Configuration;
import toothpick.registries.FactoryRegistryLocator;
import toothpick.registries.MemberInjectorRegistryLocator;
import toothpick.smoothie.module.SmoothieApplicationModule;

public class App extends Application {

    private static Scope sAppScope;

    @Override
    public void onCreate() {
        super.onCreate();

        Toothpick.setConfiguration(Configuration.forProduction().disableReflection());
        //will generate after one @Inject
        //MemberInjectorRegistryLocator.setRootRegistry(new com.elegion.tracktor.MemberInjectorRegistry());
        //FactoryRegistryLocator.setRootRegistry(new com.elegion.tracktor.FactoryRegistry());

        sAppScope = Toothpick.openScope("Application");
        sAppScope.installModules(new ApplicationModule());

        Realm.init(this);
    }

    public static Scope getAppScope() {
        return sAppScope;
    }
}
