class Switch:
    def __init__(self, ptBase, ptLeft, ptRight):
        self.ptBase = ptBase
        self.ptLeft = ptLeft
        self.ptRight = ptRight
        self.id = f"il.switch.{ptBase.trackSection.id}-{ptLeft.trackSection.id}-{ptRight.trackSection.id}"

    def json(self):
        return {
                "base": {"endpoint": self.ptBase.side, "section": self.ptBase.trackSection.id},
                "left": {"endpoint": self.ptLeft.side, "section": self.ptLeft.trackSection.id},
                "right": {"endpoint": self.ptRight.side, "section": self.ptRight.trackSection.id},
                "id": self.id,
                "position_change_delay": 6
            }
