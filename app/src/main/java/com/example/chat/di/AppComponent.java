package com.example.chat.di;

import com.example.chat.App;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created by jimji on 2017/11/13.
 */
@Component(modules = {AndroidSupportInjectionModule.class, AppMoudle.class})
public interface AppComponent extends AndroidInjector<DaggerApplication> {
    void inject(App app);
}
