BLENDER EXPORT for FLY Level Files

Put fly_export.py in your ...\scripts\startup folder of Blender.


Most of the time you only need Blender Render mode. (see middle part of top menu)
Everything that is changed in Blender Game mode is marked with a [GAME].



Every Level needs:
1. Camera called Player:
	- stores all the dependencies in the custom properties under "Object" (orange box on the left)
	- position and rotation defines starting position of the player
	- object constraints define the first gate(s) to fly through (the menu item right of the orange box)
		-> Add Object Constraint -> Child Of -> Set Target to next gate -> Set Influence to 0.000 -> Repeat if alternative gates are needed
			(blue dotted lines should appear)

2. Object (can be a box etc.) called Level:
	- position does not matter at all
	- custom properties (with example values):
		Border: space
		Class: tutorials.xxx		(can be empty)
		ID: 1
		Name: Level 1
		Time: 50


Gates:
- have to be called Gate.XXX
- custom properties (with example values):
	HoleModel: hole
	Model: torus
- object constraints define the successor gate(s) to fly through (the menu item right of the orange box)
	-> Add Object Constraint -> Child Of -> Set Target to successor gate -> Set Influence to 0.000 -> Repeat if alternative gates are needed
		(blue dotted lines should appear)


Upgrades:
- have to be called like the fitting upgrade
- custom properties (with example values):
	HoleModel: resizeGatesUpgrade
	scaleX: 2.0		(you have to change minimum/maximum value in the edit box)
	scaleY: 2.0
	scaleZ: 2.0

Deco:
- special custom property:
	Type: deco
	Model: <model>

Lamps:
- light always has to have the same name like the object
	(click on the light -> button 3rd from right on the long button row -> the names must be the same)
- 1 lamp called Ambient		-> color
- 1 lamp called AmbientLight	-> color

- Point.XXX for point lights	-> color, energy as intensity, position as position
- Spot.XXX for spot lights	-> color, rotation as direction

- every object can have an optional custom property called "Environment" with possible values "ambient" oder "lighting"


Gates + Upgrades:
- location / rotation / scale define the position / rotation / scaling of the gate (scaling not working for gates yet)
- to add rotation / translation:
	- select the gate/upgrade
	- [GAME]: click on physics on the left in the left side menu -> Physics Type Dynamic
	- select viewport "Logic Editor" on bottom
	- on the right part "Add Actuator" -> Motion (should add Simple Motion Actuator on bottom)
		- Force / Torque / Linear Velocity -> Sinus translation, e.g.:
			Force X,Y,Z -> Translation on X axis with X*sin(Y*i+Z) where i is increased by the delta in every frame.
		- Angular Velocity -> Rotation around vector X,y,Z with speed of length of the vector