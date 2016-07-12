# bfd-oneops-automation
BFD OneOps Automation Tool
https://confluence.walmart.com/pages/viewpage.action?pageId=163659806

Build:
Run boo as a bash script:

1. Run: mvn clean package

2. Move the ./target/BFDOneOpsAutomation-1.0-executable.jar to your target server, and rename to boo.

3. Run boo: ./boo

Build to a RPM:

1. Run: mvn clean package -DskipTests -Pbuild-rpms
2. Then install the BFDOneOpsAutomation-<version>.noarch.rpm to your server
3. After that, you can run boo command

```usage: boo [-cf <yaml>] [-cleanup] [-create] [-h] [-s]
 -cf,--config_file <yaml>   Use specified template.
 -cleanup                   Remove all deployment in OneOps.
 -create                    Create a new deployment in OneOps.
 -h,--help                  show help.
 -s,--status                Check deployment status.i```
