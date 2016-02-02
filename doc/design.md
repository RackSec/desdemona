# Assumptions

Desdemona is based on a number of working assumptions, which this
document aims to outline.

## Great SIEMs are data-driven correlation machines.

All our user studies so far point in the same direction: a successful
SIEM is one that enables its users (SOC analysts) to find and
highlight correlations.

## The user is smart.

We're assuming that our users are highly competent professionals with
varying levels of experience between information security, system
administration, and data science. It is more important that we provide
power users with power tools than it is to make it easy for people
without that experience to do anything, and it is certainly more
important than providing useless (but pretty) pictures.

## The user's computer is powerful and has a fast network connection.

We're assuming that the consumer has a powerful computer with lots of
memory, and we rely on that to provide good UX. Desdemona can stream
lots of data to users' computers. Clients can use that data to give
approximate answers quickly to see if a particular avenue is worth
exploring, allowing for faster response times and increased
productivity.

Ideally, we can reuse logic on client and server sides; but if that's
actually possible or useful remains an open research question.

## We have tons of data, but interest follows a power law.

Desdemona eventually archives essentially everything it sees. That
includes syslog data, netflow data and sometimes even PCAP
data. However, the data you care about at any given time is extremely
likely to be a part of a small subset:

 * Recent data
 * Data part of ongoing incidents
 * Correlated, refined alerts

This only means that you don't care about archived, global data
_often_, not that it's unimportant. For example, archived global data
can be extremely valuable as a baseline reference, either for a human
to consume, or for example as a training set for machine learning
algorithms. You just care about it much less _frequently_.

## Latency requirements have pronounced modes

Latency requirements fall in three main groups:

1. ~1-10s: feature detection in incoming streams.
2. ~1-10m: queries over increased amounts of data; new aggregate data.
3. ~1-10h: training machine learning models, high-level overviews and
   metrics

# Guidelines

## Enrichment and correlation should be pure functions

By making functions that we apply to our source data pure, we only
_have_ to store our source data. We then have more freedom to decide
what to cache (precompute) and compute on the fly.
