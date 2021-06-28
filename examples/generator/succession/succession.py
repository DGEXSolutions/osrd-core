from infra.infra import Infra

class Succession:

    def __init__(self, infra):
        self.data = {
            "successions":
                [
                    {
                        "switch": switch.id,
                        "table": infra.successionTables[switch.id]
                    } for switch in infra.switches
                ]
            }

    def json(self):
        return self.data

