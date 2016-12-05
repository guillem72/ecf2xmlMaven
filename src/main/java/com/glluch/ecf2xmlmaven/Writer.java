/*
 * The MIT License
 *
 * Copyright 2016 Guillem LLuch Moll <guillem72@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.glluch.ecf2xmlmaven;

import com.glluch.findterms.Surrogate;
import com.glluch.findterms.Vocabulary;
import com.glluch.utils.JMap;
import com.glluch.utils.Out;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Guillem LLuch Moll
 */
public class Writer {

    public static transient boolean verbose = true;

    protected static Surrogate surro = new Surrogate(Vocabulary.getJenaModel(), 1);
    protected static IEEEMapper ieee = new IEEEMapper();

    public static transient boolean debug = true;

    public static void competencesParts2Solr(Collection <Competence> 
        competences,String path) throws IOException{
        Iterator<Competence> ic = competences.iterator();
        if (ic.hasNext()){
        Competence c=ic.next();
        show("Processing "+c.getTitle()+" "+c.getCode());
            competenceParts2Solr(c,path);
        }
    
    }
    
    
    /**
     * Given a competence produces xml files ready to put in apache solr. TODO
     * specify the solr schema.xml
     *
     * @param comp The competence to be processed.
     * @param path The directory where the files will be written.
     * @throws java.io.IOException
     */
    public static void competenceParts2Solr(Competence comp, String path) throws IOException {

        String title = comp.getTitle();
        String group = comp.getGroup();
        String code = comp.getCode();
        String comment="Competence: " + title + ", Group: " + group + ", Code: " + code;
        //Description
        String description = comp.getDescription();
        writePart2Solr(path, description, "Description", code + "Description",
            comment);
        
        //Knowledges
        HashMap<String, String> knowledges = comp.getKnowledges();
        writeHM2solr(knowledges,path, "Knowlegde",code,
            comment);
        
        //Levels
        HashMap<Integer, String> levels0 = comp.getLevels();
        HashMap<String, String> levels=JMap.is2ss(levels0);
        writeHM2solr(levels,path, "level",code+"L", comment);
        
        //Skills
        HashMap<String, String> skills = comp.getSkills();
        writeHM2solr(skills,path,"skill",code,comment);
        
    }

    protected static void writeHM2solr (HashMap<String, String> map, String path, 
        String solrType, String code, String solrComment ){
          map.forEach((String key, String value) -> {
         
              try {
                  writePart2Solr(path, value, solrType, code+key, solrComment);
              } catch (IOException ex) {
                  Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
              }
           
        });//end forEach
    }
    
    
    protected static void writePart2Solr(String path, String txt,
        String solrType, String id, String solrComment) throws IOException {

        HashMap<String, Integer> terms;
        terms = ieee.findTerms(txt);
        if (terms.size() > 0) {
            String xml = "<add><doc>" + System.lineSeparator();
            //Mandatory params
            xml += "<field name=\"id\"";
            xml += ">";
            xml += id;

            xml += "</field>" + System.lineSeparator();
            xml += "<field name=\"type\">" + solrType;
            xml += "</field>" + System.lineSeparator();

            //terms
            xml += terms2xml("ieee_term", terms) + System.lineSeparator();
            //optional elements
            if (StringUtils.isNotEmpty(solrComment)) {
                
                xml += "<field name=\"comment\">" + solrComment;
                xml += "</field>" + System.lineSeparator();
            }

            //end of xml
            xml += "</doc></add>";
            //competencesXMLsolr
            String fileTitle = path + id + ".xml";
            FileUtils.writeStringToFile(new File(fileTitle), xml);
        } else {
            show(id + " has no ieee terms");
        }

    }

    protected static String terms2xml(String field_name, HashMap<String, Integer> terms) {
        String text = "";
        Set pterms = terms.keySet();
        for (Object t : pterms) {
            text += "<field name=\"" + field_name + "\" "
                + " boost=\"" + terms.get(t) + "\""
                + ">"
                + t + "</field>" + System.lineSeparator();
        }
        return text;
    }

    protected static void writePart2Solr(String path, String txt,
        String solrType, String id) throws IOException {
        writePart2Solr(path, txt, solrType, id, null);

    }

    /**
     * Sent a message to the console depens on the parametre verbose. If it is
     * true (on), the text is shown.
     *
     * @param text The text to be shown
     */
    protected static void show(String text) {
        if (verbose) {
            Out.p(text);
        }
    }

    /**
     * Sent a message to the console depens on the parametre debug. If it is
     * true (on), the text is shown.
     *
     * @param text The text to be shown
     */
    protected static void debug(String text) {
        if (debug) {
            Out.p(text);
        }
    }

}
