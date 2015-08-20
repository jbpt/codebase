# jBPT Developer Guide #

## Pre-Requisites ##

  * > Maven 2.x (http://maven.apache.org/)
  * > JDK 1.7
  * IDE of your choice

## Setup ##

  1. Install Maven as shown on the [Maven website](http://maven.apache.org/download.html). Just remember to include the "bin" directory in your PATH enviroment variable for convencience.
  1. Read the [Quick Start](http://maven.apache.org/run-maven/index.html#Quick_Start) guide. Building jBPT is as easy as running "mvn clean install" in the base directory. Binary will be saved in the "target" directory.
  1. If you're using Eclipse you should download and install [m2eclipse](http://www.eclipse.org/m2e/) through the Eclipse Marketplace or the mentioned Update Site. Note that sometimes the integrated Maven version is causing issues (maybe because of conflicts with an already installed Maven). If the dependencies can not be resolved try to configure Eclipse to use your manually installed Maven.
  1. Now depending on your IDE:
    * Eclipse: Choose import new project -> Maven -> Check out Maven Projects from SCM to checkout and setup jBPT directly as an Maven project
    * Others: Checkout jBPT to a directory of your choice
  1. You should now be able to build jBPT by typing "mvn clean install" at the base directory.
  1. In Eclipse you may need to run "Maven -> Update project" using the context menu on the new project to setup and download the project dependencies.

## Useful links ##
  * [Maven in 5 Minutes](http://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)