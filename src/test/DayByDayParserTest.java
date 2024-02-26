package test;

import com.company.Main;
import org.junit.Test;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.text.ParseException;

public class DayByDayParserTest {
	@Test
	public void testOverallParser_log1() throws IOException, ParseException {
		Main.main(new String[]{"-a", "new_hsiufen.log"});
	}

	@Test
	public void testUserSpecificParser_dayChange() throws IOException, ParseException {
		Main.main(new String[]{"-u", "new_hsiufen.log"});
	}

	@Test
	public void testOverallParser_log2() throws IOException, ParseException {
		Main.main(new String[]{"-a", "longJenny.log"});
	}

	@Test
	public void testOverallParser_overlapDates() throws IOException, ParseException {
		Main.main(new String[]{"-a", "logfile_overlapDates.log"});
	}

	@Test
	public void testOverallParser_manyFiles_overall() throws IOException, ParseException {
		Main.main(new String[]{"-fa", "logfile"});
	}

	@Test
	public void testOverallParser_manyFiles_userSpecific() throws IOException, ParseException {
		Main.main(new String[]{"-fu", "hsiufen.log"});
	}

	@Test
	public void testUserSpecificParser_doubleCheckout() throws IOException, ParseException {
		Main.main(new String[]{"-u", "logfile_two_co_test.log"});
	}



	@Test
	public void testUserSpecificParser() throws IOException, ParseException {
		Main.main(new String[]{"-u", "Partek log 1-19-24.txt"});
	}

	@Test
	public void testSimpleUserActions() throws IOException, ParseException {
		Main.main(new String[]{"-s", "Partek log 1-19-24.txt"});
	}

	@Test
	public void testDaily() throws IOException, ParseException {
		Main.main(new String[]{"-a", "Partek log 1-19-24.txt"});
	}

    @Test
	public void testSimpleUserActionsNCI() throws IOException, ParseException {
		Main.main(new String[]{"-s", "Partek log 1-19-24.txt"});
	}

	@Test
	public void testSimpleSpecificMonths() throws IOException, ParseException {
		Main.main(new String[]{"-d", "3", "Partek log 1-19-24.txt"});
	}

	// TODO make tests for averages and total


}
