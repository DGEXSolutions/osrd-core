# RAILJSON GENERATOR

## How to use

### Functions
- `Infra(lengths)`: build an empty infrastructure whose tracks' lengths are given in `lengths`. The track are indexed from 0 to `nb_tracks - 1` in the same order that they are given in `lengths`

- `Infra.add_link(first, second)`: add an undirected edge between to tracks given their indexes

- `Infra.add_switch(base, left, right)`: build a switch given three tracks indexes. The base track should be the first parameter

- `Simulation(infra)`: build an empty simulation from and infrastructure

- `Simulation.add_schedule(departure_time, departure_track, arrival_track)`: add a train which leaves at departure_time from the middle of departure_track and goes to arrival_track

### Some important rules
- no track can be isolated: a track should appear **at least one time** as argument of `add_link` or `add_switch`
- each track has only two ends: a track should appear **at most two times** as argument of `add_link`or `add_switch`

### Example

```
infra = Infra([1000] * 7)
infra.add_link(0, 1)
infra.add_link(1, 2)
infra.add_link(2, 3)
infra.add_switch(3, 4, 5)
infra.add_link(4, 6)
sim = Simulation(infra)
sim.add_schedule(0, 0, 6)
```

generates the infrastructure below

```                                                              
                                                                            <           
                                                                        +--o---------o--#
                                                                      v/  >    6
                                                                      o
                                                                     /^
                                                                  4 /
                                                                  v/
                                                                  o
       0      <     <   1     <     <   2     <     <   3     <  /^
#--o---------o--+--o---------o--+--o---------o--+--o---------o--+
            >     >         >     >         >     >         »   v\
                                                                  o
                                                                   \^
                                                                  5 \
                                                                     \
                                                                      o
                                                                       \
                                                                        #
```

 -----
## How it works

### Graph theory

Tracks are nodes that can be connected by two types of links
- link: a bidirectionnal edge between the two tracks
- switche: a triple (base, left, right) of tracks that creates two bidirectionnal edge: {base, left}, {base, right}

### Detectors, TVD sections, routes

#### Detectors
Each track can has two detectors at its ends, either buffer stops or TDE detectors according to how many tracks it is linked

- a track with one buffer stop `#` and a TDE detector `o`:
```
#------------o--+
```
- a track with two TDE detectors `o`
```
+--o---------o--+
```

#### TVD sections

A TVD section is a part of the network delimited by detectors. There are three type of TVD sections

- track: the part of the track between its two detectors
```
o---------o
```
- link: the part of two tracks linked together that is between the detectors
```
o--+--o
```
- switch: the part of the tracks delimited by the three detectors of a switch
```
     o
    /
o--+
    \
     o
```

#### routes
A route is a oriented path between two consecutive detectors and thus occupy only one TVD section

### Signals
Each entreance of a TVD section should be protected by a signal place before this entreance (= the detector)

- a `bal3_line_signal` is placed before the entreance detector of a linear TVD section (track or link type) whose other detector is not a buffer_stop, or before the entreance of a left/right detector of a switch TVD section
```
    >         >
--+--o---------o--+--o--

     v/
     o
    /
o--+
    \
     o
      \^
```
- a `check_route` is placed before the entreance detector of a linear TVD section whose other type is a buffer_stop
```
              <
#--o---------o--+
```
- a `switch_signal` is placed before the entreance of the base detector of a switch TVD section
```
        /
       o
      /
--o--+
 »    \
       o
        \ 
```


Notation on schemas (oriented from left to right) :
- `>` for a `bal3_line_signal`/`check_route detector`
- `»` for a `switch_signal`

### Schedules

A train schedule is mainly a path of routes which are pairs of detectors. It is computed in the infrastructure with a shortest path algorithm (Dijkstra). The train start at the middle of a track section and ends at the middle of another track section.
