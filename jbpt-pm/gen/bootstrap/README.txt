This Java project implements the bootstrap generalization method presented in:

Artem Polyvyanyy, Alistair Moffat, Luciano Garcia-Banuelos: Bootstrapping
Generalization of Process Models Discovered from Event Data. CAiSE 2022: 36-54.

This project then uses the implementation to conduct an extensive experiment to
assess the performance of the method.

To replicate the experiment, follow these steps:

1. Unzip logs.zip, models.zip, and systems.zip in the folder these archives are stored.
2. Specify the number of threads to be used to execute the experiment by
   changing the numOfTreads variable value in
   BootstrapGeneralizationBigExperimentRandomInputs.java
3. Run BootstrapGeneralizationBigExperimentRandomInputs.java

Inputs for the experiment are taken from "./inputs_r.csv".

Results of the experiment are written in "./results.csv".

Errors encountered during the experiment are written in "./errors.csv".

The "tmp" folder stores temporary event logs generated during the experiment.

Alternatively, you can execute the experiment by running 
"./gen-experiment.bat"; this experiment uses four threads.
