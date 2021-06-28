from printer.jsonPretiffy import JsonPretiffy

class JsonWrite:
    def writeJson(path, json):
        out = open(path, "w")
        out.write(JsonPretiffy.pretiffy(json))
        out.close()

    def writeConfig():
        JsonWrite.writeJson(
                "config.json", 
                {
                  "simulation_time_step": 1,
                  "infra_path": "infra.json",
                  "simulation_path": "simulation.json",
                  "succession_path": "succession.json",
                  "show_viewer": True,
                  "realtime_viewer": True,
                  "change_replay_check": True,
                  "simulation_step_pause": 0.2
                }
            )

    def writeInfra(data):
        JsonWrite.writeJson("infra.json", data.json())

    def writeSimulation(data):
        JsonWrite.writeJson("simulation.json", data.json())

    def writeSuccession(data):
        JsonWrite.writeJson("succession.json", data.json())

    def write(infra, simulation, succession):
        JsonWrite.writeConfig()
        JsonWrite.writeSimulation(simulation)
        JsonWrite.writeSuccession(succession)
