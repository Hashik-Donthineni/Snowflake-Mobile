package org.torproject.snowflake;

import org.torproject.snowflake.exceptions.EmptySIDException;

import java.util.Random;

/**
 * A helper class to handle SID operations.
 * There should only be one SID for whole connection cycle hence singleton.
 */
public class SIDHelper {
    private static SIDHelper sidHelper;
    private String sid;

    private SIDHelper() {
    }

    public static SIDHelper getInstance() {
        if (sidHelper == null) {
            sidHelper = new SIDHelper();
        }
        return sidHelper;
    }

    /**
     * generates a new sid.
     *
     * @return generated sid.
     */
    public String generateSid() {
        sid = Integer.toString(new Random().nextInt(10000));
        return sid;
    }

    /**
     * Returns the generated sid.
     *
     * @return Existing sid that is generated in "generateSid" method.
     * @throws EmptySIDException sid is not generated.
     */
    public String getSid() throws EmptySIDException {
        if (sid == null) {
            throw new EmptySIDException();
        }
        return sid;
    }
}
