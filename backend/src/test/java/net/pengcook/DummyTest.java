package net.pengcook;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class DummyTest {

    @Test
    public void test() {
        assertThat(3).isEqualTo(5);
    }

    @Test
    public void test2() {
        assertThat(3).isEqualTo(7);
    }


}
