package com.merpay.readerk

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.io.File

class DataStoreTest {

    interface DataStore {

        fun get(key: String): String

        fun set(key: String, value: String)
    }

    class MemoryDataStore : DataStore {

        private val memory = hashMapOf<String, String>()

        override fun get(key: String): String = memory[key] ?: ""

        override fun set(key: String, value: String) {
            memory[key] = value
        }
    }

    class DiskDataStore(path: String) : DataStore {

        val assetsDir = File(System.getProperty("user.dir"), path)

        override fun get(key: String): String = assetsDir.resolve(key).readText()

        override fun set(key: String, value: String) = assetsDir.resolve(key).writeText(value)
    }

    object DataStoreReader {

        fun get(key: String): Reader<DataStore, String> = Reader { store -> store.get(key) }

        fun set(key: String, value: String): Reader<DataStore, Unit> = Reader { store -> store.set(key, value) }
    }

    val memoryDataStore = MemoryDataStore()

    val diskDataStore = DiskDataStore("src/test/assets")

    init {
        diskDataStore.assetsDir.list { file, s -> file.delete() }
    }

    @Test
    fun getFromMemory() {
        val setReader = DataStoreReader.set("foo1", "hello world, reader")
        val getFoo1Reader = DataStoreReader.get("foo1")

        //this reads(set value) into the memory data store
        setReader.read(memoryDataStore)

        //read what we have set earlier
        assertThat(getFoo1Reader.read(memoryDataStore), equalTo("hello world, reader"))
        assertThat(DataStoreReader.get("xxx").read(memoryDataStore), equalTo(""))
    }

    @Test
    fun getFromDisk() {
        val setReader = DataStoreReader.set("foo1", "ReaderK is awesome")
        val getFoo1Reader = DataStoreReader.get("foo1")

        //this reads(set value) into the disk data store
        setReader.read(diskDataStore)

        assertThat(getFoo1Reader.read(diskDataStore), equalTo("ReaderK is awesome"))
    }

    @Test
    fun sharedReader() {
        val setBarReader = DataStoreReader.set("bar1", "arstgkneio'")
        val getBarReader = DataStoreReader.get("bar1")

        val staticDataStore = object : DataStore {

            override fun set(key: String, value: String) {
                //do nothing because this data store just return
            }

            override fun get(key: String): String = "STATIC TEXT"
        }

        //use with local static data store
        setBarReader.read(staticDataStore) //actually this doesn't matter, but yeah
        assertThat(getBarReader.read(staticDataStore), equalTo("STATIC TEXT"))

        //use with disk data store
        val value = setBarReader.flatMap { getBarReader }.map { it + it }.read(diskDataStore)
        assertThat(value, equalTo("arstgkneio'arstgkneio'"))

        //use with memory data store
        val anotherValue = setBarReader.flatMap { getBarReader }.map { "simple value" }.read(memoryDataStore)
        assertThat(anotherValue, equalTo("simple value"))
    }
}
