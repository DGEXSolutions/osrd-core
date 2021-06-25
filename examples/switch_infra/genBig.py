import os, sys, inspect
current_dir = os.path.dirname(os.path.abspath(inspect.getfile(inspect.currentframe())))
parent_dir = os.path.dirname(current_dir)
sys.path.insert(0, parent_dir)
import generator_infra.libgen as gen

# build the network
infra = gen.Infra([400] * 18, 100, 10)

infra.add_link(0, 1)
infra.add_link(1, 2)
infra.add_link(2, 3)
infra.add_link(3, 4)
infra.add_link(4, 5)

infra.add_link(6, 7)
infra.add_link(7, 8)
infra.add_link(8, 9)
infra.add_link(9, 10)
infra.add_link(10, 11)

infra.add_link(12, 13)
infra.add_link(13, 14)
infra.add_link(14, 15)
infra.add_link(15, 16)
infra.add_link(16, 17)

infra.add_switch(5, 6, 12)

# build the trains
sim = gen.Simulation(infra)
sim.add_schedule(0, 0, 11)
#sim.add_schedule(0, 17, 0)

# build the successions
succession = gen.Succession()
#succession.add_table(5, 6, 12, [0, 1])
succession.add_table(5, 6, 12, [])

gen.write_json("config.json", gen.CONFIG_JSON)
gen.write_json("infra.json", infra.to_json())
gen.write_json("simulation.json", sim.to_json())
gen.write_json("succession.json", succession.to_json())
