# augero-db-manager

Service that provides HANA database onboarding and multitenancy support for persisting and retrieving data. 

See more information on the [wiki page](https://wiki.cerner.com/x/3j30jg).

---

*General: This readme is targeted at internal audiences with the purpose of helping getting a quick overview of the project contents and how to run the project. Technical details can be found in referenced documentation.*

<!---
*Add badges to make the current status of the repository transparent
*Use following badges where appropriate:
-   Build status (Jenkins build)
-   Sonar quality gate (Frontend and/or Backend)
-   Sonar code coverage (Frontend and/or Backend)
-->
|                                                                               Jenkins build                                                                                |                                                                                                                           Backend Quality Gate                                                                                                                           |                                                                                                                     Backend Coverege                                                                                                                      |
| :-----------------------------------------------------------------------------------------------------------------------------------------------------------------------: | :----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: |
| [![Build Status](https://jenkins.cerner.com/rom/buildStatus/icon?job=augero-db-manager%2Fmaster)](https://jenkins.cerner.com/rom/job/augero-db-manager/job/master/) | [![Quality Gate Status](https://sonarqube01.euip.devcerner.net:9010/SonarQubeEnterprise/api/project_badges/measure?project=augero-db-manager&metric=alert_status)](http://sonarqube01.euip.devcerner.net:9000/SonarQubeEnterprise/dashboard?id=augero-db-manager) | [![Coverage](https://sonarqube01.euip.devcerner.net:9010/SonarQubeEnterprise/api/project_badges/measure?project=augero-db-manager&metric=coverage)](http://sonarqube01.euip.devcerner.net:9000/SonarQubeEnterprise/dashboard?id=augero-db-manager) |


## Overview

For multitenant applications there are multiple possibilities to separate the data for different tenants in the database:
- a tenant id column in all tables using one schema in one database
- a own schema for each tenant in one database
- a own database for each tenant

SAP proposes to use own schemas for each tenant which the HANA database supports with tenant databases either as simple schema or as hdi container.

There is basically a own managed-hana service implementing a instance manager which provides REST APIs to create such tenant databased at runtime when a new tenant subscribes.<br>
But this managed-hana is not available for clients/partners.

More general information can be found in the [Wiki](https://wiki.cerner.com/x/h24chw).

The augero-db-manager service provides REST APIs to create and delete SAP HANA Schema service instances at runtime and for returning the credentials for the created service instances.

### Repository contacts

<!--- *Who is the responsible for the content of this project/repository. This can be developer(s) which are familiar with the code, key developer(s) and/or team architects.* -->
-   [george.nistor@cerner.com](mailto:george.nistor@cerner.com) (developer)
-   [norbert.paukner@cerner.com](mailto:norbert.paukner@cerner.com) (developer)
-   [rucsandra.craciun@cerner.com](mailto:rucsandra.craciun@cerner.com) (tester)
-   [tobias.klitzke@cerner.com](mailto:tobias.klitzke@cerner.com) (architect)

### Contents
<!---
*Which sub-projects do exist* -->

This projects is composed by 2 modules:

- [augero-db-manager-broker](./augero-db-manager-broker) - standard SAP service broker - manages the complete lifecycle of the service.
- [augero-db-manager-service](./augero-db-manager-service) - the service that offers the API for creating and deleting database schemas and retrieving credentials.

The service broker is needed to register the augero-db-manager-service in the [SAP Service Marketplace](https://help.sap.com/viewer/09cc82baadc542a688176dce601398de/LATEST/en-US/affcc245c332433ba71917ff715b9971.html) so that other apps/services can create a service instance of it and use it.

## Setup
<!---
*How to run the projects (for local execution and for test execution)* -->

### Credential Store setup

The functionality of the db-manager is dependent on the existence of a _[Credential Store](https://help.sap.com/viewer/601525c6e5604e4192451d5e7328fa3c/Cloud/en-US/02e8f7d1016740b8adf68690f36df142.html)_ instance set up in a certain way, as, in the current version, the values that the db-manager interacts with are hardcoded in the application.

In order to set up the Credential Store instance for the use of the db-manager, the following values need to be set:
- name: **onboarding-credentialstore**
- plan: **standard**

You need to create a service key (securestore-key).

Inside the Credential Store (go to Manage Instance in cockpit) the following namespaces need to be created:
    
- **augero.managedDb**

Inside this namespace a credentials entry of type _Password_ needs to be created, with the following attributes:

- type: **Password**
- name: **cfUserPwd**
- username: **\<a cloud foundry user with rights to create, access and delete database and schema instances\>**

At this moment we use for this purposes the _scpautomation@cerner.com_ user, but any user - or a specially created user - with the aforementioned rights can be used.

>**IMPORTANT!**
>
>The user must be a member of the space where db-manager and the Credential Store reside

- value: **\<the password of the user\>**
  
After entering the password in Credential Store, it will no longer be visible. The user/password combination will be retrieved by db-manager from the Credential Store and used to perform the desired operations on the database (obtaining a token).
- **augero.managedDb.tenantDatabases** - this namespace will be created by db-manager in order to keep the credentials for the created database and schema instances. 

More detailed info about setting up the Credential Store instance for the use of the db-manager can be found at: <https://wiki.cerner.com/x/3j30jg#AugeroFTDDBManagerService-CredentialStoresetup>.

### Authentication

For any operation requested for the service you need to authenticate with a token obtained from the relevant provider. For SCP you get one with a POST request to: https://developmentpaas.authentication.eu10.hana.ondemand.com/oauth/token

You provide user / pass from the credentials of the Cloud Foundry space.

### Operation

In order to create a new schema for a specific module you need to send a POST request to:

`<service url>/v1/tenants`

with the following body:
```json
{
  "tenantGuid": "BigHospital",
  "module":     "triage",
  "space":      "clients"
}
```
Each tenant has a dedicated HANA Cloud database instance in which schemas for each software module are created.
If a database for the given tenant does not exist yet, it will be created automatically as part of the request and the schema will be created in it.
The name of the created database instance will be in the form db-`<tenant-id>` (so, for the example above, the name of the database instance will be: db-BigHospital). The name of the db instance should not exceed 50 characters.

The name of the schema created is an **uuid** automatically generated at creation, in order to avoid a name resulting from the module name exceeding the character limit.

The space just identifies the space wanted. At this point it does not have any effect on the actual space where the schema is created.

The admin password for the database created is hardcoded in the [configuration file](augero-db-manager-service/src/main/resources/application.properties) of the application.

Also, in the above mentioned configuration files, the parameters for creation of a database instance, and the space where the instance is to be created are defined.

These are as follows:
- the guid of the space where the service instance should be created
  (dev-common, spaceGuid=0c9a4a87-b6d8-47ae-9ebd-94315d7a3d62)
- for the creation of the HANA Cloud database instance:
    - RAM amount for the instance to be created at the minimal value for AWS (minimum 30 on AWS, 32 on Azure)
    - what IPs are allowed to connect to the database. The options are:
        - Deny all IP addresses (except SAP Cloud Platform)
        - Allow all IP addresses
        - Allow only specific IP addresses and IP ranges (in addition to SAP Cloud Platform)


From SAP Documentation:

_Specify IP address filter ranges using classless inter-domain routing (CIDR) notation. Up to 45 entries can be specified._

_CIDR notation is a compact way to specify an IP address and its associated routing prefix. The notation is made up of:_
- _the IP address_
- _a forward slash character(/)_
- _the number of leading 1-bits in the subnet mask._

_For example: 192.168.0.2/24 represents the IPv4 address 192.168.0.2 and its subnet mask 255.255.255.0, which has 24 leading 1-bits._

The application is hardcoded to allow all IP addresses.




The information about created SAP HANA Schema service instances is stored in a SAP Credential Store service. It contains the id of the tenant which subscribed to it.
It also supports multiple SAP HANA Schema service instances per tenant for different usages (like configuration data, medical data,...) and for different spaces if needed (like feature spaces for development).<br>
It contains a subscription counter in case multiple subscribeable applications should reference the same schema.<br>
The schemas are not deleted immediately when the last subscriber unsubscribes, instead the status is set to MARKED\_FOR\_DELETION so that if someone unsubscribes by accident that the data is still there.<br>
_Note_: a CF user with permission to create service instances is needed and has to be configured in the application.properties.

### <a name="localrun"></a>Local run
In order to run the application locally, you need to open a console and follow this steps:
1. login to the SAP Cloud Foundry using the command:
```bash
cf login -u <your_email>@cerner.com
```
Provide the password when prompted. 

If you are already logged in to CF, even in another console, and your token didn't expire, you can skip this step.

2. Select the appropriate Subaccount (org) (Cerner Health Services Deutschland GmbH_DevelopmentPaaS) and Space.

3. Navigate to the `augero-db-manager-service/launch-config/` directory
```bash
cd augero-db-manager-service/launch-config/
```
(depending on the console used, the actual command and format may be different)

4. Execute 
```bash
npm install
``` 
if needed

5. Execute 
```bash
npm run generate-config
```

6. In Eclipse, navigate to the newly created (or modified)  `augero-db-manager-service/launch-config/augero-db-manager-service.launch`. Right click on it and choose `Run As > augero-db-manager-service`.
The application now starts running locally and can be used from `localhost:8081`.

## Deployment

*How is the project deployed (configured automated deployment and manual deployment)*
### Automatic deployment
Automatic deployment is done by the Jenkins server following the setup from the [Jenkinsfile](./Jenkinsfile).
```groovy
stage('Deploy for master') {
    when {
        branch 'master'
    }
    steps {
        // This step runs deploy on SPACE 'dev-common'
        deployMbtMtar(workspace: env.WORKSPACE, organizationName: env.ORGANIZATION_NAME, spaceName: 'dev-common', mtarName: env.REPOSITORY_NAME)
        deployMbtMtar(workspace: env.WORKSPACE, organizationName: env.ORGANIZATION_NAME, spaceName: 'test', mtarName: env.REPOSITORY_NAME)
    }
}

stage('Deploy for feature branch') {
    when {
        expression {
            BRANCH_NAME ==~ /^feature-[0-9]+.*$/
        }
    }
    steps {
        deployMbtMtar(workspace: env.WORKSPACE, organizationName: env.ORGANIZATION_NAME, spaceName: env.BRANCH_NAME, mtarName: env.REPOSITORY_NAME)
    }
}
```
### Manual deployment
You need to be logged in in SAP Cloud Foundry, as described in point 1 from the [Local run](#localrun).

You need to have the [Cloud MTA Build Tool](https://github.com/SAP/cloud-mta-build-tool) installed. 

Make sure that you are in the "root" directory of the application (where the mta.yaml descriptor file resides).

Run the following command:
```bash
mbt build
```
A mtar archive will be generated in the `mta_archives` directory with the name and version defined in the mta.yaml file. If the version was not increased in mta.yaml, the existing archive, if any, will be overwritten. 

To actuall deploy this archive, you need to have the [MultiApps Cloud Foundry CLI Plugin](https://developers.sap.com/tutorials/cp-cf-install-cliplugin-mta.html) installed.

Run the command:
```bash
cf deploy <mtar_path_and_name>
```
The application is now deployed to the space targeted by your CF login.
## Versioning

See the [CHANGELOG](./CHANGELOG.md) for information about the latest changes and a version history.

## Further Information

<!--- 
*Links to Technical Module Description and other important documentation* -->
Technical Module Description: [Augero - TMD - SAP HANA Cloud integration](https://wiki.cerner.com/x/Tz30jg)

Feature Technical Description: [Augero - FTD - DB Manager Service](https://wiki.cerner.com/x/3j30jg)

