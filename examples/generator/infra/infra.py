from infra.aspects import Aspects
from infra.scriptFunctions import ScriptFunctions
from infra.speedSections import SpeedSections
from infra.trackSection import TrackSection
from infra.point import Point
from infra.trackSectionLink import TrackSectionLink
from infra.switch import Switch
from infra.route import Route
from infra.tvdSection import TvdSection
from infra.detector import Detector
from infra.bufferStop import BufferStop
from infra.signal import Signal

from infra import *

class Infra:

    def __init__(self):
        self.aspects = Aspects.json()
        self.operationalPoints = []
        self.routes = []
        self.scriptFunctions = ScriptFunctions.json()
        self.speedSections = SpeedSections.json()
        self.switches = []
        self.trackSectionLinks = []
        self.trackSections = dict()
        self.tvdSections = []
        self.version = "1"
        self.successionTables = dict()
        self.degree = dict()
        self.neighboors = dict()

    def newPoint(self, trackId):
        assert self.degree[trackId] == 0 or self.degree[trackId] == 1
        self.degree[trackId] += 1
        if self.degree[trackId] == 1:
            return Point(self.trackSections[trackId], "BEGIN")
        else:
            return Point(self.trackSections[trackId], "END")

    def nextPoints(self, route):
        return [pt for pt in self.neighboors[route.ptArrival.id] if pt.id != route.ptStart.id]

    def addTrackSection(self, trackId, length, navigability = "BOTH"):
        trackSection = TrackSection(trackId, length)
        self.trackSections[trackId] = trackSection
        ptBegin = Point(trackSection, "BEGIN")
        ptEnd = Point(trackSection, "END")
        self.neighboors[ptBegin.id] = []
        self.neighboors[ptEnd.id] = []
        tvdSection = TvdSection([ptBegin, ptEnd])
        self.tvdSections.append(tvdSection)
        if navigability == "NORMAL" or navigability == "BOTH":
            self.routes.append(Route(ptBegin, ptEnd, tvdSection))
            self.neighboors[ptBegin.id].append(ptEnd)
        if navigability == "REVERSE" or navigability == "BOTH":
            self.routes.append(Route(ptEnd, ptBegin, tvdSection))
            self.neighboors[ptEnd.id].append(ptBegin)
        self.degree[trackId] = 0

    def addTrackSectionLink(self, start, arrival, navigability = "BOTH"):
        ptStart = self.newPoint(start)
        ptArrival = self.newPoint(arrival)
        self.trackSectionLinks.append(TrackSectionLink(ptStart, ptArrival, navigability))
        tvdSection = TvdSection([ptStart, ptArrival])
        self.tvdSections.append(tvdSection)
        if navigability == "NORMAL" or navigability == "BOTH":
            self.routes.append(Route(ptStart, ptArrival, tvdSection))
            self.neighboors[ptStart.id].append(ptArrival)
        if navigability == "REVERSE" or navigability == "BOTH":
            self.routes.append(Route(ptArrival, ptStart, tvdSection))
            self.neighboors[ptArrival.id].append(ptStart)
        
    def addSwitch(self, base, left, right, table, navigability = "BOTH"):
        ptBase = self.newPoint(base)
        ptLeft = self.newPoint(left)
        ptRight = self.newPoint(right)
        switch = Switch(ptBase, ptLeft, ptRight)
        self.successionTables[switch.id] = table
        self.switches.append(switch)
        self.trackSectionLinks.append(TrackSectionLink(ptBase, ptLeft, navigability))
        self.trackSectionLinks.append(TrackSectionLink(ptBase, ptRight, navigability))
        tvdSection = TvdSection([ptBase, ptLeft, ptRight])
        self.tvdSections.append(tvdSection)
        if navigability == "NORMAL" or navigability == "BOTH":
            self.routes.append(Route(ptBase, ptLeft, tvdSection, switch, "LEFT", False))
            self.neighboors[ptBase.id].append(ptLeft)
            self.routes.append(Route(ptBase, ptRight, tvdSection, switch, "RIGHT", False))
            self.neighboors[ptRight.id].append(ptLeft)
            self.trackSections[base].signals.append(Signal.signalSwitch(switch))
        if navigability == "REVERSE" or navigability == "BOTH":
            self.routes.append(Route(ptLeft, ptBase, tvdSection, switch, "LEFT"))
            self.neighboors[ptLeft.id].append(ptBase)
            self.routes.append(Route(ptRight, ptBase, tvdSection, switch, "RIGHT"))
            self.neighboors[ptRight.id].append(ptLeft)

    def addSignals(self):
        for route in self.routes:
            if route.addSignals:
                trackId = route.ptStart.trackSection.id
                self.trackSections[trackId].signals.append(Signal.signalBal3(route, self.nextPoints(route)))

            for k, v in self.degree.items():
                if v == 0:
                    self.trackSections[k].routeWaypoints.append(BufferStop(self.trackSections[k], "NORMAL"))
                    self.degree[k] += 1
                if v == 1:
                    self.trackSections[k].routeWaypoints.append(BufferStop(self.trackSections[k], "REVERSE"))
                    self.degree[k] += 1

    def json(self):
        return {
                "aspects": self.aspects,
                "operational_points": self.operationalPoints,
                "routes": [route.json() for route in self.routes],
                "script_functions": self.scriptFunctions,
                "speed_sections": self.speedSections,
                "switches": [switch.json() for switch in self.switches],
                "track_section_links": [trackSectionLink.json() for trackSectionLink in self.trackSectionLinks],
                "track_sections": [trackSection.json() for trackSection in self.trackSections.values()],
                "tvd_sections": [tvdSection.json() for tvdSection in self.tvdSections],
                "version": self.version
            }
