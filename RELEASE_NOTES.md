# Release notes

## 2016-06-29 - 0.4-SNAPSHOT

* bumped version to 0.4-SNAPSHOT
* added newest dropsonde protocol version (git submodule magic)
* removed `ControlMessage` and `Heartbeat` as described in https://www.pivotaltracker.com/n/projects/993188/stories/96706488 and https://github.com/cloudfoundry/dropsonde-protocol/commit/6a986020f511f4a8571b6265b7df37b2c671fe02
* refeactored code to use new dropsonde java package name
* bumped 'wire' version to 2.2.0 to be able to work with `map` structure in `envelope.prot` (see https://github.com/cloudfoundry/dropsonde-protocol/commit/74d3c2d919a37a10dd264704903d46a89ac0c28c)
