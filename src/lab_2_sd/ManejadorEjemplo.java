/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab_2_sd;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.Attributes;  
import org.xml.sax.SAXException;  
import org.xml.sax.helpers.DefaultHandler;  
  
public class ManejadorEjemplo extends DefaultHandler{  
    private String valor = "";
    private String cadena = "";
    private String titulo = "";
    
    Mongo mongo = new Mongo("localhost",27017);
    
    DB db = mongo.getDB("test");
    DBCollection coleccion = db.getCollection("test");

   @Override  
   public void startDocument() throws SAXException {  
      System.out.println("\nPrincipio del documento...");  
   }  
  
   @Override  
   public void endDocument() throws SAXException {  
      System.out.println("\nFin del documento...");  
   }  
  
   @Override  
   public void startElement(String uri, String localName, String name,  
        Attributes attributes) throws SAXException {
   }  
     
   @Override  
   public void characters(char[] ch, int start, int length)  
         throws SAXException {  
      //System.out.println("\nProcesando texto dentro de una etiqueta... ");
      
      cadena = new String(ch, start, length);
      valor = valor + cadena;
   }  
  
   @Override  
   public void endElement(String uri, String localName, String name)  
         throws SAXException {  
       if(localName.equals("title")){
           titulo = valor;
//           System.out.println(titulo);
       }
       if (localName.equals("page")){
           try {
               //System.out.println(valor);
               valor = filtroStopWords.filtrar(valor);
               //System.out.println("*********************************************************************************");
               System.out.println(titulo);
               System.out.println(valor);
               
               titulo = titulo.replaceAll("[^a-z A-Z]","");
                              
               BasicDBObject document = new BasicDBObject();
               document.put("titulo", titulo);
               //document.put("cuerpo", valor);
               coleccion.insert(document);
               
               valor = "";
               System.out.println("----------------------");
               
           } catch (IOException ex) {
               Logger.getLogger(ManejadorEjemplo.class.getName()).log(Level.SEVERE, null, ex);
           }
        }
       if (localName.equals("siteinfo")){
           valor = "";
        }
       if (localName.equals("format")){
           valor = "";
        }


   }  
  
}