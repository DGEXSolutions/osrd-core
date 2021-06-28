class Detector:

    TDE_SPACE = 100

    def tdeId(ptStart, ptArrival):
        trackSectionId = ptStart.trackSection.id
        side = ptStart.side
        direction = ptStart.direction(ptArrival)
        return f"tde.{trackSectionId}_{side}_{direction}"

    def __init__(self, trackSection, side, direction):
        self.trackSection = trackSection
        self.side = side
        self.direction = direction
        self.id = f"tde.{self.trackSection.id}_{self.side}_{self.direction}"
        if self.side == "BEGIN":
            self.position = Detector.TDE_SPACE
        else:
            self.position = self.trackSection.length - Detector.TDE_SPACE

    def json(self):
        return {
                "type": "detector",
                "id": self.id,
                "application_directions": self.direction,
                "position": self.position
            }
