from infra.point import Point
from infra.detector import Detector

class TrackSection:

    def __init__(self, id, length):
        self.id = id
        self.length = length
        self.routeWaypoints = [
                Detector(self, "BEGIN", "NORMAL"),
                Detector(self, "BEGIN", "REVERSE"),
                Detector(self, "END", "NORMAL"),
                Detector(self, "END", "REVERSE")
            ]
        self.signals = []

    def json(self):
        return {
                "id": self.id,
                "length": self.length,
                "operational_points": [],
                "route_waypoints": [routeWaypoint.json() for routeWaypoint in self.routeWaypoints],
                "signals": [signal for signal in self.signals]
            }
