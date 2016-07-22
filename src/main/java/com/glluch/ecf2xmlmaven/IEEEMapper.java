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


import com.glluch.findterms.FindTerms;
import com.glluch.findterms.Vocabulary;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Given a Competence, search in all fields and return the terms from IEEE taxonomy 
 * found. Uses jsentvar.
 * @author Guillem LLuch Moll guillem72@gmail.com
 */
public class IEEEMapper {
   protected HashMap<String,Integer> terms=new HashMap<>();
   protected String doc;
    
    public HashMap<String,Integer> findTerms(String doc) {
    
    this.doc=doc;
       FindTerms finder=new FindTerms();
        FindTerms.vocabulary=Vocabulary.get();
        
        terms=finder.foundAndCount(doc);
        return terms;
    }
    
    public ArrayList<String> onlyTerms(String doc){
        ArrayList <String> oterms=new ArrayList<>();
        if (!doc.equals(this.doc)){
            findTerms(doc);
        }
        oterms.addAll(terms.keySet());
        return oterms;
    }
}
