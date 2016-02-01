# desdemona

[![Build Status](https://travis-ci.org/RackSec/desdemona.svg?branch=master)](https://travis-ci.org/RackSec/desdemona)
[![codecov.io](https://codecov.io/github/RackSec/desdemona/coverage.svg?branch=master)](https://codecov.io/github/RackSec/desdemona?branch=master)

Experiments in data-driven security operations using stream
processing.

## Usage

### Launch the Sample Job in Development

Run the `deftest`s in `test/desdemona/jobs/sample_job_test.clj`. The
tests automatically start and stop the development environment, so
make sure you don't already have the dev environment (explained below)
running - otherwise you'd get a port conflict.

### Using from a REPL

Fire up your favorite REPL or use `lein repl` (ideally with `rlwrap` so you
get reasonable line editing). You will be placed in the `user` namespace,
which should come pre-loaded with a number of functions to help you set up a
development environment, notably:

 * `(go 4)` sets up a development environment with 4 peers.
 * `(reset)` resets your development environment.
 * `(stop)` stops your development environment.

You can launch a sample job as follows:

```clojure
(user/go 4)
(require 'desdemona.jobs.sample-submit-job)
(desdemona.jobs.sample-submit-job/submit-job user/system)
```

### Production Mode Peers

First start the Aeron media driver, which should be used in production
mode, by running the main function in
`src/desdemona/launcher/aeron_media_driver.clj`.

Then launch the `src/desdemona/launcher/launch_prod_peers.clj` main
function, giving it an Onyx ID. Optionally, a Dockerfile has been
created at the root of the project to package this up into a
reproducible Java 8 environment.

### Launch the Sample Job in Production

Launch the `src/desdemona/launcher/submit_prod_sample_job.clj` main
function, giving it an Onyx ID and ZooKeeper address.

## License

Copyright © 2015 Rackspace Hosting, Inc.

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
