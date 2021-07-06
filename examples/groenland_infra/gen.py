import os, sys, inspect
current_dir = os.path.dirname(os.path.abspath(inspect.getfile(inspect.currentframe())))
parent_dir = os.path.dirname(current_dir)
grand_parent_dir = os.path.dirname(parent_dir)
sys.path.insert(0, grand_parent_dir)
import examples_generator.libgen as gen

# build the network

lengths = [
    1000, # 0
    1000,
    991,
    2500,
    5000,
    2500, # 5
    2500,
    2500,
    991,
    5000,
    5000, # 10
    5000,
    5000,
    5000,
    5000,
    1000, # 15
    1000,
    1000,
    1000,
    5000,
    5000, # 20
    5000,
    5000,
    5000,
    5000,
    991, # 25
    991,
    992,
    991,
    5000,
    5000, # 30
    5000,
    5000,
    5000,
    5000,
    5000, # 35
    5000,
    991,
    991,
    991,
    993, # 40
    991,
    992,
    991,
    1000,
    991, # 45
    992,
    1000,
    991,
    993,
    991, # 50
    1000,
    991,
    1000,
    5000,
    5000, # 55
    5000,
    5000,
    10000,
    1000,
    1000, # 60
    20000,
    1000,
    1000,
    20000,
    1000, # 65
    1000,
    1000,
    1000,
    1000,
    1000 # 70
]
infra = gen.Infra(lengths)

# first line
infra.add_switch(5, 0, 2)
infra.add_link(5, 6)
infra.add_switch(10, 8, 6)
infra.add_link(10, 11)
infra.add_link(11, 13)
infra.add_switch(13, 67, 16)
infra.add_switch(19, 15, 16)
infra.add_link(19, 21)
infra.add_link(21, 24)
infra.add_switch(24, 55, 25)
infra.add_switch(28, 26, 25)
infra.add_switch(28, 29, 54)
infra.add_link(29, 31)
infra.add_link(31, 34)
infra.add_link(34, 35)
infra.add_link(35, 37)
infra.add_switch(39, 37, 38)
infra.add_switch(39, 41, 42)
infra.add_switch(42, 44, 45)
infra.add_switch(45, 48, 49)
infra.add_switch(53, 49, 52)
# second line
infra.add_switch(1, 2, 3)
infra.add_switch(7, 3, 4)
infra.add_switch(7, 8, 9)
infra.add_link(9, 12)
infra.add_link(12, 14)
infra.add_switch(14, 17, 68)
infra.add_switch(20, 17, 18)
infra.add_link(20, 22)
infra.add_link(22, 23)
infra.add_switch(23, 26, 27)
infra.add_link(27, 30)
infra.add_link(30, 32)
infra.add_link(32, 33)
infra.add_link(33, 36)
infra.add_switch(36, 38, 40)
infra.add_switch(43, 40, 41)
infra.add_switch(43, 46, 47)
infra.add_switch(50, 48, 46)
infra.add_switch(50, 51, 52)
# left line
infra.add_link(55, 56)
infra.add_link(54, 57)
infra.add_switch(58, 56, 57)
infra.add_switch(58, 59, 60)
infra.add_switch(61, 59, 69)
infra.add_switch(61, 62, 63)
infra.add_switch(64, 62, 70)
infra.add_switch(64, 65, 66)

# build the trains
sim = gen.Simulation(infra)
sim.add_schedule(0, 0, 66)

# build the successions
succession = gen.Succession()

succession.add_table(5, 0, 2, [])
succession.add_table(10, 8, 6, [])
succession.add_table(13, 67, 16, [])
succession.add_table(19, 15, 16, [])
succession.add_table(24, 55, 25, [])
succession.add_table(28, 26, 25, [])
succession.add_table(28, 29, 54, [])
succession.add_table(39, 37, 38, [])
succession.add_table(39, 41, 42, [])
succession.add_table(42, 44, 45, [])
succession.add_table(45, 48, 49, [])
succession.add_table(53, 49, 52, [])
succession.add_table(1, 2, 3, [])
succession.add_table(7, 3, 4, [])
succession.add_table(7, 8, 9, [])
succession.add_table(14, 17, 68, [])
succession.add_table(20, 17, 18, [])
succession.add_table(23, 26, 27, [])
succession.add_table(36, 38, 40, [])
succession.add_table(43, 40, 41, [])
succession.add_table(43, 46, 47, [])
succession.add_table(50, 48, 46, [])
succession.add_table(50, 51, 52, [])
succession.add_table(58, 56, 57, [])
succession.add_table(58, 59, 60, [])
succession.add_table(61, 59, 69, [])
succession.add_table(61, 62, 63, [])
succession.add_table(64, 62, 70, [])
succession.add_table(64, 65, 66, [])

gen.write_json("config.json", gen.CONFIG_JSON)
gen.write_json("infra.json", infra.to_json())
gen.write_json("simulation.json", sim.to_json())
gen.write_json("succession.json", succession.to_json())
