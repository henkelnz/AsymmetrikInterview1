Given a directory containing text files, each containing OCR output for a single business card, 
generate a file containing contact info for all input files. 

For example, given:
-ocr_bcp
--ocr1.txt
--ocr2.txt
--ocr3.txt

It might create a file like this:
---------------------------------

Name: Mike Smith
Phone: 4105551234
Email: msmith@asymmetrik.com

Name: Lisa Haung
Phone: 4105551234
Email: lisa.haung@foobartech.com

Name: Arthur Wilson
Phone: 17035551259
Email: awilson@abctech.com

---------------------------------

USAGE:

java -jar cmdbcp.jar inputDir outputFile <wordsFile> <namesFile>


wordsFile is an optional file of common words (not proper nouns). namesFile is an optional file of common names.
Each is one per line. By default, the program comes with common words from /usr/share/dict/words, and names (first
and last) from US Census data. Bear in mind that names have precedence over words, so the program will consider
"smith" a name if it is found in namesFile even if it is also found in wordsFile.