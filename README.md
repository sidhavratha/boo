# bfd-oneops-automation
[BFD OneOps Automation Tool](https://confluence.walmart.com/pages/viewpage.action?pageId=163659806)

####The precompiled executable jar under intall/, skip the build steps if you don't want to compile java.
####If you want to run mvn test, make sure have a valid test.yaml under src/test/resources.

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


## Usage:
```usage: boo [-c] [-d DIR] [-f FILE] [--get-ips platform component]
       [-h] [-l] [--quiet] [-r] [--retry] [-s] [-u]
       
 -c,--create                           Create a new Assembly specified by
                                       -d or -f. If Assembly automatic
                                       naming is enabled, each invocation
                                       will create a new Assembly.
                                       
 -d,--config-dir <DIR>                 Use all configuration files in
                                       given directory, required if -f not
                                       used
                                       
 -f,--config-file <FILE>               Use specified configuration file,
                                       required if -d not used
                                       
    --get-ips <platform> <component>   Get IPs of deployed nodes specified
                                       by -d or -f
                                       
 -h,--help                             show help.
 
 -l,--list                             List all YAML files specified by -d
                                       or -f
                                       
    --quiet                            Silence the textual output.
    
 -r,--remove                           Remove all deployed configurations
                                       specified by -d or -f
                                       
    --retry                            Retry deployments of configurations
                                       specified by -d or -f
                                       
 -s,--status                           Get status of deployments specified
                                       by -d or -f
                                       
 -u,--update                           Update configurations specified by
                                       -d or -f.
                                       
The tool is managed by BFD team.```
