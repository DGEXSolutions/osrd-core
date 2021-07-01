import os, sys, inspect
current_dir = os.path.dirname(os.path.abspath(inspect.getfile(inspect.currentframe())))
parent_dir = os.path.dirname(current_dir)
sys.path.insert(0, parent_dir)
import libgen.libgen as gen

# build the network
infra = gen.Infra([1000] * 7)
infra.add_switch(2, 0, 1)
infra.add_switch(2, 3, 6)
infra.add_switch(3, 4, 5)

# build the trains
sim = gen.Simulation(infra)
sim.add_schedule(0, 4, 1)
sim.add_schedule(0, 0, 5)
sim.add_schedule(0, 1, 6)

# build the successions
succession = gen.Succession()
succession.add_table(2, 0, 1, [2, 0, 1])
succession.add_table(2, 3, 6, [2, 0, 1])
succession.add_table(3, 4, 5, [0, 1])

gen.write_json("config.json", gen.CONFIG_JSON)
gen.write_json("infra.json", infra.to_json())
gen.write_json("simulation.json", sim.to_json())
gen.write_json("succession.json", succession.to_json())