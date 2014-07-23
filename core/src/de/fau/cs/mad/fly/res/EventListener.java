package de.fau.cs.mad.fly.res;

import de.fau.cs.mad.fly.res.Gate;

public interface EventListener {
    public void onFinished();
    
    public void onGatePassed(Gate gate);
}
