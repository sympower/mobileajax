---------------------------------------------
    Sample Java ME GPS app
---------------------------------------------

1. Introduction

   This project contains a sample MIDlet that connects to an external GPS
   receiver over Bluetooth and retrieves its GPS coordinates. The coordinates
   are then used to retrieve a map centered at the GPS coordinates from the
   Yahoo! Map Image API. The map can be zoomed, but panning or caching is not
   implemented.
   
   For more information please see:
     https://meapplicationdevelopers.dev.java.net/mobileajax.html

2. Required APIs

   JSR 30 - Connected Limited Device Configuration (CLDC) 1.0
   JSR 37 - Mobile Information Device Profile (MIDP) 1.0
   JSR 82 - Java APIs for Bluetooth

3. Tools

   The project can be built easily using the following tools -

   1. Java SE version 6.0 or higher
   2. NetBeans IDE 5.5.1 or higher
   3. NetBeans Mobility Pack 5.5.1 for CLDC or higher

4. Setup

   1. Launch NetBeans.

   2. Open the project in NetBeans.

   3. Resolve dependencies, if necessary. If the project name is in red,
      resolution is needed. Right-click on the project name and select
      Resolve Reference Problems (towards the bottom of the menu).

      Resolve the location of the request project. Click Resolve,
      navigate to the request project directory and select it. This
      should fix the reference problem.

      Resolve the location of builtin.ks, if necessary. Click Resolve,
      navigate to the NetBeans user directory and select Open. This
      directory is usually $HOME/.netbeans on Solaris and Linux and
      something like C:\Documents and Settings\<user>\.netbeans on
      Windows. This should fix the reference problem.

   4. Get an Application ID from Yahoo! to be able to use their Map Image API.
      The App ID can be obtained from
      http://search.yahooapis.com/webservices/register_application. Update the
      MAP-APPID constant in MapCanvas.java with the Application ID received from
      Yahoo!

   5. Right-click on the project and select Build All Project
      Configurations.

5. Running

   1. Install MIDlet on a phone that supports JSR 82, Java APIs for Bluetooth.

   2. Starting the MIDlet initiates a Bluetooth discovery for a GPS device. Make
      sure a GPS receiver is within range, is turned on, is discoverable and is
      accepting connections. A list of discovered devices is presented. Select
      the GPS receiver and then select the Connect button. The MIDlet will then
      connect to the GPS receiver and continously retrieve updates as frequently as the
      receiver sends them. When the first GPS location fix is sent by the GPS
      receiver, the MIDlet downloads and displays a map centered at that
      location at a default zoom level (which can be changed by pressing the
      numeric keys - see MapCanvas.java for current key assignments).
