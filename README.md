# BrailleZephyr

BrailleZephyr is a simple Braille Ready File (BRF) editor
* Runs on Windows, Mac or Linux.
* Six key entry and display using the F D S and J K L keys.
* ASCII Braille entry and display.
* Ability to set page width (cells across) and depth (lines down).
* Bell for margin warning, user settable.
* Bell warning for end of line.
* Bell warning for end of page.
* Can save files in Braille ready file format or in BrailleZephyr format.

## Getting BrailleZephyr

You can download binary builds of BrailleZephyr from the [download page](https://mwhapples.github.io/BrailleZephyr/download.html). If your platform does not have a binary build, there is a chance you may be able to build your own copy, see the below instructions for building.

## Licence

BrailleZephyr is licenced under the Apache 2.0 license.  A copy of the license is included with the software.  For details see the file LICENSE.txt.

The BrailleZephyr fonts are licensed under the SIL Open Font License 1.1. A copy of the license is included with the software.  For details see the file OFL.txt.


## Building

Maven is the build system for BrailleZephyr.  Maven does not need to be installed as there is a wrapper that is included with the software that will download Maven automatically.  You issue build tasks using the wrapper script, which is mvnw on *nix systems and mvnw.cmd on Windows systems.

To build BrailleZephyr:
```console
./mvnw package
```

To build and run BrailleZephyr:
```console
./mvnw package exec:exec
```

To clean the distribution:
```console
./mvnw clean
```


## Miscellaneous

Margin bell:
http://www.freesound.org/people/ramsamba/sounds/318687/

End of Line bell:
https://www.freesound.org/people/Neotone/sounds/75338/

Page bell:
https://www.freesound.org/people/anbo/sounds/34456/
