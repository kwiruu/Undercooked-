# Undercooked

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).
Created for the CSIT228 Object Oriented Programming 2 Final Capstone Project.

This project was generated with a template including simple application launchers and an `ApplicationAdapter` extension that draws libGDX logo.

<p align="center">
  <img src="https://github.com/kwiruu/Undercooked-/blob/master/assets/github-icons/Title%20Screen.png" width="30%">
</p>

<table border="1">
 <tr>
    <td width="50%">
      
## About
- We created this project as the final requirements for the CSIT228 `Object Oriented Programming 2` Final Project in `Cebu Institute of Technology - University` located in Cebu City, Philippines. Consisted of 4 Contributors guided by Professor `Jay Vince Serato`.

## Contributors
- `Bolante, Val Mykel Ceven`
- `Benitez, Mars`
- `Cabili, Keiru Vent`
- `So, Alec Giuseppe`
<p align="center">
  <img src="https://github.com/kwiruu/Undercooked-/blob/master/assets/github-icons/splashscreen-export.png" width="50%">
</p>
      
## Requirements
- `IntelliJ` : Needed for running the base code.
- `Xampp` : Main database handler (name the database `dbundercooked`).
- `jdk-21` : This version is needed to run.

## Platforms
- `intellij`: Main IDE to run the software.
- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3.
- `xampp`: For cross-platform server database for the informations.
- `aseprite`: Pixel art editor used for animation and static sprites.
- `tiled`: Mapping the whole maptile and tilesets.

## Assets
- `Modern Exteriors`: By LimeZu
- `Modern Interiors`: By LimeZu
- `Modern User Interface`: By LimeZu
- `8-bit Audio` : Free royalty audio from youtube.

   </td>
   <td>
     <img src="https://github.com/kwiruu/Undercooked-/blob/master/assets/github-icons/map1.gif">
     <img src="https://github.com/kwiruu/Undercooked-/blob/master/assets/github-icons/map2.gif">
     <img src="https://github.com/kwiruu/Undercooked-/blob/master/assets/github-icons/map3.gif">
     <img src="https://github.com/kwiruu/Undercooked-/blob/master/assets/github-icons/map4.gif"> 
     <img src="https://github.com/kwiruu/Undercooked-/blob/master/assets/github-icons/map5.gif">
   </td>
 </tr>
 <tr>
 </tr>
</table>

<p align="center">
  <img src="https://github.com/kwiruu/Undercooked-/blob/master/assets/github-icons/map_selector.png" width="60%">
</p>

<p>
<img src="https://github.com/kwiruu/Undercooked-/blob/master/assets/github-icons/chop.gif" width="49%">
<img src="https://github.com/kwiruu/Undercooked-/blob/master/assets/github-icons/cook.gif" width="49%">
</p>
<p>
<img src="https://github.com/kwiruu/Undercooked-/blob/master/assets/github-icons/drop.gif" width="49%">
<img src="https://github.com/kwiruu/Undercooked-/blob/master/assets/github-icons/mix.gif" width="49%">
</p>

## 
## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/lib`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).
- `java connector`: needed to communicate to the xampp.

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.

