Word Lists
==========

Claus' Morse Code Trainer (Learn CW) uses the "Claus' Morse Trainer" folder at the root of the internal storage to store the word list for your training.

Each world list is a file with characters, words or phases that you want to learn.  Several files are supplied when you install the application.  Additional
files can be found at https://github.com/cniesen/morsetrainer/tree/master/additional_word_lists .  You can also create your own word list, which is
highly recommended to optimize your training to the character and words that you need.


Creating Custom Word List
-----------------------------------

Just create a text file with each character, word or phrase on a separate line. If the Morse text and spoken text is different then separate them with a
vertical pipe "|". E.g.:

```
hello
tu|thank you
fb|fine business
good bye
```

The order does not matter as the trainer is randomizing which word is picked next.  If you want a word to be practiced more often then just add it
multiple times to the file.


Uninstalling
---------------

Uninstalling the program does not automatically remove the "Claus' Morse Trainer" folder.  This is on purpose since you may have customized files
in that directory that you don't want to loose.  During (re)install no files will be created or overridden if the folder exists already, thus preserving
the original files. You may choose to delete the folder from your devices internal storage after uninstalling.