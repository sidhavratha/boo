# bfd-oneops-automation
[BFD OneOps Automation Tool](https://confluence.walmart.com/pages/viewpage.action?pageId=163659806)

*The precompiled executable jar under install/, skip the build steps if you don't want to compile java.*

*If you want to run mvn test, make sure have a valid test.yaml under src/test/resources and a local boo config (~/.boo/config).  See the Configuration section below.*

##Build to a executable command:
Run boo as a bash script:

1. Run: mvn clean package

2. Move the ./target/BFDOneOpsAutomation-1.0-executable.jar to your target server, and rename to boo.

3. Run boo: ./boo

##Build to a RPM:

1. Run: mvn clean package -DskipTests -Pbuild-rpms
2. Then install the BFDOneOpsAutomation-\<version\>.noarch.rpm to your server
3. After that, you can run boo command

##Install RPM:

1. On Linux: ```sudo rpm -ivh BFDOneOpsAutomation-1.0-1.noarch.rpm```
2. On Mac: ```sudo rpm -ivh BFDOneOpsAutomation-1.0-1.noarch.rpm --nodeps``` (Ignore errors if any)


## Usage:

```
boo -h
```

## Configuration

Boo YAML templates are processed with [Mustache][1] to allow variable interpolation when the standard `~/.boo/config` file contains a `default` profile. If you have a `default` profile that looks like the following:

```
[default]
host=https://oneops.prod.walmart.com
organization=devtools
api_key=oneops_api_key # copy from OneOps UI->profile->authentication->API Token
email=jvanzyl@walmart.com
cloud=prod-cdc6

[bfd]
host=https://web.bfd.dev.cloud.wal-mart.com
organization=bfd
api_key=oneops_api_key # copy from OneOps UI->profile->authentication->API Token
email=jvanzyl@walmart.com
cloud=bfd-cdd1
```

With a Boo YAML template that looks like this:

```
boo:
  oneops_host: '{{host}}'
  organization: '{{organization}}'
  api_key: '{{api_key}}'
  email: '{{email}}'
  environment_name: 'dev'
  ip_output: 'json'
  clouds:
    {{cloud}}:
       priority: '1'
       dpmt_order: '1'
       pct_scale: '100'

...

```

It will yield the following:

```
boo:
  oneops_host: 'https://oneops.prod.walmart.com'
  organization: 'devtools'
  api_key: 'oneops_api_key'
  email: 'jvanzyl@walmart.com'
  environment_name: 'dev'
  ip_output: 'json'
  clouds:
    prod-cdc6:
       priority: '1'
       dpmt_order: '1'
       pct_scale: '100'

...

```
If you want to see what your Boo YAML template will look like with interpolated values you can use the following command:

```
boo -f boo.yml -v
```

## Inlining File Content

You can use the `file` directive to inline content into a Boo template. The following are examples of how you can inline the content of files:

```
{{file(./id_rsa.pub)}}
{{file(~/.ssh/id_rsa.pub)}}
{{file(/Users/jvanzyl/id_rsa.pub)}}
```

So, for example, if you wanted to inline the content of your public SSH key into your Boo template before execution you can do the following:

```
user:
  user-jvanzyl:
    system_account: true
    sudoer: true
    username: 'jvanzyl'
    description: "JvZ"
    authorized_keys: '["{{file(~/.ssh/id_rsa.pub}})"]'
```

The result will look something like:

```
user:
  user-jvanzyl:
    system_account: true
    sudoer: true
    username: 'jvanzyl'
    description: "JvZ"
    authorized_keys: '["ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQC7g6cjv8yxH0pbo..."]'
```

## Testing

In order the the tests that require a live server you need your `default` profile to have a `host` setting for a OneOps instance:

```
[default]
host=https://oneops.prod.walmart.com
organization=oneops_org_name
api_key=oneops_api_key
email=me@email.com
cloud=cloud_name_in_oneops
```

The tool is managed by BFD team.

[1]: https://github.com/spullara/mustache.java
