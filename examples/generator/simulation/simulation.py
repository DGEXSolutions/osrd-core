from infra.infra import Infra
from infra.route import Route
from infra.point import Point
from heapq import heappush, heappop

class Simulation:

    ROLLING_STOCKS = [{'id': 'fast_rolling_stock', 'rolling_resistance': {'type': 'davis', 'A': 5400.0, 'B': 200.0, 'C': 12.0}, 'length': 20, 'max_speed': 80, 'startup_time': 10, 'startup_acceleration': 0.05, 'comfort_acceleration': 0.25, 'timetable_gamma': 0.5, 'mass': 900000, 'inertia_coefficient': 1.05, 'features': ['TVM300', 'TVM430', 'ETCS1', 'ETCS2', 'KVB'], 'tractive_effort_curve': [{'speed': 0, 'max_effort': 441666.6666666667}, {'speed': 5, 'max_effort': 439473.6842105263}, {'speed': 10, 'max_effort': 435714.28571428574}, {'speed': 15, 'max_effort': 427777.77777777775}, {'speed': 20, 'max_effort': 400000.0}, {'speed': 22, 'max_effort': 350971.5388299929}, {'speed': 27, 'max_effort': 347206.93642395496}, {'speed': 32, 'max_effort': 346938.7385068534}, {'speed': 37, 'max_effort': 344395.0325320009}, {'speed': 42, 'max_effort': 334314.2138640166}, {'speed': 47, 'max_effort': 313589.8108101956}, {'speed': 52, 'max_effort': 283584.5657113532}, {'speed': 57, 'max_effort': 250604.14937613969}, {'speed': 62, 'max_effort': 222698.71360301683}, {'speed': 67, 'max_effort': 204685.35097358702}, {'speed': 72, 'max_effort': 195984.55717992093}, {'speed': 77, 'max_effort': 192916.76425246376}]}]

    def __init__(self, infra):
        self.infra = infra
        self.trainSchedules = []

    def findPath(self, start, arrival):
        ptStartBegin = Point(self.infra.trackSections[start], "BEGIN")
        ptStartEnd = Point(self.infra.trackSections[start], "END")
        ptArrivalBegin = Point(self.infra.trackSections[arrival], "BEGIN")
        ptArrivalEnd = Point(self.infra.trackSections[arrival], "END")

        dist = dict() 
        parent = dict()
        q = [(0, ptStartBegin, None), (0, ptStartEnd, None)]
        while q != [] and not ptArrivalBegin.id in dist and not ptArrivalEnd.id in dist:
            d, u, p = heappop(q)
            if not u.id in dist:
                dist[u.id] = d
                parent[u.id] = p
                for v in self.infra.neighboors[u.id]:
                    heappush(q, (d + v.trackSection.length, v, u))

        u = ptArrivalBegin if ptArrivalBegin.id in dist else ptArrivalEnd
        pts = []
        while u != None:
            pts.append(u)
            u = parent[u.id] if u.id in parent else None
        pts.reverse()
        return pts

    def addSchedule(self, trainId, depature_time, start, arrival):
        pts = self.findPath(start, arrival)
        pts = [pts[0].otherSide()] + pts + [pts[-1].otherSide()]
        routes = [Route.routeId(pts[i], pts[i + 1]) for i in range(len(pts) - 1)]
        self.trainSchedules.append({
                "id": trainId,
                "rolling_stock": "fast_rolling_stock",
                "initial_head_location": {
                        "track_section": start,
                        "offset": self.infra.trackSections[start].length / 2
                    },
                "initial_route": routes[0],
                "initial_speed": 0,
                "phases": [
                        {
                            "driver_sight_distance": 400,
                            "end_location": {
                                    "track_section": arrival,
                                    "offset": self.infra.trackSections[arrival].length / 2
                                },
                            "routes": routes,
                            "type": "navigate"
                        }
                    ]
            })

    def json(self):
        return {
                "rolling_stocks": Simulation.ROLLING_STOCKS,
                "train_schedules": self.trainSchedules
            }
