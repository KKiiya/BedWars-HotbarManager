# BedWars-HotbarManager
Lightweight addon for BedWars1058 and BedWars2023 that allows players to manage their hotbar with ease. Fully customizable and easy to use.
# How to compile
In order to build the jar, you must have Spigot Build Tools in your device so IntelliJ recognizes the libraries the dependencies in th. You can download it from [here](https://www.spigotmc.org/wiki/buildtools/). After downloading, you can run the following command to build the program:
```bash 
java -jar BuildTools.jar --rev <version> --remapped
```

## Windows
If you have newer or older versions of Java in your machine (Windows), sometimes the BuildTools will not work. In this case, you must install the version of Java that the BuildTools is asking for. You can download it from [here](https://adoptopenjdk.net/). After downloading, restart your shell and find the path to the java executable. After doing so, you can run the following command to set the path to the correct version of Java (Examples):
```bash
"C:\Program Files\Java\jdk-17\bin" -jar BuildTools.jar --rev <version> --remapped
```
```bash
"C:\Program Files\Java\jdk-21\bin\javaw.exe" -jar BuildTools.jar --rev <version> --remapped
```
Your shell will always take the latest version of Java, so you must set the path to the correct version of Java if needed.

## Linux
If you have newer or older versions of java, your shell will also use the latest version of Java. Please follow this [guide](https://linuxconfig.org/how-to-install-and-switch-java-versions-on-ubuntu-linux) on how to change the java path.

After doing so, you can run it normally:
```bash
java -jar BuildTools.jar --rev <version> --remapped
```

### You must run this for every version listed here:
- 1.8.8
- 1.12.2
- 1.16.5
- 1.17.1
- 1.18.2
- 1.19.4
- 1.20.1
- 1.20.2
- 1.20.3 or 1.20.4 (Same libraries)
- 1.21.1

This process takes some time, so please be patient. After the process is done, you can open the project in IntelliJ and build the jar with maven.

