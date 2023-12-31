---
title: Microservice Domain Specific Language (MDSL) Quick Reference
author: Olaf Zimmermann
copyright: Olaf Zimmermann, 2019-2022. All rights reserved.
---

[Home](./index) &mdash; [Endpoint Type](./servicecontract) &mdash; [Data Type](./datacontract) &mdash; [Provider and Client](./optionalparts) &mdash; [Bindings](./bindings) &mdash; [Tutorial](./tutorial) &mdash; [Tools](./tools)


MDSL Grammar Quick Reference (Skeletons)
========================================


## Service Contract Skeleton
One can start from this, e.g., by copy-pasting into a text editor or [the Eclipse editor](./updates) that Xtext generates from the MDSL grammar. `[...]` are placeholders to be replaced with correct MDSL:

<!-- could feature "role" keyword introduced in V3 (optional) -->

~~~
API description [name]
usage context [visibility] for [direction] // MAP pattern tags (optional)

data type [name] [...] // reusable data contract elements (optional) 

endpoint type [name]  
  version x.y.z // semantic versioning information (optional) 
  serves as [role_enum] // MAP tag(s) (optional)
  exposes 
  	operation [name]
	  with responsibility [resp_enum] // MAP tag (optional)
	  expecting
	    headers [...] // optional 
		payload [...] // mandatory, e.g., {V}
	  delivering  
	    headers [...] // optional
		payload [...] // mandatory in request-response exchanges
	    reporting 
	  	[...] // see bottom of page for explanation (optional)
	protected by [...] // see bottom of page for explanation (optional)
~~~

### Usage Context 
The valid values for API *visibility* are: 
> PUBLIC_API | COMMUNITY_API | SOLUTION_INTERNAL_API

The API type or *direction* can be: 
> FRONTEND_INTEGRATION | BACKEND_INTEGRATION 

### Roles and Responsibilities 
MAP defines the following *roles* (of endpoint types): 
> PROCESSING_RESOURCE | INFORMATION_HOLDER_RESOURCE  
> OPERATIONAL_DATA_HOLDER | MASTER_DATA_HOLDER | REFERENCE_DATA_HOLDER
> DATA_TRANSFER_RESOURCE | LINK_LOOKUP_RESOURCE 
> COLLECTION_RESOURCE | MUTABLE_COLLECTION_RESOURCE
> VALIDATION_RESOURCE | TRANSFORMATION_RESOURCE


<!-- STATEFUL_PROCESSING_RESOURCE | STATELESS_PROCESSING_RESOURCE --> 

And the *responsibilities* (of operations) can be: 
> COMPUTATION_FUNCTION | RETRIEVAL_OPERATION 
> STATE_CREATION_OPERATION | STATE_TRANSITION_OPERATION
> STATE_REPLACEMENT_OPERATION | STATE_DELETION_OPERATION

All these enum values correspond to Microservice API Patterns (MAP) or their variants; please refer to the [pattern index](https://microservice-api-patterns.org/patterns/index) for more information.


## Data Contract Skeletons (for Operation Signatures)
Data types can be modelled under `data type` and then referenced in headers, payloads, and reports within operations (`expecting`, `delivering`, `reporting`) in the above skeleton. They can also be inlined directly in the operation definitions.

<!--
Type systems that can be listed: 
> MAP_TYPE | JSON_SCHEMA | XML_SCHEMA | PROTOCOL_BUFFER | AVRO_SCHEMA | THRIFT_TYPE | OTHER 
-->

Skeleton data contracts for `headers` and `payload` elements and `data type` definitions are:

* [Parameter Tree](https://microservice-api-patterns.org/patterns/structure/representationElements/ParameterTree.html) (ptree): `{ {subtree1}, {subtree2}, ap1, ap2, ...}`, can appear in pforest
* [Atomic Parameter](https://microservice-api-patterns.org/patterns/structure/representationElements/AtomicParameter.html) (ap), can appear in ptree and apl: `<<pattern>>"name": Role<Type>` (see below for explanations) 
* [Atomic Parameter List](https://microservice-api-patterns.org/patterns/structure/representationElements/AtomicParameterList.html) (apl): `(ap1, ap2, ...)`, can appear in ptree (note: can and should be modeled as a flat ptree)
* [Parameter Forest](https://microservice-api-patterns.org/patterns/structure/representationElements/ParameterForest.html) (pforest): `[ {ptree1}; {ptree2}; ... ]`, can only appear on top level of operation signature


### Atomic Parameter Syntax

The `ap` from the above contract skeleton resolves to `<<pattern>>"name": Role<Type>`. A first example featuring all parts hence is `<<API_Key>>"accessToken1":D<string>`. 

Pattern stereotype and type information are optional; either the role or the name of the representation element have to be specified. Theefore, more compact examples are `ID`, `D<bool>`, `L<string>`, and `"resultCounter":MD<int>`.

Furthermore, an abstract, unspecified element can be represented as `P` (for parameter or payload part). It must not contain a stereotype or  role and type information (but can have an identifier). 

### `<<pattern>>` Stereotype (optional)
The `<<pattern>>` stereotype can take one of the following values from the [Microservice API Patterns (MAP)](https://microservice-api-patterns.org/introduction) pattern language: 

> API_Key | Context_Representation | Request_Bundle | Request_Condition 
> Wish_List | Wish_Template | Pagination | Error_Report 
> Embedded_Entity | Linked_Information_Holder | Annotated_Parameter_Collection |
> Data_Element | Metadata_Element | Identifier_Element | Link_ELement
> Control_Metadata | Aggregated_Metadata | Provenance_Metadata 

`Data_Element` is the default; `L` is a shorthand for `<<Link_ELement>> D`, `ID` is short for `<<Identifier_ELement>> D` (or `<<Id_ELement>> D`), and `MD` is short for `<<Metadata_ELement>> D`. 

A pattern stereotype can also be assigned to other tree nodes (apls and ptrees). This is optional.

### `"name"` Identifier
An identifier can, but does not have to be defined (if the role information is present). It appears in double quotes. At present, names must start with a character and must not contain blanks. Special characters such as `-` are not permitted (yet).

### `Role` Element role stereotypes
The roles match the four [element stereotype patterns in MAP](https://microservice-api-patterns.org/patterns/structure/):

> D(ata) | MD (Metadata) | ID(entifier) | L(ink) 

Data corresponds to the [Data Element](https://microservice-api-patterns.org/patterns/structure/elementStereotypes/DataElement) pattern; the other mappings are straightforward as well.  

### `<Type>` Base types 
Finally, the base types are:

> bool | int | long | double | string | raw | void

<!-- You can also use any `STRING`, but in that case MDSL tools cannot do much with the specification (this might, for instance, be useful in early conceptualization work). -->
 
A default value (plain text) can be specified for explicitly modeled data types:

~~~
data type StatusCode "success": MD<bool> default is "true"
~~~

### Cardinalities (Multiplicity)

MDSL does not know an array construct as known from the type system in most programming languages. Cardinalities are marked with single-byte flags instead (as also used to define [regular expressions](https://en.wikipedia.org/wiki/Regular_expression)):

* `?` means that an element is optional (i.e., none or one instance may appear)
* `*` represents "any" (i.e., zero or more)
* `+` indicates that at least one instance of the specified element must appear (i.e., one or more)

The cardinality markers can be applied to Atomic Parameters, Atomic Parameter Lists,[^1] and Parameter Trees. Two examples are `"listOfIntegers":D<int>*` and `"optionalInformation": {"part1":D, "part2":D}?`. If there is no suffix, the default is `!` (exactly one, mandatory).

[^1]: Note that the Atomic Parameter List, introduced above, actually is closer to a sequence or a map in programming languages.

*Note:* The marker might be a bit hard to notice, especially when deeply nested structured are modeled. You can increase readability by introducing external data type definitions (see [data contracts](./datacontract) page). 

### Variability (Choice)

In the definition of a Parameter Tree `{...|...}` and an Atomic Parameter List `(...|...)`, you can express optionality: 

* `|` choice 

An example is `data type AnIntegerOrAString {D<int> | D<string>}`.

## Operation Skeletons (in Endpoint Types)

### Reporting 
MDSL has an specific construct for error handling such as fault elements or response codes (still *experimental*):

Add the following snippet to the specification of response messages (behind `delivering`):

~~~
reporting 
    error [...]
~~~

The placeholder `[...]` resolves to a data contract (see above). The simplest one is `P`. Some more advances examples are: 

* `error BadRequest "MoreInformation": {D<string>} 

<!--
* `error <<Error_Report>>"resourceNotFound": {"errorCode":D<int>, "errorMessage":D<string>}+`
* `error <<Error_Report>>{("code402":D<int>, "notAuthorized":D<string>) | ("code403":D<int>, "anotherMessage":D<string>)}`.
-->

<!-- TODO tbd: feature `status` and analytics`? move examples to service contract page? -->

The report elements can be modeled as data types as described under [data contracts (schemas)](./datacontract). Examples are: 

* `reporting error sampleErrorReport "soapFault": SOAPFaultElement`, with a previous definition:
* `data type SOAPFaultElement {"code":D<int>, "string": D<string>, "actor":D<string>, "detail":D<string>}`

Note that the `"soapFault":` identifier is optional.

### Security Policy
A security policy can be specified for each operation: 

* `protected by policy basicAuthentication "UserIdPassword":{"userId":ID<string>, "password":MD<string>}`

Note that the `"UserIdPassword":` identifier is optional.

The policy elements can be modeled as data types as described under [data contracts (schemas)](./datacontract) as well.

### State Transitions
State transitions may optionally be defined on operation level: 

* `transitions from "State1" to "State2"`

They appear after the `delivering` and `reporting` parts of the operation contract. 

### Compensation
One operation may be defined to undo another: 

* `compensated by undoOp1`

This optional specification element has to be the last one in an operation contract. 

### Example 

~~~
endpoint type HelloWorldEndpoint
data type SomeAtomicParameter D
exposes 
  operation op1 
    expecting payload D<string>
    delivering payload "some_data_type":SomeAtomicParameter
    reporting error sampleErrorReport "soapFault": SOAPFaultElement
    transitions from "State1" to "State2"
	  compensated by undoOp1

  operation undoOp1
    expecting payload "correlationIdentifier":D<int>  
    delivering payload D<bool> 
~~~

## Requirements and Orchestration

### Stories 

Stories have up to five parts; only role and activity are mandatory:

~~~
scenario ScenarioNN
  story StoryTba
   when "something has happened" // precondition
   a "customer and/or integrator" // role
   wants to "startProcess" in "location"// business activity 
   yielding "a result" // outcome
   so that "both actors are satisfied and profit is made" // goal 
~~~

See the page [Integration Scenarios and User/Job Stories](./scenarios) for more modeling options. 

### Integration Flows 

A basic flow looks like this:

~~~
flow FlowMM realizes ScenarioNN
event something_has_happened triggers command startProcess
command startProcess emits event startProcessCompleted
~~~

Branching, forking, and joining is also supported:

~~~
event type FlowInitiated
event type Event0, Event1, Event2
event type FlowTerminated

command type StartFlowCommand
command type Command1, Command2, Command3, Command4
command type TerminateCommand

flow SampleFlow
event FlowInitiated triggers command Command1 or Command2
command Command1 emits event Event1 
command Command2 emits event Event2
event Event1 and Event2 trigger commands Command3 and Command4
command Command3 emits event FlowTerminated
command Command4 emits event FlowTerminated
~~~

See the page [Orchestration Flows](./flows) for more modeling options.

<!--

### AsyncMDSL for Messaging APIs

TODO (future work)
-->

## Site Navigation

* [Home](./index), [Primer](./primer), [Tutorial](./tutorial)
* [Transformations](./soad) and [tools](./tools)
* Language specification: service [endpoint contract types](./servicecontract) and [data contracts (schemas)](./datacontract), [bindings](./bindings), [instance-level constructs](./optionalparts), [scenarios and stories](scenarios.md), [flows](flows.md)

*Copyright: Olaf Zimmermann, 2018-2022. All rights reserved. See [license information](https://github.com/Microservice-API-Patterns/MDSL-Specification/blob/master/LICENSE).*

<!-- *EOF* -->