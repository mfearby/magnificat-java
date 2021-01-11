# Magnificat
Magnificat will become a magnificent cataloguing app for classical music enthusiasts with large CD collections to organise. Version 0.1 is close to completion; here's what it looks like so far:

![Screenshot of version 1.0](screenshot.png)

There are no installers yet, but they are coming soon (right now you need to be a programming enthusiast to check out Magnificat and test it).


## Project Roadmap

This project is only a hobby so the following milestones may take some time to achieve. This project started out in Electron and Ext JS but will now be written in Java FX; the previous version can be seen here: [magnificat-electron](https://github.com/mfearby/magnificat-electron).

### Version 1.0
- Use Java FX to create an interface that allows users to open multiple tabs.
- Allow users to browse/play music on the filesystem.
- Remember the currently playing track when the program closes/reopens.

### Version 1.1
- Allow users to edit tag information.
- Obtain tag details from file names based on user-entered format.

### Version 1.2
- Allow users to rename files based on tag information.
- Remember play count and last date for each track by saving information to a file in the current directory (when browsing/playing music by files and folders, not the iTunes-like column browser).

### Version 1.3
- Ripping CDs to MP3, OGG, and FLAC.
- Use CDDB et al for default tags.
 
### Version 1.4
- Rip CDs to FLAC and transfer existing file names and tag information from user-selected lossless equivalents on hard disk.

### Version 2.0
- Allow users to browse/play music by genre, composer, album, etc (like the column browser in iTunes; requires a database which will need to be updated periodically).
- Save the play count of each track and allow users to enter a star rating.

### Version 2.1
- Add more fields to the database to store things like CD label, conductor, orchestra, soloists, etc.
- More search features for finding music, for example "all CDs on the Decca label".

### Other possible features
- Mini player support (with single-click buttons to switch between both views).