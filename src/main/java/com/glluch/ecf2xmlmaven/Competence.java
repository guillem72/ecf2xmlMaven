/*
 * Copyright (C) 2016 Guillem LLuch Moll guillem72@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.glluch.ecf2xmlmaven;

import com.glluch.findterms.Surrogate;
import com.glluch.findterms.Vocabulary;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.json.simple.JSONObject;

/**
 *
 * @author Guillem LLuch Moll guillem72@gmail.com
 */
public class Competence {

    private String description;
    private String group;
    private String title;
    private String code;
    private HashMap<Integer, String> levels;
    private HashMap<String, String> knowledges;
    private HashMap<String, String> skills;
    private HashMap <String,Integer> terms;
    private transient String serverUrl="http://localhost:8888/solr/ecf";
    private HashMap <String, Integer>related=new HashMap <>() ; //terms and theirs syncets
    public transient double term_boost=2.0;
    public transient double related_boost=1.0;
   
    public HashMap <String, Integer> buildRelated() {
        if(!related.isEmpty()) return related;
        Set ot=terms.keySet();
         Vocabulary.get();
    Surrogate surro=new Surrogate(Vocabulary.jenaModel);
        for (Object t0:ot){
            String t=(String)t0;
            surro.setTerm(t);
            ArrayList<String> related0=surro.getSurrogates();
            addAllinRelated(related0,terms.get(t0));
        }
        return related;
    }
    
    private void addAllinRelated(ArrayList<String> rel,int boost){
        for (String r:rel){
            related.put(r, boost);                    
        }
    }

    public void setRelated(HashMap <String, Integer> related) {
        this.related = related;
    }

    
    
    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public Competence(String group, String title, String code) {
        this.group = group;
        this.title = title;
        this.code = code;
        this.levels = new HashMap<>();
        this.knowledges = new HashMap<>();
        this.skills = new HashMap<>();
    }

    public void add(String what, String value) {
        if (what.equals("description")) {
            description = value;
        }
        if (what.equals("group")) {
            group = value;
        }
        if (what.equals("title")) {
            title = value;
        }
        if (what.equals("code")) {
            code = value;
        }

    }

    public HashMap<String, Integer> getTerms() {
        return terms;
    }

  public ArrayList<String> getTermsWithoutCounts(){
      ArrayList <String> oterms=new ArrayList <>();
      oterms.addAll(terms.keySet());
      return oterms;
  }

    public void addTerm(String term, int count) {
        this.terms.put(term, count);
        
    }

    public void setTerms(HashMap<String, Integer> terms) {
        this.terms = terms;
    }

    
    public void addSkill(String id, String skill) {
        skills.put(id, skill);
    }

    public void addLevel(int level, String desc) {
        levels.put(level, desc);
    }

    public void addKnow(String id, String k) {
        knowledges.put(id, k);
    }

    public HashMap<Integer, String> getLevels() {
        return levels;
    }

    public HashMap<String, String> getKnowledges() {
        return knowledges;
    }

    public HashMap<String, String> getSkills() {
        return skills;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        String info = "";
        info += "\n\nTitle: " + title;
        info += "\nDescription: " + description;
        //info+="\nlevels:\n"+levels.toString();
        info+="\nlevels:\n"+levels2String();
        return info;
    }

    protected String levels2String(){
        String text="";
       Set nivells=levels.keySet();
       for (Object nivell: nivells){
           text+=nivell+": "+levels.get((int) nivell)+"\n";
       }
        return text;
    }
    
    public String allText(){
        String m=this.code;
        m+=" "+this.toString();
        m=m.replace("\n", " ");
        return m;
    }
    
    public void textWriter(String path) throws IOException{
        String fileTitle=this.code+"txt";
        String textCompetence="";
        textCompetence+=this.description+"\n";
        textCompetence+=this.levels2text();
         textCompetence+=this.know2text()+"\n";
          textCompetence+=this.skills2text()+"\n";
        
        FileUtils.writeStringToFile(new File(path+fileTitle), textCompetence, "utf8");
    }

    protected String levels2text(){
        String res="";
        Set index=this.levels.keySet();
        for (Object i0:index){
            int i=(int) i0;
            res+=this.levels.get(i)+"\n";
        }
        return res;
        }
    
       protected String know2text(){
        String res="";
        Set index=this.knowledges.keySet();
        for (Object i0:index){
            String i=(String) i0;
            res+=this.knowledges.get(i)+".";
        }
        return res;
        }
    
      protected String skills2text(){
        String res="";
        Set index=this.skills.keySet();
        for (Object i0:index){
            String i=(String) i0;
            res+=this.skills.get(i)+".";
        }
        return res;
        }
      
      
    

//Gson gson = new Gson();
    //String json = gson.toJson(obj);  
    
    
    
     public void jsonWriter(String path, boolean pretty) throws IOException{
        
        Gson gson;
        if (pretty){
        gson =  new GsonBuilder().setPrettyPrinting().create();}
        else
        {gson = new Gson(); }
        String jsonCompetence = gson.toJson(this);
        //Utils.echo(jsonCompetence);
        String fileTitle=this.code+"json";
        FileUtils.writeStringToFile(new File(path+fileTitle), jsonCompetence, "utf8");

	
    }
    
    public void jsonWriterRaw(String path) throws IOException{
        String m=this.code;
        m+=" "+this.toString();
        m=m.replace("\n", " ");
        JSONObject jsonCompetence = new JSONObject();
        jsonCompetence.put("code", this.code);
        jsonCompetence.put("txt", m);
        String fileTitle=this.code+".json";
        FileUtils.writeStringToFile(new File(path+fileTitle), jsonCompetence.toJSONString(), "utf8");

	
    }
    
    
    
    public void jsonIEEEWriter(String path) throws IOException{
        String m=this.terms.toString();
        JSONObject jsonCompetence = new JSONObject();
        jsonCompetence.put("code", this.code);
        jsonCompetence.put("txt", m);
        String fileTitle=this.code+"json";
        FileUtils.writeStringToFile(new File(path+fileTitle), jsonCompetence.toJSONString(), "utf8");
    }
    
    //it produces error
    public void toSolr() throws SolrServerException, IOException{
        SolrClient solr = new HttpSolrClient(serverUrl);
        //solr.setDefaultCollection("ecf");
        SolrInputDocument doc1 = new SolrInputDocument();
        doc1.addField("title", this.code);
        doc1.addField("content", this.terms.toString());
        solr.add(doc1);
        //System.out.println(a);
    }
    
    public void toXmlSolr(String path) throws IOException{
        String xml="<add><doc>"+System.lineSeparator();
        xml+="<field name=\"id\"";
        xml+=">";
        xml+=this.code;
         xml+="</field>"+System.lineSeparator();
         xml+="<field name=\"type\">competence</field>"+System.lineSeparator();
                
        xml+=this.terms2xml("term")+System.lineSeparator();
        xml+=this.rterms2xml("term")+System.lineSeparator();
        xml+="</doc></add>";
        //competencesXMLsolr
        String fileTitle=this.code+"xml";
        FileUtils.writeStringToFile(new File(path+fileTitle), xml, "utf8");
    }
    
    
     
      protected String rterms2xml(String field_name){
        String text="";
      if (related.isEmpty()) return "";
      Set rterms=related.keySet();
       for (Object t: rterms){
          text+="<field name=\""+field_name+"\" "
                   + " boost=\""+related_boost*related.get(t)+"\""
                   + ">"
                   +t+"</field>"+System.lineSeparator();
       }
        return text;
    }
    protected String terms2xml(String field_name){
        String text="";
      Set pterms=terms.keySet();
       for (Object t: pterms){
           text+="<field name=\""+field_name+"\" "
                   + " boost=\""+term_boost*terms.get(t)+"\""
                   + ">"
                   +t+"</field>"+System.lineSeparator();
       }
        return text;
    }
}
