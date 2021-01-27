call mvn -f ../jbpt-core/pom.xml clean install
call mvn -f ../jbpt-deco/pom.xml clean install
call mvn -f ../jbpt-petri/pom.xml clean install
call mvn install:install-file -Dfile=${project.basedir}/lib/eigen-measure.jar -DrepositoryLayout=default -DgroupId=org.processmining.eigenvalue -DartifactId=eigenvalue -Dversion=0.1.1 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=${project.basedir}/lib/StochasticAwareEntropy.jar -DrepositoryLayout=default -DgroupId=org.processmining.stochasticawareconformancechecking -DartifactId=stochastic -Dversion=0.0.1 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=${project.basedir}/lib/jbpt-pm-relevance-1.0-SNAPSHOT-jdk1.8.jar -DpomFile=${project.basedir}/lib/relevance-pom.xml
call mvn clean install
