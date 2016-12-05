package com.glluch.ecf2xmlmaven;

import com.glluch.findterms.Surrogate;
import com.glluch.findterms.Vocabulary;
import com.glluch.utils.Out;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import org.apache.solr.client.solrj.SolrServerException;




public class Ecf2xmlMaven {

    public static void main(String[] args)  throws IOException, SolrServerException{
          
         //System.out.println(new File(".").getAbsoluteFile());
        ecfReader ecfR = new ecfReader();
         
          File origin=new File("resources/profilesAcronims.txt");
        HashMap<String, Competence> competences=ecfR.parseTxtDocument(origin);
        //String targetIEEE="resources/competences2IEEE/";
        //toJson(competences);
        //toText(competences);
        //parts2Solr(competences);
        Out.p(competences.values().size()+" competences");
        Writer.competencesParts2Solr(competences.values(),"resources/partsXmlSolr/");
    }   
   
    
    protected static void parts2Solr(HashMap<String, Competence> competences) throws IOException{
    String path="resources/partsXmlSolr/";
        Collection<Competence> values = competences.values();
        Object[] toArray = values.toArray();
        Competence comp=(Competence) toArray[0]; 
        Writer.competenceParts2Solr(comp, path);
    }
    
      public static void toText (HashMap<String, Competence> competences) throws IOException{
     Set keys=competences.keySet();
     String target="resources/competencesTxt/";
        for (Object key: keys){
            Competence c=competences.get((String)key);
            c.textWriter(target);
        }
    }
    
    public static void toJson (HashMap<String, Competence> competences) throws IOException{
     Set keys=competences.keySet();
     String target="resources/competences/";
        for (Object key: keys){
            Competence c=competences.get((String)key);
            c.jsonWriter(target, false);
        }
    }
    
    
    public static void toSolr(HashMap<String, Competence> competences) throws IOException{
    
        String targetXS="resources/competencesXMLsolr/";
        //String targetAcro="resources/acro";
        HashMap <String,ArrayList<String>> related;
        Surrogate surro=new Surrogate(Vocabulary.getJenaModel(),1);
        Set keys=competences.keySet();
        for (Object key: keys){
            Competence c=competences.get((String)key);
            //c.jsonWriter(target);
            HashMap<String,Integer> terms=new HashMap<>();
            IEEEMapper ieee=new IEEEMapper();
            terms=ieee.findTerms(c.allText());
            c.setTerms(terms);
            ArrayList<String> oterms=new ArrayList<>();
            oterms.addAll(terms.keySet());
           related=surro.surrogatesForeach(oterms);
           System.out.println(Utils.stringfyHashMapList(related));
           c.buildRelated();
           //System.out.println(Utils.stringfyList(c.getRelated()));
           c.toXmlSolr(targetXS);
                      
        }
    }

}
