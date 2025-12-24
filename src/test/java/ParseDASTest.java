import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import ndov.cdm.trein.reisinformatie.data._4.*;
import ndov.cdm.trein.reisinformatie.messages.dynamischeaankomststaat._1.PutReisInformatieBoodschapIn;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ParseDASTest {

    /**
     * Test unmarshalling of a DAS (Dynamische Aankomst Staat) message.
     *
     * This test verifies that we can successfully parse a DAS message containing
     * dynamic arrival board information including train details, arrival times, tracks, and origin information.
     */
    @Test
    public void testUnmarshalDASMessage() throws JAXBException {
        // Create JAXB context for the generated classes
        JAXBContext jaxbContext = JAXBContext.newInstance(PutReisInformatieBoodschapIn.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        // Load the test XML file
        InputStream xmlStream = getClass().getResourceAsStream("/test-das-message.xml");
        assertNotNull(xmlStream, "Test XML file should be found in resources");

        // Attempt to unmarshal the XML
        Object result = unmarshaller.unmarshal(xmlStream);

        // Verify the result
        assertNotNull(result, "Unmarshalling should produce a result");
        assertTrue(result instanceof PutReisInformatieBoodschapIn,
            "Result should be a PutReisInformatieBoodschapIn");

        PutReisInformatieBoodschapIn message = (PutReisInformatieBoodschapIn) result;
        ReisInformatieProductDASType das = message.getReisInformatieProductDAS();

        // Verify basic structure
        assertNotNull(das, "ReisInformatieProductDAS should not be null");
        assertNotNull(das.getRIPAdministratie(), "RIPAdministratie should not be null");
        assertNotNull(das.getDynamischeAankomstStaat(), "DynamischeAankomstStaat should not be null");

        // Verify product metadata
        assertEquals("6.1", das.getVersie());

        // Verify RIP administration data
        assertEquals("8357002889258013", das.getRIPAdministratie().getReisInformatieProductID().toString());
        assertEquals(55, das.getRIPAdministratie().getAbonnementId().intValue());

        // Verify DAS data
        DynamischeAankomstStaatType aankomstStaat = das.getDynamischeAankomstStaat();
        assertEquals(2889, aankomstStaat.getRitId().intValue());
        assertEquals(LocalDate.of(2025, 12, 23), aankomstStaat.getRitDatum());

        // Verify station data
        assertNotNull(aankomstStaat.getRitStation(), "RitStation should not be null");
        assertEquals("GD", aankomstStaat.getRitStation().getStationCode());
        assertEquals("Gouda", aankomstStaat.getRitStation().getLangeNaam());
        assertEquals("8400258", aankomstStaat.getRitStation().getUICCode());

        // Verify train arrival data
        assertNotNull(aankomstStaat.getTreinAankomst(), "TreinAankomst should not be null");
        TreinAankomstType treinAankomst = aankomstStaat.getTreinAankomst();
        assertEquals(2889, treinAankomst.getTreinNummer());
        assertEquals("IC", treinAankomst.getTreinSoort().getCode());
        assertEquals("Intercity", treinAankomst.getTreinSoort().getValue());
        assertEquals("NS", treinAankomst.getVervoerder());

        // Verify train origin (both planned and actual)
        assertEquals(2, treinAankomst.getTreinHerkomst().size());
        assertEquals("RTD", treinAankomst.getTreinHerkomst().getFirst().getStationCode());
        assertEquals("Rotterdam Centraal", treinAankomst.getTreinHerkomst().getFirst().getLangeNaam());
        assertEquals("8400530", treinAankomst.getTreinHerkomst().getFirst().getUICCode());
        assertEquals(InfoStatus.GEPLAND, treinAankomst.getTreinHerkomst().getFirst().getInfoStatus());
        assertEquals(InfoStatus.ACTUEEL, treinAankomst.getTreinHerkomst().get(1).getInfoStatus());

        // Verify presentation of train origin
        assertNotNull(treinAankomst.getPresentatieTreinHerkomst());
        assertFalse(treinAankomst.getPresentatieTreinHerkomst().getUitingen().isEmpty());
        assertEquals(2, treinAankomst.getPresentatieTreinHerkomst().getUitingen().size());
        assertEquals("Rotterdam C.", treinAankomst.getPresentatieTreinHerkomst().getUitingen().getFirst().getUiting().getFirst().getValue());

        // Verify arrival times (both planned and actual)
        assertEquals(2, treinAankomst.getAankomstTijd().size());
        assertEquals(InfoStatus.GEPLAND, treinAankomst.getAankomstTijd().get(0).getInfoStatus());
        assertEquals(InfoStatus.ACTUEEL, treinAankomst.getAankomstTijd().get(1).getInfoStatus());

        // Verify delays
        assertNotNull(treinAankomst.getExacteAankomstVertraging());
        assertEquals("PT1M4S", treinAankomst.getExacteAankomstVertraging().toString());
        assertNotNull(treinAankomst.getGedempteAankomstVertraging());
        assertEquals("PT0S", treinAankomst.getGedempteAankomstVertraging().toString());

        // Verify presentation of delay
        assertNotNull(treinAankomst.getPresentatieAankomstVertraging());
        assertFalse(treinAankomst.getPresentatieAankomstVertraging().getUitingen().isEmpty());
        assertEquals("+1 min.", treinAankomst.getPresentatieAankomstVertraging().getUitingen().getFirst().getUiting().getFirst().getValue());

        // Verify arrival track
        assertNotNull(treinAankomst.getTreinAankomstSpoor());
        assertEquals(2, treinAankomst.getTreinAankomstSpoor().size());
        assertEquals(3, treinAankomst.getTreinAankomstSpoor().getFirst().getSpoorNummer());
        assertEquals(InfoStatus.GEPLAND, treinAankomst.getTreinAankomstSpoor().getFirst().getInfoStatus());
        assertEquals(InfoStatus.ACTUEEL, treinAankomst.getTreinAankomstSpoor().get(1).getInfoStatus());

        // Verify presentation of arrival track
        assertNotNull(treinAankomst.getPresentatieTreinAankomstSpoor());
        assertFalse(treinAankomst.getPresentatieTreinAankomstSpoor().getUitingen().isEmpty());
        assertEquals("3", treinAankomst.getPresentatieTreinAankomstSpoor().getUitingen().getFirst().getUiting().getFirst().getValue());

        // Verify shortened route from origin (verkorte route herkomst)
        assertEquals(2, treinAankomst.getVerkorteRouteHerkomst().size());
        VerkorteRouteHerkomstType plannedRouteOrigin = treinAankomst.getVerkorteRouteHerkomst().getFirst();
        assertEquals(InfoStatus.GEPLAND, plannedRouteOrigin.getInfoStatus());
        assertFalse(plannedRouteOrigin.getStation().isEmpty(), "Verkorte route herkomst should have stations");
        assertEquals(1, plannedRouteOrigin.getStation().size());

        // Verify station in route from origin is Rotterdam Alexander
        assertEquals("RTA", plannedRouteOrigin.getStation().getFirst().getStationCode());
        assertEquals("Rotterdam Alexander", plannedRouteOrigin.getStation().getFirst().getLangeNaam());

        // Verify presentation of shortened route from origin
        assertNotNull(treinAankomst.getPresentatieVerkorteRouteHerkomst());
        assertFalse(treinAankomst.getPresentatieVerkorteRouteHerkomst().getUitingen().isEmpty());
        assertEquals("Alexander", treinAankomst.getPresentatieVerkorteRouteHerkomst().getUitingen().getFirst().getUiting().getFirst().getValue());

        // Verify changes to origin (wijziging herkomst)
        assertFalse(treinAankomst.getWijzigingHerkomst().isEmpty(), "Should have wijziging herkomst");
        WijzigingHerkomstType wijziging = treinAankomst.getWijzigingHerkomst().getFirst();
        assertEquals("11", wijziging.getWijzigingType());
    }
}