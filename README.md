# ecf2xmlMaven
E-competence framework reader and writer. Maven implementation

## How to write a json for each competence

You need a txt file with the competences. For instance, I have two versions:
  
- <https://github.com/guillem72/ecf2xmlMaven/blob/master/resources/profilesORIGINAL.txt>
The original file transformed to txt by apache pdfbox
- <https://github.com/guillem72/ecf2xmlMaven/blob/master/resources/profilesAcronims.txt> 
The same as above, but with acronyms expanded. For example "SWOT" is changed by
"Strengths, Weaknesses, Opportunities and Threats (SWOT)".

Put the file in resources directory (both of them are there now) and call the function toJson.
The results will be in resources/compenteces directory.