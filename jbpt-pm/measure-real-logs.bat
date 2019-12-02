FOR %%i IN (logs\*.xes) DO java -jar jbpt-pm.jar -pr -ret=%%i
FOR %%i IN (logs\*.xes) DO java -jar jbpt-pm.jar -ppr -ret=%%i
FOR %%i IN (logs\*.xes) DO java -jar jbpt-pm.jar -pepr -ret=%%i
pause