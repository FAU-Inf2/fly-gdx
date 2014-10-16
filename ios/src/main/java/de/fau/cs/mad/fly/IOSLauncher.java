package de.fau.cs.mad.fly;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.rt.bro.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;

import java.lang.Runtime;

import de.fau.cs.mad.fly.Fly;

public class IOSLauncher extends IOSApplication.Delegate {
    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        config.useAccelerometer = true;
        config.useCompass = true;
        config.allowIpod = true;
        config.orientationLandscape = true;
        config.orientationPortrait = false;
        return new IOSApplication(new Fly(), config);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }

    @Override
    public void didReceiveMemoryWarning(UIApplication application) {
        System.out.println("Received a memory warning. Current total memory: " + Runtime.getRuntime().totalMemory());
        for(int i=0; i<5; i++) {
            System.gc();
        }
        System.out.println("Total memory after GC: " + Runtime.getRuntime().totalMemory());
    }

}