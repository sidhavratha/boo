# bfd-oneops-automation
BFD OneOps Automation Tool
https://confluence.walmart.com/pages/viewpage.action?pageId=163659806

Build:

1. mvn clean package

2. move the /target/BFDOneOpsAutomation-1.0-executable.jar to your own folder, for example: mv ./target/BFDOneOpsAutomation-1.0-executable.jar /tmp/boo
3. run boo directly in tmp folder: ./boo

Usage:

boo create yarn

boo cleanup yarn

boo getip zookeeper

boo getip yarn


-c template_fullpath:

If not set, will use the default folder where the jar located. 
