package de.fau.cs.mad.fly;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Disposable;

public class DisposeScreenManager implements Disposable {
    private static DisposeScreenManager instance;
    private List<Screen> disposables;
    
    public static DisposeScreenManager getInstance() {
        if (instance == null) {
            instance = new DisposeScreenManager();
        }
        return instance;
    }
    
    private DisposeScreenManager() {
        disposables = new ArrayList<Screen>();
    }
    
    public void registerForDispose(Screen newDisposableScreen) {
        disposables.add(newDisposableScreen);
    }
    
    @SuppressWarnings("static-access")
    @Override
    public void dispose() {
        for (int i = disposables.size() - 1; i > 0; i--) {
            disposables.get(i).dispose();
        }
        this.instance = null;
    }
    
}
