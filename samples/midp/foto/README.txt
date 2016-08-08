---------------------------------------------
    Sample Java ME client for Flickr
---------------------------------------------

1. Introduction

   This project contains a sample client for Flickr. It uses the public
   Flickr API. It can browse "interesting" photos, and can upload photos taken
   from a phone's camera to a user's Flickr account, if the user completes
   Flickr's authentication process.

   The project has two configurations defined - the default configuration only
   allows browsing Flickr's "interesting" photos and should work on most phones.
   The CameraPhone configuration is meant for advanced phones that support JSRs
   135 and 177. This configuration allows a user to authenticate with Flickr and
   upload photos to their Flickr account.

   Uploaded photos can be geo-coded. The location is currently selected from a
   few preset locations, this needs to be enhanced to allow the location to come
   from a text field + geocoder, over Bluetooth from an external GPS receiver or
   via the Location API.
   
   For more information please see:
     https://meapplicationdevelopers.dev.java.net/mobileajax.html

2. Required APIs

   JSR 139 - Connected Limited Device Configuration (CLDC) 1.1
   JSR 118 - Mobile Information Device Profile (MIDP) 2.0

   Optional APIs

   JSR 135 - Mobile Media API (MMAPI) - for taking pictures
   JSR 177 - Java Crypto Extensions API (SATSACRYPTO) - for MD5

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

   4. Get an API Key from Flickr to be able to use their APIs. The API Key can
      be obtained from http://www.flickr.com/services/api/. Update the API_KEY
      and SHARED_SECRET constants in Foto.java with the values received from
      Flickr. If it is desired to geocode uploaded photos, an APP ID from Yahoo!
      will be needed to use the Yahoo! Geocoder API. Get this from
      http://search.yahooapis.com/webservices/register_application and update
      the GEOCODE_APPID constant in Foto.java.

   5. Right-click on the project and select Build All Project
      Configurations.
