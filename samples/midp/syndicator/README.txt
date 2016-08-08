----------------------------------------------------
    Sample Blog reader and publisher for Java ME
----------------------------------------------------

1. Introduction

   This project contains a sample blog reader and publisher for Java ME.
   It uses the atom library to parse feeds and publish feed entries. A
   configuration for camera phones shows how photos could be published
   to a blog directly from a phone's camera.
      
   For more information please see:
     https://meapplicationdevelopers.dev.java.net/mobileajax.html

2. Required APIs

   JSR 139 - Connected Limited Device Configuration (CLDC) 1.1
   JSR 118 - Mobile Information Device Profile (MIDP) 2.0
   JSR 172 - J2ME Web Services Specification (only for the XML parser - JAXP)

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

      Resolve the location of the atom project. Click Resolve,
      navigate to the atom project directory and select it. This
      should fix the reference problem.

      Resolve the location of builtin.ks, if necessary. Click Resolve,
      navigate to the NetBeans user directory and select Open. This
      directory is usually $HOME/.netbeans on Solaris and Linux and
      something like C:\Documents and Settings\<user>\.netbeans on
      Windows. This should fix the reference problem.

   4. Right-click on the project and select Build All Project
      Configurations.
