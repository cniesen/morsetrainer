/*
 *  Copyright (C) 2020 Claus Niesen
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

package com.niesens.morsetrainer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CharToMorseTable {
    private Map<String, String> charToMorse = new HashMap<>();

    /**
     * Add an entry to CharToMorseTable.
     *
     * @param character
     * @param morse the morse code representation of the character
     * @return this
     * @throws IllegalArgumentException if key already exist
     */
    public CharToMorseTable put(String character, String morse) {
        if (charToMorse.containsKey(character)) {
            throw new IllegalArgumentException("Duplicate key '" + character);
        }
        charToMorse.put(character, morse);
        return this;
    }

    /**
     * Add the entries of another CharToMorseTable.
     *
     * @param charToMorseTable the other CharToMorseTable
     * @return this
     * @throws IllegalArgumentException if key already exist
     */
    public CharToMorseTable putAll(CharToMorseTable charToMorseTable) {
        for (Map.Entry entry: charToMorseTable.entrySet()) {
            put((String) entry.getKey(), (String) entry.getValue());
        }
        return this;
    }

    /**
     * Get the morse representation of the character
     *
     * @param character
     * @return morse code representation of the character
     */
    public String get(String character) {
        return charToMorse.get(character.toLowerCase());
    }

    /**
     * Get the morse representation of the character
     *
     * @param character
     * @return morse code representation of the character
     */
    public String get(Character character) {
        return get(Character.toString(character));
    }

    /**
     * The entry set of the CharToMorseTable.
     * @return entry set of CharToMorseTable
     */
    public Set<Map.Entry<String, String>> entrySet() {
        return charToMorse.entrySet();
    }

}
