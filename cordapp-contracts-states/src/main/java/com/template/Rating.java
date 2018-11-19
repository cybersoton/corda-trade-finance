package com.template;

import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
public enum Rating {
    AAA, AA, A, BBB, BB, B, CCC, CC, C, D, NULL;

    static boolean greaterThanCCC(Rating r1){
        if (r1.equals(Rating.CC))
            return false;

        if (r1.equals(Rating.C))
            return false;

        if (r1.equals(Rating.D))
            return false;

        return true;
    }

}
