package de.fau.cs.mad.fly.ui.help;

public interface WithHelpOverlay {
    
    public void startHelp();

	public void step(OverlayFrame overlay);
    
    public void endHelp();
    
}
