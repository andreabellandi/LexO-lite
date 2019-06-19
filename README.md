# LexO-lite v1.0

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
LexO comes with an empty lexicon having the namespace equals to the init value of the above <i>lexiconNamespace</i> parameter (you can find it in the lexicon folder of the project).
If you place it to the <i>lexiconFolder</i> folder you can run LexO over tomcat, and you are ready for starting to construct your multilingual lexicon.
<br/><br/>
It is important to note that currently LexO has not an ontology import button, so if you want to upload an ontology you have to manually change its namespace to the value stated 
in the <i>domainOntologyNamespace</i> parameter, and place it to the <i>domainOntologyFolder</i> folder.
Then you have to restart tomcat.

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
    Once you created an administrator user, you can use the following credentials to login to LexO:

 ```
user = admin
password = admin
  ```

By clicking on the user icon placed at the top of the interface, you can manage your users team by means of the panel shown in the following image.
</p>

<img src="images/LexO-lite_userPanel.png"/>

<p>
Then you can modify user and password of the administrator account and create some users on the basis of the following three profiles:
<ul>
<li>Administrator: this profile is related to the team leader; it can add or remove users to the team, and assign them their profiles. It can create, modify, delete, validate lexical entries, and import the domain ontology.</li>
<li>User: this profile is related to the editor role. It can create, modify, delete lexical entries, and link senses to ontological entities.</li>
<li>Viewer: this profile can only access the dictionary view of lexical entries.</li>
</ul>
</p>

<a name="create-lexicon"><h4>Create your own lexicon</h4></a>

<img src="images/LexO-lite_editPanel.png"/>

<p>
The above figure shows how the LexO interface appears.  
It is mainly composed of two parts. The leftmost column allows scholars to browse lemmas, 
forms and senses, according to the OntoLex-Lemon core model. 
If the resource is multilingual, then users have the possibility of filtering lemmas, 
forms and senses by language. Information related to the selected entry is shown in 
the central panel where the system shows the selected lexical entry alongside the 
lemma (red box), its forms (blue boxes) and lexical senses (yellow boxes). 
It is also possible to list the concepts belonging to a reference ontology
</p>

  <p>
By means of the new button at the bottom of the interface, you can create new lexicon languages. The form to fill is the following:
</p>
<center>
<img src="images/LexO-lite_newLanguage.png" width=100/>
</center>
<p>
<ul>
<li>Language name (manadatory): the language tag (e.g., "en");
<li>Language code uri (optional): it is recommended to use the Dublin Core language property 
with reference to either <a href="http://www.lexvo.org/"> Lexvo.org</a> or <a href="http://id.loc.gov/vocabulary/iso639-1.html"> The Library of Congress Vocabulary</a>:
<ul>
<li> Lexvo.org codes should be of the form <i>http://www.lexvo.org/id/iso639-3/xxx</i> where <i>xxx</i> is the 3-Letter ISO 639-3 code</li>
<li> Library of Congress codes should be of the form <i>http://id.loc.gov/vocabulary/iso639-1/xx</i> where <i>xx</i> is the 2-Letter ISO 639-1 code</li>
</ul>
</li>
<li>Linguistic catalogue (manadatory): the value is automatically set to LexInfo, an ontology that provides data categories for the OntoLex-Lemon model;</li>
<li>Description (optional): a textual description;</li>
<li>Creator (optional): some reference to the creator of the lexicon language;</li>
</ul>
</p>

<a name="export"><h4>Export</h4></a>
  <p>
TODO
</p>
</p>

<a name="restapi"><h3>LexO's REST API</h3></a>
<p>
    LexO comes with a basic set of services returning information about your lexicon. Currently LexO implements the following services (let assume that http://localhotst:8080/LexO-lite preceeds each of them):
  </p>

| Description | Example of invocation |
| :--- | :--- |
| List all the *lexicon languages* | `/lexicon/languages` |
| List all the *lexicon lemmas* | `/lexicon/lemmas?lang=l&startswith=a&limit=10` |
| List all the *lemma metadata* | `/lexicon/lemma?id=lemma_id` |
| List all the lemmas involved in a specific *lexico-semantic relation* with a given lemma | `/lexicon/lemmaByRel?lang=l&entry=lemma_id&rel=translation` |
| Get some lexicon *statistics* | `/lexicon/statistics?lang=l` |

<a name="ack"><h3>Acknowledgemt</h3></a>
<p>
    This research has been conducted in the context of the cooperation agreement between Guido Mensching, director of the DiTMAO project at the Seminar für Romanische Philologie of the Georg-August-Universität Göttingen, and the Istituto di Linguistica Computazionale “A. Zampolli” of the Italian National Research Council (29 August 2016).
  </p>



  
