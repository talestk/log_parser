package test;

import com.company.Main;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

public class DayByDayParserTest {
	@Test
	public void testOverallParser() throws IOException, ParseException {
		Main.main(new String[]{"-a", "logfile.log"});
	}

	@Test
	public void testUserSpecificParser_doubleCheckout() throws IOException, ParseException {
		Main.main(new String[]{"-u", "logfile_two_co_test.log"});
	}

	@Test
	public void testUserSpecificParser_dayChange() throws IOException, ParseException {
		Main.main(new String[]{"-u", "logfile_daychange_test.log"});
	}

	@Test
	public void testUserSpecificParser() throws IOException, ParseException {
		Main.main(new String[]{"-u", "logfile.log"});
	}

	// TODO make tests for averages and total
}