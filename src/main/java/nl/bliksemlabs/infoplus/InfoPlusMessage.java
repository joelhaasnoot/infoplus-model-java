package nl.bliksemlabs.infoplus;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import ndov.cdm.trein.reisinformatie.data._4.ReisInformatieProductDASType;
import ndov.cdm.trein.reisinformatie.data._4.ReisInformatieProductDVSType;
import ns.cdm.reisinformatie.data.rit._5.ReisInformatieProductRitInfoType;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for marshalling and unmarshalling InfoPlus messages.
 *
 * Supports three message types:
 * - RIT (RitInfo): Journey/trip information messages
 * - DVS (Dynamische Vertrek Staat): Dynamic departure board messages
 * - DAS (Dynamische Aankomst Staat): Dynamic arrival board messages
 *
 */
public class InfoPlusMessage {

    private static volatile JAXBContext ritContext;
    private static volatile JAXBContext dvsContext;
    private static volatile JAXBContext dasContext;

    private static JAXBContext getRitContext() throws JAXBException {
        if (ritContext == null) {
            synchronized (InfoPlusMessage.class) {
                if (ritContext == null) {
                    ritContext = JAXBContext.newInstance(
                        ns.cdm.reisinformatie.message.ritinfo._5.PutReisInformatieBoodschapIn.class
                    );
                }
            }
        }
        return ritContext;
    }

    private static JAXBContext getDvsContext() throws JAXBException {
        if (dvsContext == null) {
            synchronized (InfoPlusMessage.class) {
                if (dvsContext == null) {
                    dvsContext = JAXBContext.newInstance(
                        ndov.cdm.trein.reisinformatie.messages._5.PutReisInformatieBoodschapIn.class
                    );
                }
            }
        }
        return dvsContext;
    }

    private static JAXBContext getDasContext() throws JAXBException {
        if (dasContext == null) {
            synchronized (InfoPlusMessage.class) {
                if (dasContext == null) {
                    dasContext = JAXBContext.newInstance(
                        ndov.cdm.trein.reisinformatie.messages.dynamischeaankomststaat._1.PutReisInformatieBoodschapIn.class
                    );
                }
            }
        }
        return dasContext;
    }

    /**
     * Parse a RIT message from an XML string.
     * @param xml XML string containing the RIT message
     * @return Parsed RIT message
     * @throws InfoPlusParseException if parsing fails
     */
    public static ReisInformatieProductRitInfoType parseRIT(String xml)
            throws InfoPlusParseException {
        return parseRIT(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Parse a RIT message from an InputStream.
     * @param inputStream InputStream containing the RIT message
     * @return Parsed RIT message
     * @throws InfoPlusParseException if parsing fails
     */
    public static ReisInformatieProductRitInfoType parseRIT(InputStream inputStream)
            throws InfoPlusParseException {
        try {
            Unmarshaller unmarshaller = getRitContext().createUnmarshaller();
            Object result = unmarshaller.unmarshal(inputStream);
            return ((ns.cdm.reisinformatie.message.ritinfo._5.PutReisInformatieBoodschapIn) result).getReisInformatieProductRitInfo();
        } catch (JAXBException e) {
            throw new InfoPlusParseException("Failed to parse RIT message", e);
        }
    }

    /**
     * Parse a DVS message from an XML string.
     * @param xml XML string containing the DVS message
     * @return Parsed DVS message
     * @throws InfoPlusParseException if parsing fails
     */
    public static ReisInformatieProductDVSType parseDVS(String xml)
            throws InfoPlusParseException {
        return parseDVS(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Parse a DVS message from an InputStream.
     * @param inputStream InputStream containing the DVS message
     * @return Parsed DVS message
     * @throws InfoPlusParseException if parsing fails
     */
    public static ReisInformatieProductDVSType parseDVS(InputStream inputStream)
            throws InfoPlusParseException {
        try {
            Unmarshaller unmarshaller = getDvsContext().createUnmarshaller();
            Object result = unmarshaller.unmarshal(inputStream);
            return ((ndov.cdm.trein.reisinformatie.messages._5.PutReisInformatieBoodschapIn) result).getReisInformatieProductDVS();
        } catch (JAXBException e) {
            throw new InfoPlusParseException("Failed to parse DVS message", e);
        }
    }

    /**
     * Parse a DAS message from an XML string.
     * @param xml XML string containing the DAS message
     * @return Parsed DAS message
     * @throws InfoPlusParseException if parsing fails
     */
    public static ReisInformatieProductDASType parseDAS(String xml)
            throws InfoPlusParseException {
        return parseDAS(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Parse a DAS message from an InputStream.
     * @param inputStream InputStream containing the DAS message
     * @return Parsed DAS message
     * @throws InfoPlusParseException if parsing fails
     */
    public static ReisInformatieProductDASType parseDAS(InputStream inputStream)
            throws InfoPlusParseException {
        try {
            Unmarshaller unmarshaller = getDasContext().createUnmarshaller();
            Object result = unmarshaller.unmarshal(inputStream);
            return ((ndov.cdm.trein.reisinformatie.messages.dynamischeaankomststaat._1.PutReisInformatieBoodschapIn) result).getReisInformatieProductDAS();
        } catch (JAXBException e) {
            throw new InfoPlusParseException("Failed to parse DAS message", e);
        }
    }

    /**
     * Exception thrown when InfoPlus message processing fails.
     */
    public static class InfoPlusParseException extends Exception {

        public InfoPlusParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
