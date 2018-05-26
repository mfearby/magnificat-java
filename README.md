# Magnificat
Magnificat will become a magnificent cataloguing app for classical music enthusiasts with large CD collections to organise. 

## Project Roadmap

This project is only a hobby so the following milestones may take some time to achieve. This project started out in Electron and Ext JS but will now be written in Java FX; the previous version can be seen here: [magnificat-electron](https://github.com/mfearby/magnificat-electron).

### Version 0.1
- Use Java FX to create an interface that allows users to open multiple tabs.
- Allow users to browse/play music on the filesystem.

### Version 0.2
- Allow users to browse/play music by genre, composer, album, etc (like the column browser in iTunes; requires a database which will need to be updated periodically).
- Save the play count of each track and allow users to enter a star rating.

### Version 0.3
- Allow users to edit tag information.
- Obtain tag details from file names based on user-entered format.

### Version 0.4
- Allow users to rename files based on tag information.
- Remember play count and last date for each track by saving information to a file in the current directory (when browsing/playing music by files and folders, not the iTunes-like column browser).

### Version 0.5
- Mini player support (with single-click buttons to switch between both views).

### Version 0.6
- Ripping CDs to MP3, OGG, and FLAC.
- Use CDDB et al for default tags.
 
### Version 0.7
- Rip CDs to FLAC and transfer existing file names and tag information from user-selected lossless equivalents on hard disk.

### Version 0.8
- Add more fields to the database to store things like CD label, conductor, orchestra, soloists, etc.
- More search features for finding music, for example "all CDs on the Decca label".
