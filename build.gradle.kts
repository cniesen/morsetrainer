/*
 *  Copyright (C) 2020–2026 Claus Niesen
 *
 *  This file is part of Claus' Morse Trainer.
 *
 *  Claus' Morse Trainer is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Claus' Morse Trainer is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Claus' Morse Trainer.  If not, see <https://www.gnu.org/licenses/>.
 */

import java.time.Year

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {

    /**
     * Use `apply false` in the top-level build.gradle file to add a Gradle
     * plugin as a build dependency but not apply it to the current (root)
     * project. Don't use `apply false` in sub-projects. For more information,
     * see Applying external plugins with same version to subprojects.
     */

    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
}

tasks.register("updateCopyrightYears") {
    group = "maintenance"
    description = "Automatically updates copyright years in source files, preserving original first year"

    val currentYear = Year.now().value

    // Directories or files to scan
    val scanPaths = listOf(
        "app/src",
        "app/build.gradle.kts",
        "build.gradle.kts",
        "settings.gradle.kts"
    )

    // Matches:
    // Copyright (C) 2021–2026 Claus Niesen
    // or already: Copyright (C) 2021–2026 Claus Niesen
    val regex = Regex("""Copyright\s*\(C\)\s*(\d{4})(?:–\d{4})?\s+Claus Niesen""")

    doLast {
        val filesToScan = scanPaths
            .map { file(it) }
            .filter { it.exists() }
            .flatMap { f ->
                if (f.isDirectory) {
                    f.walkTopDown()
                        .filter { it.isFile && it.extension in listOf("kt", "java", "kts") }
                        .filter { !it.path.contains("/build/") }
                        .toList()
                } else {
                    listOf(f)
                }
            }

        filesToScan.forEach { dir ->
            file(dir).walkTopDown()
                .forEach { file ->
                    val content = file.readText()
                    val newContent = content.replace(regex) { matchResult ->
                        val firstYear = matchResult.groupValues[1]
                        // Only update if current year is greater than first year
                        if (currentYear.toString() != firstYear) {
                            "Copyright (C) $firstYear–$currentYear Claus Niesen"
                        } else {
                            matchResult.value
                        }
                    }
                    if (newContent != content) {
                        file.writeText(newContent)
                        println("Updated copyright in: ${file.path}")
                    }
                }
        }
    }
}