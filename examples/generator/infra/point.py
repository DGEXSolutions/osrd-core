class Point:

    def __init__(self, trackSection, side):
        self.trackSection = trackSection
        self.side = side
        self.id = f"pt.{trackSection.id}_{side}"

    def otherSide(self):
        if self.side == "BEGIN":
            return Point(self.trackSection, "END")
        else:
            return Point(self.trackSection, "BEGIN")

    def __lt__(self, point):
        return self.id < point.id

    def direction(self, point):
        if point.trackSection.id == self.trackSection.id:
            if self.side == "BEGIN":
                return "NORMAL"
            else:
                return "REVERSE"
        else:
            if self.side == "BEGIN":
                return "REVERSE"
            else:
                return "NORMAL"
