package org.satochip.seedkeeper.data

import org.satochip.client.seedkeeper.SeedkeeperExportRights
import org.satochip.client.seedkeeper.SeedkeeperSecretType

data class GeneratePasswordData(
    var size: Int? = null,
    var type: SeedkeeperSecretType,
    var password: String,
    var label: String,
    var login: String? = null,
    var url: String? = null,
    var mnemonic: String? = null,
    var exportRights: SeedkeeperExportRights = SeedkeeperExportRights.EXPORT_PLAINTEXT_ALLOWED,
    val descriptor: String? = null
)
