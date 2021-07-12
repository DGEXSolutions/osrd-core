from lxml import etree
import json

# ==============================================================================
# init railjson

railjsonInfra = {}

railjsonInfra['aspects'] = []
railjsonInfra['operational_points'] = []
railjsonInfra['script_functions'] = []
railjsonInfra['speed_sections'] = []

railjsonInfra['routes'] = [{
        'id': 'identifier',
        'entry_point': 'detector',
        'switches_position': {'switch': 'LEFT/RIGHT'},
        'release_groups': [['tvd']]
    }]

railjsonInfra['switches'] = [{
        'id': 'identifier',
        'base': {'endpoint': 'BEGIN/END', 'section': 'section'},
        'left': {'endpoint': 'BEGIN/END', 'section': 'section'},
        'right': {'endpoint': 'BEGIN/END', 'section': 'section'},
        'position_change_delay': 0
    }]

railjsonInfra['track_section_links'] = [{
        'begin': {'endpoint': 'BEGIN/END', 'section': 'section'},
        'end': {'endpoint': 'BEGIN/END', 'section': 'section'},
        'navigability': 'BOTH'
    }]

railjsonInfra['track_sections'] = [{
        'id': 'identifier',
        'length': 0,
        'operational_points': [],
        'route_waypoints': [],
        'signals': [],
        'speed_sections': [],
        'endpoints_coords': [[], []]
    }]

railjsonInfra['tvd_sections'] = [{
        'id': 'identifier',
        'is_berthing_track': True,
        'buffer_stops': [],
        'train_detectors': []
    }]

# ==============================================================================
# util functions

def valid(l, r):
    y, m, d = 2021, 7, 14
    yl, ml, dl = map(int, l.split())
    yr, mr, dr = map(int, r.split())
    return (yl, ml, dl) <= (y, m, d) <= (yr, mr, dr)

# ==============================================================================


xmlInfra = etree.parse("../INT-2021-05-27-12-30-release/nodeByTrackEdge.xml")
xmlRoot = xmlInfra.getroot()

for child in xmlRoot:
    nodeId = child.attrib['nodeId']
    distance = child.attrib['distance']
    inPort = child.attrib['inPort']
    outPOrt = child.attrib['outPort']
    sequenceNo = child.attrib['sequenceNo']
    trackEdgeId = child.attrib['trackEdgeId']
    validFromDate = child.attrib['validFromDate']
    validToDate = child.attrib['validToDate']


print(json.dumps(railjsonInfra, indent = 4))
