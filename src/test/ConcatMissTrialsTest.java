package test;

import com.company.ConcatMissTrials;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

public class ConcatMissTrialsTest {
	@Test
	public void test() throws IOException, ParseException {
		ConcatMissTrials.parse("C:\\Users\\Tales\\Documents\\log_parser\\Unsuccessful Trials - DJ - 2017-12-06.tsv");
	}
}
