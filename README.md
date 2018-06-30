# ont4j

![Language](https://img.shields.io/badge/JAVA-%3E%3D1.8-red.svg)
![license](https://img.shields.io/badge/License-MIT-blue.svg)
![Progress](https://img.shields.io/badge/Version-Release.v1-brightgreen.svg)

    A tool for importing Ontology into Neo4j.
    
    Any type of Ontology file will be allowed, like OWL, RDF, etc. 
### Usage
    This project uses embedded way to access the graph database. 
    So we just need to know the database directory. And we also need to give our ontology file path.
    Write the directory and path in the application.properties.
    
    Example:
        ontPath = .../pizza.owl
        dbDir = .../databases/graph.db
    
    Before run this program, please make sure that the database is closed.
    Then run the Demo.java, and we can find our ontology file is stored in neo4j as a graph.

### Example
    The picture below shows the part of the pizza.owl from stanford university as a graph.

![image](https://github.com/ylins/ont4j/blob/master/src/main/resources/img/graph.png)

    Sincerely welcome you to pull requests.     
