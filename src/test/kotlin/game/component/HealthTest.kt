package game.component

import assertk.assertThat
import assertk.assertions.isSameAs
import game.component.HealthState.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


class HealthTest {

    @Nested
    inner class GetWorse {

        @Test
        fun `Health state gets worse`() {
            assertThat(HEALTHY.getWorse()).isSameAs(REELING)
            assertThat(REELING.getWorse()).isSameAs(NEAR_COLLAPSE)
            assertThat(NEAR_COLLAPSE.getWorse()).isSameAs(NEAR_DEATH)
            assertThat(NEAR_DEATH.getWorse()).isSameAs(DEAD)
        }

        @Test
        fun `Dead stays dead`() {
            assertThat(DEAD.getWorse()).isSameAs(DEAD)
        }

        @Test
        fun `Health state gets worse twice`() {
            assertThat(HEALTHY.getWorse(2)).isSameAs(NEAR_COLLAPSE)
            assertThat(REELING.getWorse(2)).isSameAs(NEAR_DEATH)
            assertThat(NEAR_COLLAPSE.getWorse(2)).isSameAs(DEAD)
            assertThat(NEAR_DEATH.getWorse(2)).isSameAs(DEAD)
        }
    }
}