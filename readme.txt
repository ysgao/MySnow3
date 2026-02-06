This version (0.6, 20150219) of MySnow browser fixed the following issues: 

1. MySnow will work with any default Java. If you do not have Java, Please install the latest Java from Oracle. 
2. The first start of searching is improved, but it still requires a bit long time to load all data into memory. 
3. The long descriptions in searching results are displayed fully now in this version. 

MySnow browser is built on top of the NetBeans platform and Neo4j graph database. The basic functions are: 
1. Search - popup list of results while typing; supports partial spelling and combinations regardless of order; limits searching to the specific hierarchy by selecting a top-level concept in drop down list in the search field. Cmd + I will bring it back to the default searching for all hierarchies. The other option is to include the prefix in the text for searching. The first three letters are used as prefixes, e.g. pro for Procedures, fin for Findings (include disorders). 
2. Taxonomy - navigate hierarchies and double click concept to display parents, siblings and children for best display in limited space. Right click of concept provides common functions, such as copy ID & FSN, generate text files for descendants or transitive closure. 
3. Concept details - concept id, descriptions, defining characteristics, number counts of children or descendants, and modeling. 

This is a very early version and has not been fully tested. Please do not use it as a reliable source of information. The recommended IHTSDO browser is http://browser.ihtsdotools.org 

System requirements: 
1. Mac OS X 10.8 or later, and 2GB free RAM for application. 
2. Java installed, if not, download and install the latest Java form Oracle. 
3. International release RF1 data files (concept, relationship and description) under one folder. Import them by using import function in this software. 

Steps for import RF1 release data: 
1. Open import data window by selecting Import from Window in the menu bar in MySnow. 
2. Click the 'Select folder' button to select the folder, such as Terminology under RF1release. The software will locate release files to import. 
3. Click the 'Import' button. The software will import data and restart when it is done. 

The latest IHTSDO release can be downloaded from 
https://csfe.aceworkspace.net/sf/frs/do/viewSummary/projects.snomed_ct_international_releases/frs 

Notes on importing Beta release (Changes are not required for normal release files): 
MySnow software checks the naming convention of the release files. Therefore, it does not import beta release data files because of the extra ‘x’ in their names. To import beta release, the ‘x' needs to be removed from the file names of Concepts, Descriptions and Relationship data files. Three files should be listed as the following under RF1Release/Terminology/Content/ folder : 

sct1_Concepts_Core_INT_20140131.txt 
sct1_Descriptions_en_INT_20140131.txt 
sct1_Relationships_Core_INT_20140131.txt 

Then it will be the same as routine import of release data.

Neo4j config and migration:
1. Neo4j embedded config file location (optional):
   - Default: ~/.mysnow2/neo4j.conf
   - Override: pass -Dmysnow.neo4j.conf=/path/to/neo4j.conf
   - Example content:
     dbms.pagecache.memory=3g
2. Offline store migration helper:
   - Script: tools/neo4j-migrate.sh
   - Ant target (from NetBeans or CLI): ant -f build.xml neo4j-migrate
   - Default Neo4j home: /opt/homebrew/opt/neo4j/libexec
   - Override properties:
     ant -f build.xml neo4j-migrate -Dneo4j.home=/path/to/neo4j-home -Dneo4j.db.home=/path/to/data/databases -Dneo4j.db.name=neo4j -Dneo4j.conf=/path/to/neo4j.conf
