# CryptojackingTrap Detector
This project is a module of Cryptojackingtrap solution, which is responsible for detecting cryptojacking activity. 
cryptojacking is a malicious process of mining cryptocurrencies without victims' consent in an application. The 
detector receives different input files that feed its algorithm to decide whether the suspicious application is 
malicious or benign. One of these files is from the monitor module responsible for monitoring suspicious activity. 
There are other modules for listening to different cryptocurrency networks, and each of these modules creates a 
separate output file that must be given to the detector module. We have developed two listener modules for Bitcoin 
and Ethereum until now, and detector modularity makes it easy to extend to other listeners and enhances the detector 
precious.

## Opening the Project in an IDE

This project is Maven-based, so you can use Maven commands to set it up for your specific IDE. 

### For IntelliJ IDEA
To open the project in IntelliJ IDEA, follow these steps:

1. Navigate to the root folder of the project (where the `pom.xml` file is located).
2. Run the following command:

   ```bash
   mvn idea:idea
   ```

## Execution
The main class is `DetectorExecutor.java` and by running this class you will have the main UI that let you enter your files 
and preferences and execute the main algorithm of detector. 

### Sample Data

All the data you may need is reproducible using the corresponding submodules available at [CryptojackingTrap](https://github.com/CryptojackingTrap/CryptojackingTrap). 

However, for a quick test of the detector, you can use the sample data provided in the current project at:  ```\detector\src\test\resources\sample-data ```

You can also find an example of how to provide these files through the user interface in the [User Interface](#user-interface) section.

## Contribution
Note: The tool is currently under development, please report any bugs you may find.

## User Interface
The follwoing figure shows the user interface of the CryptojackingTrap detector that facilitates users to define their input files and configurations.



![CryptojackingTrap-Detector](https://user-images.githubusercontent.com/16403529/221629272-f55f7c25-ef2f-4344-b527-0c189a396055.png)
