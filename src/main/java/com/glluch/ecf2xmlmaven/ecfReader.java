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

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;

/**
 * Created by Guillem LLuch Moll on 28/02/16.
 */
public class ecfReader {
//stat = start | group | title | desc | transition | level | know | skill

    private final String TITLE;
    private final String DESC;
    private final String TRANSITION;
    private final String LEVEL;
    private final String KNOW;
    private final String SKILL;
    private final String GROUP;

    public ecfReader() {
        TITLE = "title";
        DESC = "desc";
        TRANSITION = "transition";
        LEVEL = "level";
        KNOW = "know";
        SKILL = "skill";
        GROUP = "group";
    }

    /*
    The original document seams to produce some error
     */
    public Document parseXMLDocument() throws SAXException, ParserConfigurationException, IOException {

        String ecf = FileUtils.readFileToString(new File("resources/e-CF_V2.xml"), "utf8");
        //String xmlFile="file:resources/e-CF_V2.xml";
        DocumentBuilderFactory factory
                = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(ecf);
        return document;
        //System.out.println(document);
    }

    public HashMap<String, Competence> parseTxtDocument() throws IOException {
        HashMap<String, Competence> ecfs = new HashMap<>();
        final String ecfFile = FileUtils.readFileToString(new File("resources/profilesAcronims.txt"), "utf8");

        Status status = new Status();
        String[] lines = ecfFile.split("\n");
        String group = null;
        String stat;

        Competence comp = null;
        boolean mustAdd = false;
        for (String line : lines) {
            stat = status.move(line);
            //System.out.println(line);
            //System.out.println(stat);
            //System.out.println();
            if (stat.equals(GROUP)) {
                group = line;
            }//GROUP
            if (stat.equals(TITLE)) {
                //Utils.echo(stat);
                String[] parts = parseLine(stat, line);
                if (mustAdd) {
                    ecfs.put(comp.getCode(), comp);
                } else {
                    mustAdd = true;
                }
                comp = new Competence(group, parts[1], parts[0]);
                //Utils.echo("New comp created"+parts[0]);

            }//TITLE
            if (stat.equals(DESC)) {
                comp.setDescription(line);
            }//DESC
            if (stat.equals(LEVEL)) {
                String[] parts = parseLine(stat, line);
                //System.out.println(parts[1]);
                int levelnum = Integer.parseInt(parts[0].toString());
                //System.out.println(levelnum.getClass());
                try {
                    comp.addLevel(levelnum, parts[1]);
                } catch (java.lang.NullPointerException e) {
                    //System.out.println("Java sucks and null="+levelnum);
                }
            }//LEVEL
            if (KNOW.equals(stat)) {
                String[] parts = parseLine(stat, line);
                try {
                    comp.addKnow(parts[0], parts[1]);
                } catch (java.lang.NullPointerException e) {
                    System.out.println("Java sucks and null=" + parts[0] + " OR null=" + parts[1]);
                    Utils.echo(comp);
                }
            }//KNOW
            if (stat.equals(SKILL)) {
                String[] parts = parseLine(stat, line);
                try {
                    comp.addSkill(parts[0], parts[1]);
                } catch (java.lang.NullPointerException e) {
                    //System.out.println("Java sucks and null="+parts[0]+" OR null="+parts[1]);
                }
            }//SKILL

        }//for
        //System.out.println("Last comp "+comp.getCode());
        ecfs.put(comp.getCode(), comp);
        return ecfs;

    }

    public String[] parseLine(String status, String line) {
        String[] result = new String[2];

        if (status.equals(LEVEL)) {
            String[] parts = line.split(" - ");
            String[] subparts = parts[0].split(" "); //parts[0]=Proficiency Level 4
            result[0] = subparts[2].trim();//result[0]=4
            result[1] = Utils.join(parts, 1);//TODO join all other possible parts
        } else {
            String[] parts = line.split(" ");
            result[0] = parts[0].trim();//result[0]=4
            result[1] = Utils.join(parts, 1);//TODO join all other possible parts
        }
        //System.out.println("IN "+line+", OUT 0 "+result[0].toString()+ ", OUT 1 "+result[1].toString());
        return result;
    }

    private class Status {

        String config;
        HashMap<String, String> map;
        String now;

        public Status() throws IOException {
            this.config = FileUtils.readFileToString(new File("config/status.properties"), "utf8");
            this.map = new HashMap<>();
            this.now = "start";
            init();
        }

        private void init() {
            String[] lines = config.split("\n");
            for (String line : lines) {
                String[] v = line.split("=");
                map.put(v[1].trim(), v[0].trim());
            }
        }

        /**
         * Given a text return their status.
         *
         * @param line The text to be analized to set a new status
         * @return the status read from config/status.properties in the form
         * status=regex. If any regex matches, return desc, for description.
         */
        public String move(String line) {
            Set keys = map.keySet();
            this.now = DESC;
            for (Object regex0 : keys) {
                String regex = (String) regex0;
              

                if (Utils.match(regex, line)) {
                  
                    this.now = map.get(regex);
                   
                }
            }
            return this.now;
        }

        public String mapToString() {
            return map.toString();
        }

    }
}
