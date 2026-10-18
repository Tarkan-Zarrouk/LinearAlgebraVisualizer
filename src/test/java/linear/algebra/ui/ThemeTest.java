package linear.algebra.ui;

import org.junit.jupiter.api.Test;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.*;

class ThemeTest {
    @Test
    void customAccentIsRegisteredOnce() {
        String a = Theme.addAccent(new Color(0x12AB34));
        String b = Theme.addAccent(new Color(0x12AB34));
        assertEquals("#12AB34", a);
        assertEquals(a, b);
        assertEquals(new Color(0x12AB34), Theme.ACCENTS.get(a));
    }
}
