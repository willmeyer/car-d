# car-d

car-d is daemon software designed to run on a "Car PC" -- a full-function computer inside of an automotive environment.
It does things like:

* Interfaces via OBD-II with the car's telemetry systems
* Interfaces with a GPS receiver
* Exposes several interfaces that allow for basic interactivity and interrogation of the car's
  systems and position.  For example:
  * Instant-messaging (via Jabber or Google Talk)
  * A mobile-optimized web interface
  * A command-line interface
  * in-dash LCD text devices

See the Electronics section of my [Car PC Project Pages](http://www.willmeyer.com/things/car-hacking/electronics) for
more info on how all the software works together with the rest of the car.

car-d uses several packages which you might also find useful if you are programming against your car:

* [jOBDII](http://www.github.com/willmeyer/jobdii) for talking to the car via OBDII
* [jGPS](http://www.github.com/willmeyer/jgps) for talking to a GPS receiver
* [jFusionBrain](http://www.github.com/willmeyer/jfusionbrain) for talking to a FusionBrain
* [jLCD](http://www.github.com/willmeyer/jlcd) for talking to LCD text displays
* [Commander](http://www.github.com/willmeyer/commander) and [Commander IM](http://www.github.com/willmeyer/commander-im) for implementing command-based apps with multiple useful interfaces

### Requirements & Setup

This is fairly complicated as there are significant hardware dependencies.  Getting it running
as-is on your own setup is non-trivial, this is more useful as a learning tool. That said, feel free to
check out `card.conf` for some basic guidance and a starting point.

### FIXMEs

- make devices check ports and differentiate between total and temporary failure

- generalized component model
 - start/stop
 - friendly names
 - dependency declarations, or at least full restarts  

- rs232 passing responses back, errors, etc -- flesh out the STA model.

