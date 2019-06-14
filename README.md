# LexO-lite v1.0

<style>
table {
  font-family: arial, sans-serif;
  border-collapse: collapse;
  width: 100%;
}

td, th {
  border: 1px solid #dddddd;
  text-align: left;
  padding: 8px;
}

tr:nth-child(even) {
  background-color: #dddddd;
}
</style>

This is the first version of LexO, a collaborative web editor for easily building and managing lexical and terminological resources in the context of the Semantic Web, based on the OntoLex-Lemon model.

<b><h2>Table of content</h2></b>

<ul><li>
  <a href="#what-is-LexO-for">What is LexO for?</a>
</li><li>
    <a href="#how-to-deploy">How to deploy</a>
</li>
<ul>
  <li>
    <a href="#prepare-the-environment">Preparation of the environment</a>
</li>
    <li>
    <a href="#configuration-file">Initialization and configuration file</a>
</li>
  <li>
    <a href="#run-lexo">Run LexO</a>
</li>
  </ul>
<li>
    <a href="#how-to-use">How to use</a>
</li>
<ul>
  <li>
    <a href="#create-users">Create users</a>
</li>
    <li>
    <a href="#create-lexicon">Create your own lexicon</a>
</li>
  <li>
    <a href="#export">Export the resource</a>
</li>
  </ul>
<li>
    <a href="#restapi">LexO's REST API</a>
</li>
<li>
    <a href="#ack">Acknowledgemt</a>
</li>
  <!--<li>
    <a href="#reference">Reference</a>
</ul>-->

<a name="what-is-LexO-for"><h3>What is LexO for? </h3></a>
<p>
  LexO, a collaborative web editor for easily building and managing lexical and terminological resources in the context of the Semantic Web. The adoption of Semantic Web technologies and the Linked Data paradigm has been driven by the need to ensure the construction of resources that are interoperable and can be shared and reused by the scientific community. 
  
  LexO's primary objective is to enable lexicographers, scholars and humanists to create a resource ex novo where information can be easily manually curated by humans, that is fundamental for collecting reliable, fine-grained, and explicit information. LexO attempts to make the <a href="https://www.w3.org/community/ontolex/wiki/Final_Model_Specification">OntoLex-Lemon</a> model accessible to all those do not have technical skills, allowing the adoption of new technological advances in the Semantic Web by Digital Humanities.

  The first version of LexO deals with the core (lexical entry, form, lexical sense, ontology entity) and uses the <a href="https://www.w3.org/community/ontolex/wiki/Final_Model_Specification#Decomposition_.28decomp.29">decomp</a> module for representing multiwords, and the <a href="https://www.w3.org/community/ontolex/wiki/Final_Model_Specification#Metadata_.28lime.29">lime</a> module for metadata.  

</p>

<a name="how-to-deploy"><h3>How to deploy</h3></a>
<p>
<a name="prepare-the-environment"><h4>Prepare the environment</h4></a>
  <p>
    LexO requires:
<ul>
  <li>Apache Tomcat v8.0 or later</li>
  <li>MySql v5.0 or later</li>
  <li>Java 1.8</li>
  </ul>
  
  LexO is a Maven project and uses:
<ul>
  <li>OWL-API v5.0</li>
  <li>SPARQL-DL v3.0.0</li>
  <li>Hibernate v4.3.7</li>
  <li>Primefaces 6.0</li>
  <li>Jersey RESTful Web Services framework v2.23.2</li>
</ul>
  
<br/>  
 Download LexO-lite source code and compile it.
  </p>
  <a name="configuration-file"><h4>Initialization and configuration file</h4></a>
  <p>
  First of all, you have to create an empty schema in MySql called <b>LexO_users</b> for the users profiles management. Set the character encoding to UTF-8. You can use the following command:
  </p>
  
```
mysql> create database LexO_users character set UTF8 collate utf8_bin
```  
If you want to use another database name, you have to change it in the hibernate.cfg.xml file and recompile the project.

The lexolite.properties file contains some parameter to configure, as the following:
```
lexiconFolder, is the relative path w.r.t. your home (default value is .LexO-lite/)
lexiconNamespace, is the namespace of your lexicon followed by '#' (default value is http://lexica/mylexicon#)
lexiconFileName, is the file name of your lexicon (default name is mylexicon.owl)
lexiconExportFileName, is the file name of your export (default name is exportedLexicon)

domainOntologyFolder, is the relative path w.r.t. your home (default value is .LexO-lite/)
domainOntologyNamespace, is the namespace of your domain ontology followed by '#' (default value is http://ontologies/myontology#)
domainOntologyFileName, is the file name of your domain ontology (default name is domainOntology.owl)
```  
When you specified these values and your lexicon file (even empty) and, eventually, your domain ontology are placed in their correct path, you can run LexO over tomcat.
However, LexO comes with a little example of lexicon

<a name="run-lexo"><h4>Run LexO</h4></a>
  <p>
    You can access to the home of LexO by the following url:
  
  ```
  http://localhost:8080/LexO-lite
  ```  
  There are two servlets for creating and populating the database with one administrator user.
  
  ```
  http://localhost:8080/LexO-lite/servlet/domainCreator?command=create
  ```
  creates the database, and 
  
  ```
  http://localhost:8080/LexO-lite/servlet/domainCreator?command=preset
  ```
  creates the administrator user.
  
  </p>

<a name="how-to-use"><h3>How to use</h3></a>
<p>
<a name="create-users"><h4>Create users</h4></a>
  <p>
TODO
</p>
<a name="create-lexicon"><h4>Create your own lexicon</h4></a>
  <p>
TODO
</p>
<a name="export"><h4>Export</h4></a>
  <p>
TODO
</p>
</p>

<a name="restapi"><h3>LexO's REST API</h3></a>
<p>
    LexO comes with a basic set of services returning information about your lexicon. Currently LexO implements the following services:

    <table>
  <tr>
    <th>Company</th>
    <th>Contact</th>
    <th>Country</th>
  </tr>
  <tr>
    <td>Alfreds Futterkiste</td>
    <td>Maria Anders</td>
    <td>Germany</td>
  </tr>
  <tr>
    <td>Centro comercial Moctezuma</td>
    <td>Francisco Chang</td>
    <td>Mexico</td>
  </tr>
  <tr>
    <td>Ernst Handel</td>
    <td>Roland Mendel</td>
    <td>Austria</td>
  </tr>
  <tr>
    <td>Island Trading</td>
    <td>Helen Bennett</td>
    <td>UK</td>
  </tr>
  <tr>
    <td>Laughing Bacchus Winecellars</td>
    <td>Yoshi Tannamuri</td>
    <td>Canada</td>
  </tr>
  <tr>
    <td>Magazzini Alimentari Riuniti</td>
    <td>Giovanni Rovelli</td>
    <td>Italy</td>
  </tr>
</table>

    
  </p>
<a name="ack"><h3>Acknowledgemt</h3></a>
<p>
    This research has been conducted in the context of the cooperation agreement between Guido Mensching, director of the DiTMAO project at the Seminar für Romanische Philologie of the Georg-August-Universität Göttingen, and the Istituto di Linguistica Computazionale “A. Zampolli” of the Italian National Research Council (29 August 2016).
  </p>
  
