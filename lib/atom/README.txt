---------------------------------------------------
    A streaming Atom implementation for Java ME
---------------------------------------------------

1. Introduction

   This project contains an implementation of the Atom Feed Format for
   Java ME. It is designed as a streaming API with limits to prevent
   large feeds from overwhelming resource-constrained devices. Also
   included is limited support for the Atom Publication Protocol and
   RSS.
   
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

   4. Right-click on the project and select Build All Project
      Configurations.
