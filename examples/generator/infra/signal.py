from infra.route import Route
from infra.detector import Detector

class Signal:
    
    SIG_SPACE = 25
    SIG_SIGHT_DISTANCE = 400

    def signalBal3Id(ptStart, ptArrival):
        return f"il.sig.bal3.{ptStart.id}-{ptArrival.id}"

    def signalSwitchId(ptBase, ptLeft, ptRight):
        l = min(ptLeft.trackSection.id, ptRight.trackSection.id)
        r = max(ptLeft.trackSection.id, ptRight.trackSection.id)
        return f"il.sig.switch.{ptBase.trackSection.id}-{l}-{r}"

    def signalPosition(ptStart, ptArrival):
        if ptStart.side == "BEGIN":
            position = Detector.TDE_SPACE
        else:
            position = ptStart.trackSection.length - Detector.TDE_SPACE
        if ptStart.direction(ptArrival) == "NORMAL":
            position -= Signal.SIG_SPACE
        else:
            position += Signal.SIG_SPACE
        return position

    def signalSwitch(switch):
        return {
                "expr": {
                    "type": "call",
                    "function": "switch_signal",
                    "arguments" : [
                            {"type": "switch", "switch": switch.id},
                            {"type": "route", "route": Route.routeId(switch.ptBase, switch.ptLeft)},
                            {"type": "route", "route": Route.routeId(switch.ptBase, switch.ptRight)}
                        ]
                    },
                "id": Signal.signalSwitchId(switch.ptBase, switch.ptLeft, switch.ptRight),
                "linked_detector": Detector.tdeId(switch.ptBase, switch.ptLeft),
                "applicable_direction": switch.ptBase.direction(switch.ptLeft),
                "position": Signal.signalPosition(switch.ptBase, switch.ptLeft),
                "sight_distance": Signal.SIG_SIGHT_DISTANCE
            }

    def signalBal3(route, nextPoints):
        if nextPoints == []:
            return {
                    "expr": {
                            "type": "call",
                            "function": "check_route",
                            "arguments": [
                                    {
                                        "type": "route",
                                        "route": route.id
                                    }
                                ]
                        },
                    "id": Signal.signalBal3Id(route.ptStart, route.ptArrival),
                    "linked_detector": Detector.tdeId(route.ptStart, route.ptArrival),
                    "position": Signal.signalPosition(route.ptStart, route.ptArrival),
                    "sight_distance": Signal.SIG_SIGHT_DISTANCE
                }
        else:
            nextSignalId = Signal.signalBal3Id(route.ptArrival, nextPoints[0])
            if len(nextPoints) == 2:
                nextSignalId = Signal.signalSwitchId(route.ptArrival, nextPoints[0], nextPoints[1])
            return {
                    "expr": {
                            "type": "call",
                            "function": "bal3_line_signal",
                            "arguments": [
                                    {
                                        "type": "signal",
                                        "signal": nextSignalId
                                    },
                                    {
                                        "type": "route",
                                        "route": route.id
                                    }
                                ]
                        },
                    "id": Signal.signalBal3Id(route.ptStart, route.ptArrival),
                    "linked_detector": Detector.tdeId(route.ptStart, route.ptArrival),
                    "position": Signal.signalPosition(route.ptStart, route.ptArrival),
                    "sight_distance": Signal.SIG_SIGHT_DISTANCE
                }
