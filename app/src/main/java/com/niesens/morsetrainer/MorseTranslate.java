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

public class MorseTranslate {
    private static final CharToMorseTable charToMorse = new CharToMorseTable()
            .putAll(baseCharacters())
            .putAll(prosignCharacters())
            .putAll(russianCharacters());

    private static CharToMorseTable baseCharacters() {
        return new CharToMorseTable()
                .put("a", ".-")
                .put("b", "-...")
                .put("c", "-.-.")
                .put("d", "-..")
                .put("e", ".")
                .put("f", "..-.")
                .put("g", "--.")
                .put("h", "....")
                .put("i", "..")
                .put("j", ".---")
                .put("k", "-.-")
                .put("l", ".-..")
                .put("m", "--")
                .put("n", "-.")
                .put("o", "---")
                .put("p", ".--.")
                .put("q", "--.-")
                .put("r", ".-.")
                .put("s", "...")
                .put("t", "-")
                .put("u", "..-")
                .put("v", "...-")
                .put("w", ".--")
                .put("x", "-..-")
                .put("y", "-.--")
                .put("z", "--..")
                .put("1", ".----")
                .put("2", "..---")
                .put("3", "...--")
                .put("4", "....-")
                .put("5", ".....")
                .put("6", "-....")
                .put("7", "--...")
                .put("8", "---..")
                .put("9", "----.")
                .put("0", "-----")
                .put(".", ".-.-.-")
                .put(",", "--..--")
                .put(":", "---...")
                .put("?", "..--..")
                .put("\\", ".----.")
                .put("-", "-....-")
                .put("/", "-..-.")
                .put("(", "-.--.-")
                .put(")", "-.--.-")
                .put("\"", ".-..-.")
                .put("@", ".--.-.")
                .put("=", "-...-")
                .put(" ", "|");
    }

    private static CharToMorseTable prosignCharacters() {
        return new CharToMorseTable()
                .put("<aa>", ".-.-")
                .put("<ar>", ".-.-.")
                .put("<as>", ".-...")
                .put("<bk>", "-...-.-")
                .put("<bt>", "-...-") // also <TV>
                .put("<cl>", "-.-..-..")
                .put("<ct>", "-.-.-")
                .put("<do>", "-..---")
                .put("<kn>", "-.--.")
                .put("<sk>", "...-.-") // also <VA>
                .put("<va>", "...-.-")
                .put("<sn>", "...-.") // also <VE>
                .put("<ve>", "...-.")
                .put("<sos>", "...---...");
    }

    private static CharToMorseTable russianCharacters() {
        return new CharToMorseTable()
                .put("а", ".-")
                .put("б", "-...")
                .put("в", ".--")
                .put("г", "--.")
                .put("д", "-..")
                .put("е", ".")
                .put("ё", ".")
                .put("ж", "...-")
                .put("з", "--..")
                .put("и", "..")
                .put("й", ".---")
                .put("к", "-.-")
                .put("л", ".-..")
                .put("м", "--")
                .put("н", "-.")
                .put("о", "---")
                .put("п", ".--.")
                .put("р", ".-.")
                .put("с", "...")
                .put("т", "-")
                .put("у", "..-")
                .put("ф", "..-.")
                .put("х", "....")
                .put("ц", "-.-.")
                .put("ч", "---.")
                .put("ш", "----")
                .put("щ", "--.-")
                .put("ъ", ".--.-.")
                .put("ы", "-.--")
                .put("ь", "-..-")
                .put("э", "..-..")
                .put("ю", "..--")
                .put("я", ".-.-");
    }

    public static String textToMorse(String text) {
        StringBuilder morseCode = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '<') {
                StringBuilder prosign = new StringBuilder();
                while (text.charAt(i) != '>') {
                    prosign.append(text.charAt(i));
                    i++;
                }
                prosign.append(text.charAt(i));
                System.out.println(prosign.toString());
                morseCode.append(charToMorse.get(prosign.toString()));
            } else {
                morseCode.append(charToMorse.get(text.charAt(i)));
            }
            morseCode.append(" ");
        }
        return morseCode.toString();
    }
}
