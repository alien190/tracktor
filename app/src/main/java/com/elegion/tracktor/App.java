package com.elegion.tracktor;

import android.app.Application;

import com.elegion.tracktor.di.application.ApplicationModule;
import com.elegion.tracktor.di.application.NetworkModule;
import com.elegion.tracktor.job.LocationJobCreator;
import com.evernote.android.job.JobManager;
import com.squareup.picasso.Picasso;

import io.realm.Realm;
import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.configuration.Configuration;
import toothpick.registries.FactoryRegistryLocator;
import toothpick.registries.MemberInjectorRegistryLocator;

public class App extends Application {



    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        Toothpick.setConfiguration(Configuration.forProduction().disableReflection());
        Toothpick.setConfiguration(Configuration.forProduction().preventMultipleRootScopes());
        MemberInjectorRegistryLocator.setRootRegistry(new com.elegion.tracktor.MemberInjectorRegistry());
        FactoryRegistryLocator.setRootRegistry(new com.elegion.tracktor.FactoryRegistry());

        Scope sAppScope = Toothpick.openScope("Application");
        sAppScope.installModules(new ApplicationModule(this), new NetworkModule());

        Picasso.Builder builder = new Picasso.Builder(this);
        Picasso.setSingletonInstance(builder.build());
        JobManager.create(this).addJobCreator(new LocationJobCreator());
    }

}
