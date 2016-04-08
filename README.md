# desdemona

[![Build Status](https://jenkins.racksec.io/job/desdemona-master/badge/icon)](https://jenkins.racksec.io/job/desdemona-master/)
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

### Using Docker Compose

First, build the project:

```
docker-compose build
```

If you want to run the tests in Docker, you may now do so:

```
docker-compose run test
```

Start the cluster:

```
docker-compose up
```

Wait until it's all started. It should say this and then wait:

```
peer_1      | Started peers. Blocking forever.
```

Make sure you create the Kafka topic by connecting to the producer:

```
script/connect_kafka.sh
```

And the MySQL database:

```
script/connect_mysql.sh
```

Run this SQL, which is available in `resources/table.sql`:

```
use logs;
CREATE TABLE logLines (id int primary key auto_increment, line text);
```

Now you can submit a job:

```
script/submit_job.sh
```

Anything you send to syslog on that Docker host (there's a syslog-ng
relay running as a container) will appear in MySQL.

### Debugging `docker-compose up`

If you're using docker-machine, your VM should have at least 2048MB
RAM. This is because the Kafka container tries to pre-allocate quite a
bit of memory. If it has insufficient memory, you'll see the following
error message in the docker-compose output:

```
kafka_1     | # There is insufficient memory for the Java Runtime Environment to continue.
kafka_1     | # Native memory allocation (mmap) failed to map 1073741824 bytes for committing reserved memory.
```

You can fix this by increasing the amount of RAM in the VirtualBox VM
to 2048MB or more.

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

### Run the front-end for development

First, make sure you have [Leiningen](http://leiningen.org/) installed and then run `lein figsass` from the project directory. This will start a Leiningen server at `http://localhost:3449/` as well as automatically watch, compile and inject the project source files for you.

`lein figsass` is just an alias running two tasks in parallel: `scss :dev auto` and `figwheel`. For Sass compilation, project assumes you have [sassc](https://github.com/sass/sassc) binary installed. You can use different binary by changing the `:executable` under `:scss` inside `project.clj`.

## License

Copyright © 2015 Rackspace Hosting, Inc.

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
