infoplus-java-model
=====

Library containing generated Java model classes for processing the NS Infoplus data for train travel information in the Netherlands. 
Goal is to create a standard set of models that can be reused in JVM applications without resorting to generating your own from the WSDL/XSD.

This initial release contains models for just RIT, DVS and DAS as we test this library.

Supported
----
- _**RIT**info_ – These messages contain information about all the stops of a single journey/service and any changes to it.
- _**D**ynamische**V**ertrek**S**taat_ – These messages contain information about the departure times of journey/service at a single stop.
- _**D**ynamische**A**ankomst**S**taat_ – These messages contain information about the arrival times of journey/service at a single stop. 

Usage
---
Add the following to your Maven configuration

```maven
<dependency>
  <groupId>nl.bliksemlabs</groupId>
  <artifactId>infoplus-java-model</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

or Gradle
```gradle
  repositories {
      mavenCentral()
      maven {
          url = uri("https://maven.pkg.github.com/joelhaasnoot/infoplus-java-model")
      }
  }

  dependencies {
      implementation("nl.bliksemlabs:infoplus-java-model:1.0-SNAPSHOT")
  }
```

After that usage becomes trivial with a simple helper:
```java
ReisInformatieProductRitInfoType ritInfo = InfoplusMessage.parseRitInfo(ritInfoXmlString);
ReisInformatieProductDVSType dvs = InfoplusMessage.parseDVS(dvsString);
ReisInformatieProductDASType das = InfoplusMessage.parseDAS(dasString);
```

TODO
---
- Verstoringsinformatie: LAB, TRB, STB, VTT, VTL, VTS etc


Also see
---
- Alternative for Javascript: https://github.com/InterlockedSim/travel-info-types

Other transit models in Java
---
- NETEX: https://github.com/enturs/netex-java-model
- SIRI: https://github.com/entur/siri-java-model
- BISON KV6 https://github.com/bliksemlabs/kv6-java-model
- BISON KV15 https://github.com/bliksemlabs/kv15-java-model
- BISON KV17 https://github.com/bliksemlabs/kv17-java-model
- BUSON KV4 https://github.com/bliksemlabs/kv4-java-model
