class PointGraph:

    class Link:
        def __init__(self, start, arrival, navigability):
            self.start = start
            self.arrival = arrival
            self.navigability = navigability

    class Switch:
        def __init__(self, base, left, right, navigability):
            self.base = base
            self.left = left
            self.right = right
            self.navigability = navigability

    class Navigability:
        NORMAL = 0
        REVERSE = 1
        BOTH = 2

    def __init__(self):
        self.tracks = dict()
        self.lengths = dict()
        self.links = []
        self.switches = []

    def addTrackSection(self, id, navigability = Navigability.BOTH):
        self.tracks = 
        self.neighboors[id] = []
        self

    def addLink(self, start, arrival, navigability = Navigability.BOTH):
        self.links.append(Link(start, arrival, navigability))
        if navigability == Navigability.NORMAL or navigability == Navigability.BOTH:
            self.neighboors[start].append(arrival)
        if navigability == Navigability.REVERSE or navigability == Navigability.BOTH:
            self.neighboors[arrival].append(start)

    def addSwitch(self, base, left, right, navigability = Navigability.BOTH):
        self.switches.append(Switch(base, left, right, navigability))

        if navigability == Navigability.NORMAL or navigability == Navigability.BOTH:
            self.neighboors[base].append(left)
            self.neighboors[base].append(right)
        if navigability == Navigability.REVERSE or navigability == Navigability.BOTH:
            self.neighboors[left].append(base)
            self.neighboors[right].append(base)

    def getNeighboors(self, track):
        return self.neighboors[track]

