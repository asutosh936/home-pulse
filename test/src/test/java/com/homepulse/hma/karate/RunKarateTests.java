package com.homepulse.hma.karate;

import com.intuit.karate.junit5.Karate;

public class RunKarateTests {

    @Karate.Test
    Karate testAll() {
        // run all features placed under src/test/resources/features
        return Karate.run("classpath:features");
    }

}
