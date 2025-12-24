import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import ndov.cdm.trein.reisinformatie.data._4.*;
import ndov.cdm.trein.reisinformatie.messages._5.PutReisInformatieBoodschapIn;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ParseDVSTest {

    /**
     * Test unmarshalling of a DVS (Dynamische VertrekStaat) message.
     */
    @Test
    public void testUnmarshalDVSMessage() throws JAXBException {
        // Create JAXB context for the generated classes
        JAXBContext jaxbContext = JAXBContext.newInstance(PutReisInformatieBoodschapIn.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        // Load the test XML file
        InputStream xmlStream = getClass().getResourceAsStream("/test-dvs-message.xml");
        assertNotNull(xmlStream, "Test XML file should be found in resources");

        // Attempt to unmarshal the XML
        Object result = unmarshaller.unmarshal(xmlStream);

        // Verify the result
        assertNotNull(result, "Unmarshalling should produce a result");
        assertTrue(result instanceof PutReisInformatieBoodschapIn,
            "Result should be a PutReisInformatieBoodschapIn");

        PutReisInformatieBoodschapIn message = (PutReisInformatieBoodschapIn) result;
        ReisInformatieProductDVSType dvs = message.getReisInformatieProductDVS();

        // Verify basic structure
        assertNotNull(dvs, "ReisInformatieProductDVS should not be null");
        assertNotNull(dvs.getRIPAdministratie(), "RIPAdministratie should not be null");
        assertNotNull(dvs.getDynamischeVertrekStaat(), "DynamischeVertrekStaat should not be null");

        // Verify product metadata
        assertEquals("6.2", dvs.getVersie());

        // Verify RIP administration data
        assertEquals("5355006789623005", dvs.getRIPAdministratie().getReisInformatieProductID().toString());
        assertEquals(54, dvs.getRIPAdministratie().getAbonnementId().intValue());

        // Verify DVS data
        DynamischeVertrekStaatType vertrekStaat = dvs.getDynamischeVertrekStaat();
        assertEquals(6789, vertrekStaat.getRitId().intValue());
        assertEquals(LocalDate.of(2025, 12, 21), vertrekStaat.getRitDatum());

        // Verify station data
        assertNotNull(vertrekStaat.getRitStation(), "RitStation should not be null");
        assertEquals("UTLN", vertrekStaat.getRitStation().getStationCode());
        assertEquals("Utrecht Lunetten", vertrekStaat.getRitStation().getLangeNaam());
        assertEquals("8400623", vertrekStaat.getRitStation().getUICCode());

        // Verify train data
        assertNotNull(vertrekStaat.getTrein(), "Trein should not be null");
        TreinType trein = vertrekStaat.getTrein();
        assertEquals(6789, trein.getTreinNummer());
        assertEquals("SPR", trein.getTreinSoort().getCode());
        assertEquals("Sprinter", trein.getTreinSoort().getValue());
        assertEquals("NS", trein.getVervoerder());

        // Verify service indicators
        assertEquals("N", trein.getReserveren().value());
        assertEquals("N", trein.getToeslag().value());
        assertEquals("N", trein.getSpeciaalKaartje().value());
        assertEquals("N", trein.getNietInstappen().value());
        assertEquals("N", trein.getAchterBlijvenAchtersteTreinDeel().value());
        assertEquals("N", trein.getRangeerBeweging().value());

        // Verify train destination (both planned and actual)
        assertEquals(2, trein.getTreinEindBestemming().size());
        assertEquals("TL", trein.getTreinEindBestemming().getFirst().getStationCode());
        assertEquals("Tiel", trein.getTreinEindBestemming().getFirst().getLangeNaam());
        assertEquals("8400596", trein.getTreinEindBestemming().getFirst().getUICCode());

        // Verify departure times (both planned and actual)
        assertEquals(2, trein.getVertrekTijd().size());
        assertEquals(InfoStatus.GEPLAND, trein.getVertrekTijd().get(0).getInfoStatus());
        assertEquals(InfoStatus.ACTUEEL, trein.getVertrekTijd().get(1).getInfoStatus());

        // Verify delays
        assertNotNull(trein.getExacteVertrekVertraging());
        assertNotNull(trein.getGedempteVertrekVertraging());

        // Verify departure track
        assertNotNull(trein.getTreinVertrekSpoor());
        assertEquals(2, trein.getTreinVertrekSpoor().size());
        assertEquals(2, trein.getTreinVertrekSpoor().getFirst().getSpoorNummer());

        // Verify departure direction
        assertEquals("B", trein.getVertrekRichting().value());

        // Verify shortened route (verkorte route)
        assertEquals(2, trein.getVerkorteRoute().size());
        VerkorteRouteType plannedRoute = trein.getVerkorteRoute().getFirst();
        assertEquals(InfoStatus.GEPLAND, plannedRoute.getInfoStatus());
        assertFalse(plannedRoute.getStation().isEmpty(), "Verkorte route should have stations");
        assertEquals(4, plannedRoute.getStation().size());

        // Verify first station in route is Houten
        assertEquals("HTN", plannedRoute.getStation().getFirst().getStationCode());
        assertEquals("Houten", plannedRoute.getStation().getFirst().getLangeNaam());

        // Verify presentation of shortened route
        assertNotNull(trein.getPresentatieVerkorteRoute());
        assertFalse(trein.getPresentatieVerkorteRoute().getUitingen().isEmpty());

        // Verify train wing (vleugel)
        assertFalse(trein.getTreinVleugel().isEmpty(), "Trein should have at least one vleugel");
        assertEquals(1, trein.getTreinVleugel().size());
        TreinVleugelType vleugel = trein.getTreinVleugel().getFirst();

        // Verify wing departure track
        assertEquals(2, vleugel.getTreinVleugelVertrekSpoor().size());
        assertEquals(2, vleugel.getTreinVleugelVertrekSpoor().getFirst().getSpoorNummer());

        // Verify wing destination
        assertEquals(2, vleugel.getTreinVleugelEindBestemming().size());
        assertEquals("TL", vleugel.getTreinVleugelEindBestemming().getFirst().getStationCode());
        assertEquals("Tiel", vleugel.getTreinVleugelEindBestemming().getFirst().getLangeNaam());

        // Verify stop stations
        assertEquals(2, vleugel.getStopStations().size());
        StopStationsType plannedStops = vleugel.getStopStations().getFirst();
        assertEquals(InfoStatus.GEPLAND, plannedStops.getInfoStatus());
        assertEquals(6, plannedStops.getStation().size());

        // Verify specific stop station
        StationType lastStop = plannedStops.getStation().getLast();
        assertEquals("TL", lastStop.getStationCode());
        assertEquals("Tiel", lastStop.getLangeNaam());

        // Verify material (rolling stock)
        assertFalse(vleugel.getMaterieelDeelDVS().isEmpty(), "Vleugel should have material information");
        MaterieelDeelDVSType materieel = vleugel.getMaterieelDeelDVS().getFirst();
        assertEquals("SLT", materieel.getMaterieelSoort());
        assertEquals("6", materieel.getMaterieelAanduiding());
        assertEquals(10000, materieel.getMaterieelLengte().intValue());
        assertEquals(1, materieel.getMaterieelDeelVolgordeVertrek().intValue());
        assertEquals("000000-02628-0", materieel.getMaterieelNummer());

        // Verify material destination
        assertEquals(2, materieel.getMaterieelDeelEindBestemming().size());
        assertEquals("TL", materieel.getMaterieelDeelEindBestemming().getFirst().getStationCode());
        assertEquals("Tiel", materieel.getMaterieelDeelEindBestemming().getFirst().getLangeNaam());
    }
}
