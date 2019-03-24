package com.google.ar.core.examples.java.helloar

import java.io.BufferedWriter
import java.io.FileWriter

/**
 * Written by Bartosz Szczygiel <eziosoft@gmail.com>
 * Created on 24/03/2019.
 */

class FileSave() {
    var fileWriter: BufferedWriter? = null
    var isOpen = false

    fun openFile(path: String) {
        fileWriter = BufferedWriter(FileWriter(path))
        isOpen = true
    }

    fun append(s: String) {
        fileWriter?.write(s)
    }

    fun close() {
        fileWriter?.close()
        isOpen = false
    }
}