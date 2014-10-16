package de.fau.cs.mad.fly.ios.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.TimeUtils;

import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.settings.SettingManager;
import org.robovm.apple.coremotion.CMAttitude;
import org.robovm.apple.coremotion.CMDeviceMotion;
import org.robovm.apple.coremotion.CMMotionManager;

import de.fau.cs.mad.fly.game.FlightController;
import de.fau.cs.mad.fly.player.Player;
import de.fau.cs.mad.fly.profile.PlayerProfile;

/**
 * Created by tschaei on 15.09.14.
 */
public class IOSFlightController extends FlightController{
    private final CMMotionManager motionManager;
    private CMAttitude currentAttitude;

    public IOSFlightController(Player player, PlayerProfile playerProfile) {
        super(player, playerProfile);
        Gdx.app.log("IOSFlightController", "Instantiating motion manager");
        motionManager = new CMMotionManager();
        Gdx.app.log("IOSFlightController", motionManager.description());
        motionManager.setDeviceMotionUpdateInterval(1.f / 60.f);
        motionManager.startDeviceMotionUpdates();
        Gdx.app.log("IOSFlightController", "DeviceMotionUpdateInterval: " + motionManager.getDeviceMotionUpdateInterval());
        Gdx.app.log("IOSFlightController", "DeviceMotionAvailable: " + motionManager.isDeviceMotionAvailable());
        Gdx.app.log("IOSFlightController", "DeviceMotionActive: " + motionManager.isDeviceMotionActive());
    }

    @Override
    public void resetSteering() {
        //necessary because it takes some time until the motionManager creates motion data.
        long startTime = TimeUtils.nanoTime();
		do {
			if ( !motionManager.isDeviceMotionAvailable() || (TimeUtils.nanosToMillis(TimeUtils.timeSinceNanos(startTime)) / 1000.f) > 10.0f ) {
				Gdx.app.log("IOSFlightController.resetSteering", "CMMotionManager is not available. Falling back to touch.");
				PlayerProfileManager.getInstance().getCurrentPlayerProfile().getSettingManager().set(SettingManager.USE_TOUCH, true);
				useSensorData = false;
				return;
			}
		} while (motionManager.getDeviceMotion() == null);
		CMDeviceMotion motion = motionManager.getDeviceMotion();
		Gdx.app.log("IOSFlightController.resetSteering", "motion object null: " + Boolean.toString(motion == null));
		currentAttitude = motionManager.getDeviceMotion().getAttitude();
		startRoll = (float) (currentAttitude.getRoll() * 180 / Math.PI);
		startPitch = -(float) (currentAttitude.getPitch() * 180 / Math.PI);
    }

    @Override
    protected void interpretSensorInput() {
        currentAttitude = motionManager.getDeviceMotion().getAttitude();
        roll = (float) (currentAttitude.getRoll() * 180 / Math.PI);
        pitch = -(float) (currentAttitude.getPitch() * 180 / Math.PI);

        Gdx.app.log("IOSFlightController.interpretSensorInput", "Current roll: " + roll + "\nCurrent pitch: " + pitch + "\nStarting roll: " + startRoll + ". Starting pitch: " + startPitch);

        // removing oldest element in buffers
        if (rollInput.size() >= bufferSize) {
            rollInput.remove(0);
            pitchInput.remove(0);
        }

        // adding newest sensor-data to buffers
        rollInput.add(roll);
        pitchInput.add(pitch);

        roll = average(rollInput);
        pitch = average(pitchInput);

        // azimuth = computeAzimuth(roll, pitch, azimuth);

        float difRoll = roll - startRoll;
        if (Math.abs(difRoll) > 180) {
            difRoll -= Math.signum(difRoll) * 360;
        }

        float difPitch = pitch - startPitch;
        if (Math.abs(difPitch) > 180) {
            difPitch -= Math.signum(difPitch) * 360;
        }

        // capping the rotation to a maximum of 90 degrees
        if (Math.abs(difRoll) > maxRotate) {
            difRoll = maxRotate * Math.signum(difRoll);
        }

        if (Math.abs(difPitch) > maxRotate) {
            difPitch = maxRotate * Math.signum(difPitch);
        }

        rollFactor = 0.0f;
        azimuthFactor = 0.0f;

        // camera rotation according to smartphone rotation
        setAzimuthFactor(difPitch / maxRotate);
        setRollFactor(difRoll / -maxRotate);
    }


}
