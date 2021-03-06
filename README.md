Fly [![Build Status](https://travis-ci.org/FAU-Inf2/fly-gdx.svg?branch=master)](https://travis-ci.org/FAU-Inf2/fly-gdx)
=========
Fly is a 3D flying game for Android and PC.

Fly started as a student project of the [Mobile Application Development](http://mad.cs.fau.de/) course at [Friedrich-Alexander-University Erlangen-Nuremberg](http://fau.eu).

Fly is also available on Google's [Play Store](https://play.google.com/store/apps/details?id=de.fau.cs.mad.fly.android)!

Build process
---
Fly uses [Gradle](http://www.gradle.org/) as its build tool. Gradle will automatically download all required dependencies for you. For more information on Gradle have a look at their [documentation](http://www.gradle.org/documentation).
###Command line
The project comes with wrappers (gradlew and gradlew.bat) so you don't need to install Gradle yourself.

To build and run the desktop version you would
```sh
gradlew -p desktop run
```

To build a debug **apk** for your android smartphone you would
```sh
gradlew assembleDebug
```
This creates the .apk file in _fly-gdx/android/build/outputs/apk/_.

To see a list of all available tasks you can simply
```sh
gradlew tasks
```
###IDEs
There are plugins available for Eclipse, IDEA and netbeans. These should allow you to import the project via the root build.gradle file. See  for further information.
The following links have some good information on using gradle with your IDE:
* [Gradle Tooling](http://www.gradle.org/tooling)
* [Setting up your development environment](https://github.com/libgdx/libgdx/wiki/Setting-up-your-Development-Environment-%28Eclipse%2C-Intellij-IDEA%2C-NetBeans%29)
* [Project Setup Gradle](https://github.com/libgdx/libgdx/wiki/Project-Setup-Gradle)


Contributing
---

Contributions are always welcome! The easiest way to contribute is by leaving feedback via our [Issue Tracker](https://github.com/FAU-Inf2/fly-gdx/issues). You can also fix bugs and add features yourself! Simply fork the [Fly repository](https://github.com/FAU-Inf2/fly-gdx/), make your code changes and send pull requests.

Libraries
---
Fly makes use of the following libraries and software:
* [libGDX](http://libgdx.badlogicgames.com)
* [libGDX SQLite extension](https://github.com/mrafayaleem/gdx-sqlite)
* [blender](http://www.blender.org)

License
---
Apache License 2.0
