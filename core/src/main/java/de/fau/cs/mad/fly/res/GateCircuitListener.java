package de.fau.cs.mad.fly.res;

public interface GateCircuitListener {
    public void onFinished();
    
    public void onGatePassed(GateGoal gate);
}
