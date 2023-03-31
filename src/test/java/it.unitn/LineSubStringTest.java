package it.unitn;

import it.unitn.control.DetectorTextBase;
import org.junit.jupiter.api.Test;

import static junit.framework.Assert.assertEquals;

public class LineSubStringTest {

    @Test
    public void testLineSubString() {
        assertEquals("regular", "bcd",
                DetectorTextBase.getLineSubString(1, 2, 1, 10, 1, "abcd"));

        assertEquals("line number to is null", "bcd",
                DetectorTextBase.getLineSubString(1, null, 1, 10, 1, "abcd"));

        assertEquals("line number and index of 'to' is null", "bcd",
                DetectorTextBase.getLineSubString(1, null, 1, null, 1, "abcd"));

        assertEquals("from and to in the line", "bc",
                DetectorTextBase.getLineSubString(1, 1, 1, 2, 1, "abcd"));

        assertEquals("from equals to", "b",
                DetectorTextBase.getLineSubString(1, 1, 1, 1, 1, "abcd"));

        assertEquals("from and to is not equals to the line", "abcd",
                DetectorTextBase.getLineSubString(1, 1, 1, 1, 2, "abcd"));

        assertEquals("all values are null", "abcd",
                DetectorTextBase.getLineSubString(null, null, null, null, 1, "abcd"));


    }
}
