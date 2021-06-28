from infra.detector import Detector

class Route:
    def routeId(ptStart, ptArrival):
        return f"rt.{ptStart.trackSection.id}-{ptArrival.trackSection.id}"

    def __init__(self, ptStart, ptArrival, tvdSection, switch = None, switchPosition = None, addSignals = True):
        self.ptStart = ptStart
        self.ptArrival = ptArrival
        self.id = Route.routeId(ptStart, ptArrival)
        self.entryPoint = Detector.tdeId(ptStart, ptArrival)
        self.releaseGroups = [[tvdSection]]
        self.switchesPosition = dict()
        if switch != None:
            self.switchesPosition[switch.id] = switchPosition
        self.addSignals = addSignals

    def json(self):
        return {
                "id": self.id,
                "entry_point": self.entryPoint,
                "release_groups": [[tvdSection.id for tvdSection in releaseGroup] for releaseGroup in self.releaseGroups],
                "switches_position": self.switchesPosition
            }
