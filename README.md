# cryptojacking-detector
This project is a module of Cryptojackingtrap solution, which is responsible for detecting cryptojacking activity. 
cryptojacking is a malicious process of mining cryptocurrencies without victims' consent in an application. The 
detector receives different input files that feed its algorithm to decide whether the suspicious application is 
malicious or benign. One of these files is from the monitor module responsible for monitoring suspicious activity. 
There are other modules for listening to different cryptocurrency networks, and each of these modules creates a 
separate output file that must be given to the detector module. We have developed two listener modules for Bitcoin 
and Ethereum until now, and detector modularity makes it easy to extend to other listeners and enhances the detector 
precious.

# Execution
The main class is DetectorExecutor and by running this class you will have the main UI that let you enter your files 
and preferences and execute the main algorithm of detector. 

# Contribution
Note: The tool is currently under development, please report any bugs you may find.

# user interface
The follwoing figure shows the user interface of the CryptojackingTrap detector that facilitates users to define their input files and configurations.



![CryptojackingTrap-Detector](https://user-images.githubusercontent.com/16403529/221629272-f55f7c25-ef2f-4344-b527-0c189a396055.png)
