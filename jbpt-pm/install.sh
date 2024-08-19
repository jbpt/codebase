#!/bin/bash

mvn -f ../jbpt-core/pom.xml clean install
mvn -f ../jbpt-deco/pom.xml clean install
mvn -f ../jbpt-petri/pom.xml clean install
mvn install:install-file -Dfile="${project.basedir}/lib/StochasticAwareEntropy.jar" -DrepositoryLayout=default -DgroupId=org.processmining.stochasticawareconformancechecking -DartifactId=stochastic -Dversion=0.0.1 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile="${project.basedir}/lib/jbpt-pm-relevance-1.1-SNAPSHOT-jar-with-dependencies.jar" -DpomFile="${project.basedir}/lib/relevance-pom.xml"
mvn install:install-file -Dfile="${project.basedir}/lib/eigen-measure.jar" -DrepositoryLayout=default -DgroupId=org.processmining.eigenvalue -DartifactId=eigenvalue -Dversion=0.1.1 -Dpackaging=jar -DgeneratePom=true
mvn clean install
