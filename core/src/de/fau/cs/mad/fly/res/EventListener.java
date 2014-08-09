package de.fau.cs.mad.fly.res;

public interface EventListener {
    public void onFinished();
    
    public void onGatePassed(GateGoal gate);
}
