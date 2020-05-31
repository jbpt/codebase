FOR %%i IN (logs\*.xes) DO java -jar jbpt-pm.jar -pr -skrel=1 -skret=1 -rel=%%i
pause