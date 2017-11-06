# bikeride-lagom


This project is a learning project to understand Lagom framework

The application is composed of a context of three aggregates:

- **Biker**: The person that participates on bike rides by subscribing to a ride.
- **Track**: Sequence of waypoints to be followed as part of a ride
- **Ride**: An event organized with limited number of participants to ride over a defined track

The application is developed to used the patterns of **ES** (event sourcing) and **CQRS**(command query responsbility segregation), following also the concepts of **DDD** Domain Drive Design.

 
Build and deployment instructions can be found at [the Kubernetes readme](https://github.com/fernandohackbart/bikeride-lagom/blob/master/Kubernetes/README.md).