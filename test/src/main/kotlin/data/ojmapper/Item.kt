package data.ojmapper

data class Item(
        val kind: String,
        val id: String,
        val etag: String,
        val selfLink: String,
        val volumeInfo: VolumeInfo
        )