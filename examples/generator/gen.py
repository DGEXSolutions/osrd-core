from infra.infra import Infra
from simulation.simulation import Simulation
from succession.succession import Succession
from printer.jsonWrite import JsonWrite

infra = Infra()

infra.addTrackSection("0", 1000, "BOTH")
infra.addTrackSection("1", 1000, "BOTH")
infra.addTrackSection("2", 1000, "BOTH")
infra.addTrackSection("3", 1000, "BOTH")

infra.addTrackSectionLink("0", "1")
infra.addSwitch("1", "2", "3", [])
infra.addSignals()

simulation = Simulation(infra)
simulation.addSchedule("train.0", 0, "0", "3")

succession = Succession(infra)

JsonWrite.writeConfig()
JsonWrite.writeInfra(infra)
JsonWrite.writeSimulation(simulation)
JsonWrite.writeSuccession(succession)
