from lxml import etree
import json

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

railjsonInfra = {}

print(json.dumps(railjsonInfra, indent = 4))
