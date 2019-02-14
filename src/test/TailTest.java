package test;

import com.company.helpers.Tail;
import org.junit.Test;

import java.io.File;

public class TailTest {
	@Test
	public void test() {
		System.out.println(Tail.tail(new File("logfileNCI.log"), 200));
	}
}
