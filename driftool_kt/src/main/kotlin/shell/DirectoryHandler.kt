/**
 * Copyright 2024 Karl Kegel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.driftool.shell

import io.driftool.Log
import java.util.UUID
import java.util.concurrent.ConcurrentLinkedQueue

class DirectoryHandler(val rootLocation: String) {

    private val temporalDirectories: ConcurrentLinkedQueue<String> = ConcurrentLinkedQueue()

    /**
     * Creates a temporal directory in the root location.
     * The directory is created with a unique name.
     * @return the path to the created directory as absolute path
     */
    fun createTemporalDirectory(): String {
        val trunk = ensureNoDirectoryPathEnding(rootLocation)
        val dirname = "$trunk/${getUniqueName()}"
        val result = Shell.mkdir(dirname, null)
        if (!result.isSuccessful()){
            Log.append("Could not create temporal directory.")
            throw RuntimeException("Could not create temporal directory.")
        }
        return dirname
    }

    fun deleteAllTemporalDirectories() {
        temporalDirectories.forEach { deleteDirectory(it) }
    }

    fun createTemporalFile(): String {
        val trunk = ensureNoDirectoryPathEnding(rootLocation)
        val filename = "$trunk/${getUniqueName()}.txt"
        val result = Shell.exec(arrayOf("touch", filename), null)
        if (!result.isSuccessful()){
            Log.append("Could not create temporal file.")
            throw RuntimeException("Could not create temporal file.")
        }
        return filename
    }

    fun deleteTemporaryFile(file: String) {
        val result = Shell.exec(arrayOf("rm", "-f", file), null)
        if (!result.isSuccessful()){
            Log.append("Could not delete file $file.")
            throw RuntimeException("Could not delete file $file.")
        }
    }

    fun deleteDirectory(directory: String) {
        //TODO
        unregisterTemporalDirectory(directory)
    }

    fun registerTemporalDirectory(directory: String) {
        temporalDirectories.add(directory)
    }

    fun unregisterTemporalDirectory(directory: String) {
        temporalDirectories.remove(directory)
    }


    companion object {

        fun getUniqueName(): String {
            return UUID.randomUUID().toString()
        }

        fun ensureDirectoryPathEnding(path: String): String {
            return if (path.endsWith("/")) path else "$path/"
        }

        fun ensureNoDirectoryPathEnding(path: String): String {
            return if (path.endsWith("/")) path.substring(0, path.length - 1) else path
        }

        fun ensureDotEnding(path: String): String {
            return if (path.endsWith(".")) path else "$path."
        }

        fun ensureSlashBeginning(path: String): String {
            return if (path.startsWith("/")) path else "/$path"
        }

        fun ensureNoSlashBeginning(path: String): String {
            return if (path.startsWith("/")) path.substring(1) else path
        }

        fun refactorPathUnixStyle(path: String): String {
            return path.replace("\\", "/")
        }

    }

}