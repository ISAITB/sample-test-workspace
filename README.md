# Introduction

This repository contains a complete, working test development workspace to develop [GITB TDL test cases](https://www.itb.ec.europa.eu/docs/tdl/latest/) using 
supporting [custom test services](https://www.itb.ec.europa.eu/docs/services/latest/) for deployment on the [Interoperability Test Bed](https://github.com/ISAITB/gitb).

For more information on the Interoperability Test Bed you can visit our [Joinup space](https://joinup.ec.europa.eu/collection/interoperability-test-bed-repository/solution/interoperability-test-bed).

# How to use this repository

This repository is the result of following all steps defined in the Test Bed's [complex test development guide](https://www.itb.ec.europa.eu/docs/guides/latest/developingComplexTests/index.html#guide-developingcomplextests).
If you are following this guide, you can clone this repository to directly access the completed setup.

You can also use this guide to set up a complete test bed instance, preconfigured with test cases and supporting services. To do this:

1. Ensure you have [Docker](https://www.docker.com) and [Docker Compose](https://docs.docker.com/compose/>) installed.
2. Issue `docker compose up -d`

Following this a Dockerised Test Bed service will be initialised and be pre-populated with a complete test setup. You can refer to the [related guide](https://www.itb.ec.europa.eu/docs/guides/latest/developingComplexTests/index.html#guide-developingcomplextests)
for details on the fictional specification tested, the defines test cases, the overall design, and necessary steps for test execution. This also gives
a details account on all files and folders included in this repository.

In terms of usage, the Test Bed's user interface is available at http://localhost:9000. The setup also includes two user accounts as follows:

* A community administrator (account `admin@po`) for configuration and administration tasks.
* An organisation administrator (account `user@acme`) to execute tests.

In both cases these accounts are set with a one-time password of `changeme` to be replaced upon initial login.