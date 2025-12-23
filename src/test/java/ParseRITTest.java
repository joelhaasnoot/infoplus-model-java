import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import ns.cdm.reisinformatie.data.rit._5.*;
import ns.cdm.reisinformatie.message.ritinfo._5.PutReisInformatieBoodschapIn;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ParseRITTest {

    /**
     * Test unmarshalling of a RIT (RitInfo) message.
     *
     * This test verifies that we can successfully parse a RIT message containing
     * journey information including stations, times, tracks, and rolling stock details.
     */
    @Test
    public void testUnmarshalRITMessage() throws JAXBException {
        // Create JAXB context for the generated classes
        JAXBContext jaxbContext = JAXBContext.newInstance(PutReisInformatieBoodschapIn.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        // Load the test XML file
        InputStream xmlStream = getClass().getResourceAsStream("/test-rit-message.xml");
        assertNotNull(xmlStream, "Test XML file should be found in resources");

        // Attempt to unmarshal the XML
        Object result = unmarshaller.unmarshal(xmlStream);

        // Verify the result
        assertNotNull(result, "Unmarshalling should produce a result");
        assertTrue(result instanceof PutReisInformatieBoodschapIn,
            "Result should be a PutReisInformatieBoodschapIn");

        PutReisInformatieBoodschapIn message = (PutReisInformatieBoodschapIn) result;
        ReisInformatieProductRitInfoType ritInfo = message.getReisInformatieProductRitInfo();

        // Verify basic structure
        assertNotNull(ritInfo, "ReisInformatieProductRitInfo should not be null");
        assertNotNull(ritInfo.getRIPAdministratie(), "RIPAdministratie should not be null");
        assertNotNull(ritInfo.getRitInfo(), "RitInfo should not be null");

        // Verify product metadata
        assertEquals("9.2", ritInfo.getVersie());
        assertEquals("1.2.148", ritInfo.getApplicatieVersie());

        // Verify RIP administration data
        assertEquals("53590029920001", ritInfo.getRIPAdministratie().getReisInformatieProductID().toString());
        assertEquals(57, ritInfo.getRIPAdministratie().getAbonnementId().intValue());

        // Verify train data
        TreinRitType treinRit = ritInfo.getRitInfo();
        assertEquals(2992, treinRit.getTreinNummer());
        assertEquals(LocalDate.of(2025, 12, 25), treinRit.getTreinDatum());
        assertEquals("IC", treinRit.getTreinSoort().getCode());
        assertEquals("Intercity", treinRit.getTreinSoort().getValue());
        assertEquals("NS", treinRit.getVervoerder());

        // Verify service indicators
        assertEquals("N", treinRit.getReserveren().value());
        assertEquals("N", treinRit.getToeslag().value());
        assertEquals("N", treinRit.getSpeciaalKaartje().value());
        assertEquals("J", treinRit.getReisplanner().value());

        // Verify logical journey structure
        assertNotNull(treinRit.getLogischeRit(), "LogischeRit should not be null");
        assertFalse(treinRit.getLogischeRit().isEmpty(), "LogischeRit list should not be empty");
        assertEquals("2992", treinRit.getLogischeRit().get(0).getLogischeRitNummer());
        assertFalse(treinRit.getLogischeRit().get(0).getLogischeRitDeel().isEmpty(),
            "LogischeRit should have at least one LogischeRitDeel");

        // Verify the logical journey part
        LogischeRitDeelType ritDeel = treinRit.getLogischeRit().get(0).getLogischeRitDeel().get(0);
        assertEquals(2992, ritDeel.getLogischeRitDeelNummer());
        assertFalse(ritDeel.getLogischeRitDeelStation().isEmpty(),
            "LogischeRitDeel should have stations");

        // Verify first station (Maastricht - departure)
        RitStationsType firstStation = ritDeel.getLogischeRitDeelStation().get(0);
        assertEquals("MT", firstStation.getStation().getStationCode());
        assertEquals("Maastricht", firstStation.getStation().getKorteNaam());
        assertEquals("8400424", firstStation.getStation().getUICCode());
        assertEquals("N", firstStation.getStationToegankelijk().value());
        assertEquals("J", firstStation.getStationReisAssistentie().value());

        // Verify departure track
        assertNotNull(firstStation.getTreinVertrekSpoor());
        assertEquals(2, firstStation.getTreinVertrekSpoor().size());
        assertEquals(3, firstStation.getTreinVertrekSpoor().getFirst().getSpoorNummer());

        // Verify train stops
        assertEquals("J", firstStation.getStopt().getFirst().getValue().value());

        // Verify train destination
        assertEquals("EHV", firstStation.getTreinEindBestemming().getFirst().getStationCode());
        assertEquals("Eindhoven Centraal", firstStation.getTreinEindBestemming().getFirst().getLangeNaam());

        // Verify material (rolling stock)
        assertFalse(firstStation.getMaterieelDeel().isEmpty(),
            "Station should have material information");
        MaterieelDeelRITInfoType materieel =  firstStation.getMaterieelDeel().getFirst();
        assertEquals("AD47", materieel.getMaterieelDeelID());
        assertEquals("VIRM", materieel.getMaterieelDeelSoort());
        assertEquals("4", materieel.getMaterieelDeelAanduiding());
        assertEquals(BigInteger.valueOf(10900), materieel.getMaterieelDeelLengte());

        // Verify last station (Eindhoven - arrival)
        RitStationsType lastStation = ritDeel.getLogischeRitDeelStation().get(
            ritDeel.getLogischeRitDeelStation().size() - 1);
        assertEquals("EHV", lastStation.getStation().getStationCode());
        assertEquals("Eindhoven Centraal", lastStation.getStation().getLangeNaam());
        assertEquals("8400206", lastStation.getStation().getUICCode());

        // Verify arrival track at final station
        assertNotNull(lastStation.getTreinAankomstSpoor());
        assertEquals(2, lastStation.getTreinAankomstSpoor().size());
        assertEquals(5, lastStation.getTreinAankomstSpoor().get(0).getSpoorNummer());

        // Verify intermediate stop (Sittard)
        RitStationsType sittardStation = ritDeel.getLogischeRitDeelStation().stream()
            .filter(s -> "STD".equals(s.getStation().getStationCode()))
            .findFirst()
            .orElse(null);
        assertNotNull(sittardStation, "Sittard should be in the journey");
        assertEquals("Sittard", sittardStation.getStation().getLangeNaam());
        assertEquals("J", sittardStation.getStopt().getFirst().getValue().value());
        SpoorPlannedActueelType spoor = sittardStation.getTreinVertrekSpoor().getFirst();
        assertEquals("2b", spoor.getSpoorNummer() + spoor.getSpoorFase());
    }
}
