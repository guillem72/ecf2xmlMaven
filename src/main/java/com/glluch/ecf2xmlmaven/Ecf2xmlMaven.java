package com.glluch.ecf2xmlmaven;

import com.glluch.findterms.Surrogate;
import com.glluch.findterms.Vocabulary;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.apache.solr.client.solrj.SolrServerException;




public class Ecf2xmlMaven {

    public static void main(String[] args)  throws IOException, SolrServerException{
       competences();       
      }
    public static void competences() throws IOException{
        
        ecfReader ecfR = new ecfReader();
        HashMap<String, Competence> competences=ecfR.parseTxtDocument();
        //System.out.println(competences);
        //String target="resources/competences/";
        //String targetIEEE="resources/competences2IEEE/";
        
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
