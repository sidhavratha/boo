# bfd-oneops-automation
[BFD OneOps Automation Tool](https://confluence.walmart.com/pages/viewpage.action?pageId=163659806)

##Build to a executable command:
Run boo as a bash script:

1. Run: mvn clean package -DskipTests

2. Move the ./target/BFDOneOpsAutomation-1.0-executable.jar to your target server, and rename to boo.

3. Run boo: ./boo

##Build to a RPM:

1. Run: mvn clean package -DskipTests -Pbuild-rpms
2. Then install the BFDOneOpsAutomation-<version>.noarch.rpm to your server
3. After that, you can run boo command

##Install RPM:

1. On Linux: ```sudo rpm -ivh BFDOneOpsAutomation-1.0-1.noarch.rpm```
2. On Mac: ```sudo rpm -ivh BFDOneOpsAutomation-1.0-1.noarch.rpm --nodeps``` (Ignore errors if any)

####The precompiled executable jar and RPM under intall/.

## Usage:
usage: boo [-cd <yaml>] [-cf <yaml>] [-cleanup] [-create] [-h] [-l] [-s]

 -cd,--config_dir <yaml>    Use the config dir.
 
 -cf,--config_file <yaml>   Use specified template.
 
 -cleanup                   Remove all deployment in OneOps.
 
 -create                    Create a new deployment in OneOps.
 
 -h,--help                  show help.
 
 -l,--list                  List all YAML files.
 
 -s,--status                Check deployment status.
