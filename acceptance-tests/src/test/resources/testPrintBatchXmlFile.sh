#!/bin/bash

echo "Running print batch xml file validator on $1"
echo "Example: ./testPrintBatchXmlFile.sh myFile.xml"
echo "To get help or customize this script type in the command line 'man xmllint'"
echo ""

#actual-print-batch-output.xml is an invalid file should be a valid file.
#invalid-languageCode-in-output-actual-print-batch-output.xml is an invalid file

xmllint --schema BadgePrintExtract.xsd $1
