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

usage: 

* boo [-a <arg>] [-c] [-f <FILE>] [--force] [--get-ips <environment>
       <compute-class>] [-h] [-l <arg>] [-m <description>] [--no-deploy]
       [--procedure <platform> <component> <action>]
       [--procedure-arguments <arglist>] [--procedure-instances
       <instanceList>] [--procedure-step-size <size>] [--quiet] [-r]
       [--retry] [-s] [-u]
* -a,--assembly <arg>                          Override the assembly name.
* -c,--create                                  Create a new Assembly specified by
                                              -f. If Assembly automatic
                                              naming is enabled, each invocation
                                              will create a new Assembly.
                                       
* -f,--config-file <FILE>                      Use specified configuration file,
                                       
* --force                                      Do not prompt for --remove
 
* --get-ips <environment> <compute-class>      Get IPs of deployed nodes specified
* --get-ips <environment>                      by -f
* --get-ips
 
* --no-deploy                                  Create assembly without
                                              deployments
                                      
* -h,--help                                    show help.
 
* -l,--list                                    Return a list of instances applicable 
                                              to the identifier provided..
* -m,--message <description>                   Customize the comment
                                              for deployments
* --no-deploy                                  Create assembly without
                                              deployments
* --procedure <platform> <component> <action>   Execute actions. Use
                                              'list' as an action to
                                              show available actions.
* --procedure-arguments <arglist>               Arguments to pass to the
                                              procedure call. Example:
                                              '{"backup_type":"increme
                                              ntal"}'
* --procedure-instances <instanceList>          Comma-separated list of
                                              component instance
                                              names. 'list' to show
                                              all available component
                                              instances.
* --procedure-step-size <size>                  Percent of nodes to
                                              preform procuedure on,
                                              default is 100.                                       
* --quiet                                      Silence the textual output.
    
* -r,--remove                                  Remove all deployed configurations
                                              specified by -f
                                       
* --retry                                      Retry deployments of configurations
                                              specified by -f
                                       
* -s,--status                                  Get status of deployments specified
                                              by -f
                                       
* -u,--update                                  Update configurations specified by
                                              -f.
                                       
The tool is managed by BFD team.
