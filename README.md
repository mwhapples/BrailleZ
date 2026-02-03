# BrailleZ

BrailleZ is a simple Braille Ready File (BRF) editor
* Runs on Windows, Mac or Linux.
* Six key entry and display using the F D S and J K L keys.
* ASCII Braille entry and display.
* Ability to set page width (cells across) and depth (lines down).
* Bell for margin warning, user settable.
* Bell warning for end of line.
* Bell warning for end of page.
* Can save files in Braille ready file format or in BrailleZephyr format.
* Suitable for NLS certification tests.

## Getting BrailleZ

You can download binary builds of BrailleZ from the [download page](https://mwhapples.github.io/BrailleZ/download.html). Alternatively Windows users can find it in [the Microsoft Store](https://apps.microsoft.com/detail/9PJ223RQ7KBN). If your platform does not have a binary build, there is a chance you may be able to build your own copy, see the below instructions for building.

## Licence

BrailleZ is licenced under the GNU Public License (GPL) 3.0. A copy of the license is included with the software.  For details see the file LICENSE.txt.

BrailleZ is derived from BrailleZephyr from American Printing House for the Blind (APH). BrailleZephyr was licensed under the Apache 2.0 license. Any code which is copyrighted to APH may be used in accordance with the Apache 2.0 license. A copy of the Apache 2.0 license can be found in APACHE-LICENSE.txt.

The BrailleZephyr fonts are licensed under the SIL Open Font License 1.1. A copy of the license is included with the software.  For details see the file OFL.txt.

`SPDX-License-Identifier: GPL-3.0-only AND OFL-1.1 AND Apache-2.0`


## Building

Maven is the build system for BrailleZ.  Maven does not need to be installed as there is a wrapper that is included with the software that will download Maven automatically.  You issue build tasks using the wrapper script, which is mvnw on *nix systems and mvnw.cmd on Windows systems.

To build BrailleZ:
```console
./mvnw package
```

To build and run BrailleZ:
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
