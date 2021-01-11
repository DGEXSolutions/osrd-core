package fr.sncf.osrd.infra.parsing.railmlfancy;

import fr.sncf.osrd.App;
import https.www_railml_org.schemas._3.RailML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;


public class FancyRailMLParser {
    static final Logger logger = LoggerFactory.getLogger(FancyRailMLParser.class);

    @SuppressWarnings("unchecked")
    public static RailML parse(String path) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(RailML.class);
            var unmarshaller = jaxbContext.createUnmarshaller();

            var file = new BufferedInputStream(new FileInputStream(path));
            return ((JAXBElement<RailML>)unmarshaller.unmarshal(file)).getValue();
        } catch (IOException e) {
            logger.error("IO exception", e);
            return null;
        } catch (JAXBException e) {
            logger.error("JAXB exception", e);
            return null;
        }
    }
}
