[![Build Status](https://travis-ci.org/openmrs/openmrs-module-metadatamapping.svg?branch=master)](https://travis-ci.org/openmrs/openmrs-module-metadatamapping)

openmrs-module-metadatamapping
==============================

## Description
Provides an easy & explicit way for metadata within a system (encounter types, location, etc.) to be mapped to external vocabularies.

The aim is to solve many of the problems around metadata management by making it easier for developers to refer to metadata, simplify the synchronisation of metadata cross systems, etc. 

## Deployment Steps
A “1.0” version of Metadata Mapping will include a MetadataService within openmrs-core and REST API endpoints to manage & search metadata mappings.

## Developer Documentation
We use JIRA to track issues and monitor project development. Issue management is at:
* Versions after 1.1: [ https://issues.openmrs.org/browse/MAP](https://issues.openmrs.org/browse/MAP) 
* Versions before 1.1:  [https://issues.openmrs.org/browse/META](https://issues.openmrs.org/browse/META)

Before creating a pull request, please check out  [OpenMRS Pull Request Tips](https://wiki.openmrs.org/display/docs/Pull+Request+Tips) , and run code review tools (Lint) and all tests.

REST api is not available yet so the only option is to use Java APIs directly from your modules. 

### Code Style
The coding conventions used by OpenMRS are outlined  [here](https://wiki.openmrs.org/display/docs/Java+Conventions).

### Model pattern
The data model is based on the design at [this page](https://wiki.openmrs.org/pages/viewpage.action?pageId=6619649).

Some notes on the data model design:
* `metadata_source` is used to define a unique source (authority) for each namespace of metadata terms.
	* `name` should be fully qualified and universally unique.
* `metadata_term_mapping table` provides both the term and its mapping to local metadata.
	* `code` should be unique within the given source.
	* `metadata_class` refers to the Java class for the metadata.
	* `metadata_reference` is a unique reference to the metadata within the class (e.g., uuid)
* `metadata_set` is used to define relating grouping of metadata similar to what OpenMRS has traditionally done within global properties and similar to FHIR’s ValueSet for metadata terms.
	* `sort_weight` is used to optionally give members of a metadata set a reliable sequence.

### API Design

Common operations for metadata mapping will include these use cases:
* Manage mappings: add / remove mappings, support bulk operations
* Search for mappings by source / code / metadata object
* Search for metadata: given a mapping UUID or source+code, return the OpenmrsMetadata instance

## Release Notes
* 1.1.0 (unreleased)
	* REST api (MAP-8)
* 1.1.0-alpha1
	* Refactor ConceptSource methods to eliminate ambiguity (MAP-14)
	* Map metadata with MetadataTermMappings under a namespace defined by a MetadataSource (MAP-3, MAP-5, MAP-7, MAP-10, MAP-11, MAP-13)
	* Drop support for OpenMRS before 1.9.x (MAP-9)
	* Cleanup sources (MAP-2)
	* Format code (MAP-1)

## License
This project is licensed under the OpenMRS Public License, see the  [LICENSE](https://github.com/openmrs/openmrs-module-metadatamapping/blob/master/LICENSE)  file for details.

## Resources
For more information, check:
* Metadata Mapping Module: https://wiki.openmrs.org/display/docs/Metadata+Mapping+Module.
* Metadata Mapping (Design Page): https://wiki.openmrs.org/pages/viewpage.action?pageId=6619649
* Using Git: https://wiki.openmrs.org/display/docs/Using+Git
