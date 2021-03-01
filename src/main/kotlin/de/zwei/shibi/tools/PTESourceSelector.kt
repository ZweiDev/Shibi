package de.zwei.shibi.tools

import de.schildbach.pte.*

object PTESourceSelector {
    fun getPTESource(source: String): NetworkProvider? {
        when (source) {
            "DB" -> return DbProvider(
                "{\"type\":\"AID\",\"aid\":\"n91dB8Z77MLdoR0K\"}",
                "bdI8UVj40K5fvxwf".toByteArray(Charsets.UTF_8)
            )
            "VBB" -> return VbbProvider(
                "{\"type\":\"AID\",\"aid\":\"hafas-vbb-apps\"}",
                "RCTJM2fFxFfxxQfI".toByteArray(Charsets.UTF_8)
            )
            "INVG" -> return InvgProvider(
                "{\"type\":\"AID\",\"aid\":\"GITvwi3BGOmTQ2a5\"}",
                "ERxotxpwFT7uYRsI".toByteArray(Charsets.UTF_8)
            )
            "BVG" -> return BvgProvider("{\"aid\":\"1Rxs112shyHLatUX4fofnmdxK\",\"type\":\"AID\"}")
            "NASA" -> return NasaProvider("{\"aid\":\"nasa-apps\",\"type\":\"AID\"}")
            "RMV" -> return NvvProvider("{\"type\":\"AID\",\"aid\":\"Kt8eNOH7qjVeSxNA\"}")
            "VGS" -> return VgsProvider("{\"type\":\"AID\",\"aid\":\"51XfsVqgbdA6oXzHrx75jhlocRg6Xe\"}", "HJtlubisvxiJxss".toByteArray(Charsets.UTF_8))
            "VMT" -> return VmtProvider("{\"aid\":\"vj5d7i3g9m5d7e3\",\"type\":\"AID\"}")
            "OEBB" -> return OebbProvider("{\"type\":\"AID\",\"aid\":\"OWDL4fE4ixNiPBBm\"}")
            "ZVV" -> return ZvvProvider("{\"type\":\"AID\",\"aid\":\"hf7mcf9bv3nv8g5f\"}")
            "SNCB" -> return SncbProvider("{\"type\":\"AID\",\"aid\":\"sncb-mobi\"}")
            "CFL" -> return LuProvider("{\"type\":\"AID\",\"aid\":\"Aqf9kNqJLjxFx6vv\"}")
            "DSB" -> return DsbProvider("{\"type\":\"AID\",\"aid\":\"irkmpm9mdznstenr-android\"}")
            "SE" -> return SeProvider("{\"type\":\"AID\",\"aid\":\"h5o3n7f4t2m8l9x1\"}")
            "AVV" -> return AvvAachenProvider("{\"type\":\"AID\",\"aid\":\"4vV1AcH3N511icH\"}")
            "OOEVV" -> return OoevvProvider("{\"type\": \"AID\", \"aid\": \"wf7mcf9bv3nv8g5f\"}")
        }

        return null
    }
}
