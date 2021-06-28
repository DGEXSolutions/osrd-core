class TrackSectionLink:

    def __init__(self, ptStart, ptArrival, navigability):
        self.ptStart = ptStart
        self.ptArrival = ptArrival
        self.navigability = navigability

    def json(self):
        return {
                "begin": {"endpoint": self.ptStart.side, "section": self.ptStart.trackSection.id},
                "end": {"endpoint": self.ptArrival.side, "section": self.ptArrival.trackSection.id},
                "navigability": self.navigability
            }
