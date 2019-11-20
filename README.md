# 2137-2019-Repository
This is the repository for the robot code for the 2019 robot for Team 2137.

Yay.

Thanks to everybody who has helped out throught the creation of this repository this season.
You've all proven pudding in my eyes.

## Repository Layout

This repository is organized into different folders, based on the different project
files for the different programs which run on/off the robot.
(Examples include the "Robot Program" folder for the main roborio VS Code project directory,
and the "Vision Project" folder for the Raspberry Pi side vision program.)

## Robot Program Documentation

The VS Code robot program tries to layout it's seperate chunks of functionality
into different packages.

The following describes the purpose of each package (inside src\main\java\org\torc\robot2019):
- commands
	- General commands used for robot-wide things(Not specific to only one Subsystem, for example).
- program
	- Main control modes of the robot and other "program-wide" methods and feilds.
- robot
	- Lower-level robot operations, including the program's entrypoint and main robot class.
	- Should not be changed. All further editing of the program should be through either the classes in the program
	package, or through other classes originally called from the program package.
- subsystems
	- Individual assemblies of the robot, with their own unique methods to control them.
- tools
	- Robot-wide classes/methods to assist in development.
- vision
	- Vision-specific classes to communicate with the Raspberry Pi and interpret vision targets.
