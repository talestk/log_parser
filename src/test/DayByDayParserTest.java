package test;

import com.company.Main;
import org.junit.Test;

import java.io.IOException;

public class DayByDayParserTest {
	@Test
	public void testOverallParser() throws IOException {
		Main.main(new String[]{"-o", "logfile.log"});
	}
}
