# Changelog: augero-db-manager

<!--- ***
*General: the purpose of this file is to keep a log of all versions of this project and the changes between versions.*
*Rules for the change log:*
- *The latest version information must be always on top.*
- *Provide an outline of the most important changes for every version.*
- *When a version contains breaking changes, include a hint in the change log to notify users.*
- *After version 1.0.0 of project was released, it's not allowed to introduce breaking changes without increasing the major version.*
- *Add a link to this changelog in the versioning section of the readme of your project*
*** -->

### [1.2.4-RC-1] - 2022-01-11
- Temporarily deactivated "Copy TEW" stage in Jenkinsfile due to build errors.

### [1.2.3-RC-1] - 2022-01-04
- Updated the parent pom (augero-parent) to the latest version - 1.10.3-RC-1 - in order to mitigate the Log4j vulnerability described in [CVE-2021-44228](https://nvd.nist.gov/vuln/detail/CVE-2021-44228)

### [1.2.2-RC-1] - 2021-12-16
- Remove monitoring configuration - not longer needed in project

### [1.2.1-RC-1] - 2021-11-22
- Switch to recent versions of node.js and @sap/sbf

### [1.2.0-RC-1] - 2021-11-15
- Secure db-manager against concurrent calls (bulkhead)  excluding create for inexistent databases

### [1.1.3-RC-1] - 2021-11-03
- Bug fix for create schema operation ERROR : "The service instance name is taken:.."

### [1.1.2-RC-1] - 2021-09-15
- Enhanced API to manage (get and delete) schemas by schema instance name only

### [1.1.1-RC-1] - 2021-08-25
- Fix fortify vulnerability by adding URL validation

### [1.1.0-RC-1] - 2021-06-30
- updated the java code to java 11:
    - updated mta.yaml for java 11 deploy (buildpack and runtime) 
    - updated the module dependencies to the latest (java 11) versions
        - augero-parent -> 1.9.0-RC-1
        - augero-logging -> 2.3.0-RC-1
        - augero-core -> 1.1.0-RC-1
        - augero-security -> 1.1.0-RC-1
    - cleaned up pom to remove redundant plugins and version definitions
    - added jaxb reference to pom


### [1.0.23] - 2021-05-25
- Create release version `1.0.23`
- Fix SPV can not be subscribed on Client2 subaccount
- Fortify bug fix: Insecure binding configuration
- Fortify bug fix Race Condition: Singleton Member Field
- Fortify bug fix: "JSON Injection"
- Fortify bug fix: "Null Dereference"
- Fortify bug Header Manipulation

### [1.0.22] - 2021-05-21

- Fortify bug fix: Insecure binding configuration

### [1.0.21] - 2021-05-21

- Fix SPV can not be subscribed on Client2 subaccount
- use release versions of augero-core, augero-logging, augero-security

### [1.0.20] - 2021-05-20

- Fortify bug fix Race Condition: Singleton Member Field

### [1.0.19] - 2021-05-19

- Fortify bug fix: "JSON Injection"

### [1.0.18] - 2021-05-19

- Fortify bug fix: "Null Dereference"

### [1.0.17] - 2021-05-19

- Fortify bug Header Manipulation

### [1.0.16] - 2021-05-19

- Reverted Fortify bug fix Privacy Violation: Heap Inspection

### [1.0.15] - 2021-05-14

- Fortify bug fix: "Privacy Violation: Heap Inspection"

### [1.0.14] - 2021-05-04
- Create release version `1.0.14`

### [1.0.13] - 2021-04-30

- add NewRelic monitoring
- remove NewRelic source from jacoco scan in pom.xml

### [1.0.12] - 2021-04-30

- updated augero-logging to current version 2.2.0-RC-1 to support tenantid in logentries
- updated augero-core to current version 1.0.3-RC-1
- updated augero-security to current version 1.0.0-RC-1

### [1.0.11] - 2021-04-22

- Replaced references to the hardcoded deploy CF space with the value taken from the environment variables (
  VCAP_APPLICATION)
- Updated [README](./README.md) file to include instructions for the Credential Store setup.

### [1.0.10] - 2021-04-16

- updated common augero libraries to the latest published RC versions:
    - augero-parent -> 1.8.6-RC-1
    - augero-core -> 1.0.1-RC-1
    - augero-security -> 0.3.0-RC-1
    - augero-logging -> 2.1.1-RC-1

### [1.0.9] - 2021-04-13

- updated the README file according to Cerner [Development Guideline](https://wiki.cerner.com/pages/viewpage.action?pageId=1936397697)  template

### [1.0.8] - 2021-03-25

- added a credentialstore file [credential-store-test-space.json](./credential-store-test-space.json) to connect the user-provided-service with the credentialstore in test space for the dev environment
- added a step in Jenkins file for setup a create-user-provided-service as onboarding-credentialstore
- override propertie dbManager.spaceGuid from application.properties with DBMANAGER_SPACEGUID: ${space-guid} in mta.yaml file
- changed onboarding-credentialstore service to type org.cloudfoundry.existing-service
- changed [.npmrc](./augero-db-manager-broker/.npmrc) with path for cernerrepos.net registry as the previous eu2.cernerrepos.net gave errors

### [1.0.7] - 2021-03-24

- reverted [.npmrc](./augero-db-manager-broker/.npmrc) changes, bringing it back to the initial version

### [1.0.6] - 2021-03-24

- changed [.npmrc](./augero-db-manager-broker/.npmrc) with another path for the registry as the previous was incorrect and gave errors
- reverted lodash to default version fetched by @sap dependencies

### [1.0.5] - 2021-03-24

- Updated the lodash library in [package-lock.json](.augero-db-manager-broker/package-lock.json) 4.17.15 -> 4.17.20
- added versioning and CHANGELOG (this file)

### [1.0.4] - 2021-03-23

- updated [.npmrc](./augero-db-manager-broker/.npmrc) file to use eu registry for faster install
