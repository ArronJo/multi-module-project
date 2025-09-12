package com.snc.zero.similarity.data

import java.io.File

/**
 * 문자열 데이터를 제공하는 인터페이스
 */
interface StringDataProvider {
    fun getData(): List<String>
    fun addData(items: List<String>)
    fun addData(item: String)
    fun clearData()
    fun size(): Int
    fun isEmpty(): Boolean
}

/**
 * 메모리 기반 문자열 데이터 제공자
 */
class InMemoryStringDataProvider(
    initialData: List<String> = emptyList()
) : StringDataProvider {

    private val _data: MutableList<String> = initialData.toMutableList()

    override fun getData(): List<String> = _data.toList()

    override fun addData(items: List<String>) {
        _data.addAll(items)
    }

    override fun addData(item: String) {
        _data.add(item)
    }

    override fun clearData() {
        _data.clear()
    }

    override fun size(): Int = _data.size

    override fun isEmpty(): Boolean = _data.isEmpty()
}

/**
 * 파일에서 데이터를 읽는 데이터 제공자
 */
class FileStringDataProvider(
    private val filePath: String,
    private val encoding: String = "UTF-8"
) : StringDataProvider {

    private var cachedData: List<String>? = null

    override fun getData(): List<String> {
        if (cachedData == null) {
            cachedData = loadDataFromFile()
        }
        return cachedData ?: emptyList()
    }

    private fun loadDataFromFile(): List<String> {
        return try {
            File(filePath).readLines(charset(encoding))
                .filter { it.isNotBlank() }
                .map { it.trim() }
        } catch (e: Exception) {
            println("파일을 읽는 중 오류가 발생했습니다: ${e.message}")
            emptyList()
        }
    }

    override fun addData(items: List<String>) {
        throw UnsupportedOperationException("FileStringDataProvider는 데이터 추가를 지원하지 않습니다")
    }

    override fun addData(item: String) {
        throw UnsupportedOperationException("FileStringDataProvider는 데이터 추가를 지원하지 않습니다")
    }

    override fun clearData() {
        cachedData = emptyList()
    }

    override fun size(): Int = getData().size

    override fun isEmpty(): Boolean = getData().isEmpty()

    /**
     * 캐시된 데이터를 강제로 새로고침
     */
    fun refreshCache() {
        cachedData = null
    }
}
