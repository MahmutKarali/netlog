package com.krl.testcase

import android.net.Uri

class DataSource {
    companion object {
        private var imageList: MutableList<Uri> = mutableListOf()
    }

    fun isEmpty() = imageList.isEmpty()

    fun size() = imageList.size

    fun push(item: Uri) = imageList.add(item)

    fun get(): MutableList<Uri> = imageList

    fun pop(): Boolean {
        if (isEmpty())
            return false

        imageList.removeAt(imageList.size - 1)
        return true
    }
}