FOR %%i IN (logs\*.xes) DO java -jar jbpt-pm.jar -pr -rel=%%i
FOR %%i IN (logs\*.xes) DO java -jar jbpt-pm.jar -ppr -rel=%%i
FOR %%i IN (logs\*.xes) DO java -jar jbpt-pm.jar -pepr -rel=%%i
pause