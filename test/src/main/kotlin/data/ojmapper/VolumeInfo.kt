package data.ojmapper

import data.ojmapper.ImageLink

data class VolumeInfo(val title: String,
                       val subtitle: String,
                       val industryIdentifiers: Array<IndustryIdentifier>,
                       val imageLinks: ImageLink)