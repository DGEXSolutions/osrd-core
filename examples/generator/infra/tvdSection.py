from infra.detector import Detector

class TvdSection:

    def __init__(self, pts):
        if len(pts) == 3:
            ptBase, ptLeft, ptRight = pts
            self.id = f"tvd.{ptBase.trackSection.id}-{ptLeft.trackSection.id}-{ptRight.trackSection.id}"
            self.trainDetectors = [
                    Detector.tdeId(ptBase, ptLeft),
                    Detector.tdeId(ptLeft, ptBase),
                    Detector.tdeId(ptRight, ptBase)
                ]
        elif pts[0].trackSection.id != pts[1].trackSection.id:
            ptStart, ptArrival = pts
            self.id = f"tvd.{ptStart.trackSection.id}-{ptArrival.trackSection.id}"
            self.trainDetectors = [
                    Detector.tdeId(ptStart, ptArrival),
                    Detector.tdeId(ptArrival, ptStart)
                ]
        else:
            ptBegin, ptEnd = pts
            self.id = f"tvd.{ptBegin.trackSection.id}"
            self.trainDetectors = [
                    Detector.tdeId(ptBegin, ptEnd),
                    Detector.tdeId(ptEnd, ptBegin)
                ]

        self.bufferStops = []
        self.isBerthingTrack = True

    def json(self):
        return {
                "id": self.id,
                "is_berthing_track": self.isBerthingTrack,
                "train_detectors": self.trainDetectors,
                "buffer_stops": self.bufferStops
            }
