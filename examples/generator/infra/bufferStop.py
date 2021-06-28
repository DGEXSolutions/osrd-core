class BufferStop:

    def __init__(self, trackSection, direction):
        self.data = {
                "type": "buffer_stop",
                "application_directions": direction,
                "id": f"stop.{trackSection.id}_{direction}",
                "position": 0 if direction == "REVERSE" else trackSection.length
            }

    def json(self):
        return self.data
