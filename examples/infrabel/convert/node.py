from lxml import etree
import json

xmlInfra = etree.parse("../INT-2021-05-27-12-30-release/node.xml")
xmlRoot = xmlInfra.getroot()

for child in xmlRoot:
    if child.tag == '{http://www.infrabel.be/INT}borders':
        for c in child:
            print(c.attrib)
    elif child.tag == '{http://www.infrabel.be/INT}crossings':
        pass
    elif child.tag == '{http://www.infrabel.be/INT}curves':
        pass
    elif child.tag == '{http://www.infrabel.be/INT}deadEnds':
        pass
    elif child.tag == '{http://www.infrabel.be/INT}endOfModels':
        pass
    elif child.tag == '{http://www.infrabel.be/INT}gradients':
        pass
    elif child.tag == '{http://www.infrabel.be/INT}levelCrossingPoints':
        pass
    elif child.tag == '{http://www.infrabel.be/INT}locationPoints':
        pass
    elif child.tag == '{http://www.infrabel.be/INT}routePoints':
        pass
    elif child.tag == '{http://www.infrabel.be/INT}speedSignals':
        pass
    elif child.tag == '{http://www.infrabel.be/INT}stopSignals':
        pass
    elif child.tag == '{http://www.infrabel.be/INT}switches':
        pass

railjsonInfra = {}
print(json.dumps(railjsonInfra, indent = 4))
