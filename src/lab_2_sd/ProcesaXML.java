/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab_2_sd;

/**
 *
 * @author Rodrigo
 */
import java.io.BufferedReader;
import java.io.FileInputStream;  
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;  
import java.util.ArrayList;
  
import org.xml.sax.InputSource;  
import org.xml.sax.SAXException;  
import org.xml.sax.XMLReader;  
import org.xml.sax.helpers.XMLReaderFactory;  
  
/** 
 * Clase que procesa un XML de ejemplo mediante el handler SAX ManejadorEjemplo 
 *  
 * @author Xela 
 * 
 */  
public class ProcesaXML {  
    
    public static ArrayList<String> palabras;
  
    public static void main(String[] args) throws FileNotFoundException, IOException {  
        
        try (FileReader fr = new FileReader("stop-words-spanish.txt")) {
            if(fr == null){
                System.out.println("Archivo erroneo");
                System.exit(1);
            }
            try (BufferedReader bf = new BufferedReader(fr)) {
                palabras = new ArrayList();
                String aux1;
                
                while( (aux1 = bf.readLine() ) != null ){
                    palabras.add(aux1);
                }
            }
        }
        
        try {  
            // Creamos la factoria de parseadores por defecto  
            XMLReader reader = XMLReaderFactory.createXMLReader();  
            // Añadimos nuestro manejador al reader  
            reader.setContentHandler(new ManejadorEjemplo());           
            // Procesamos el xml de ejemplo  
            reader.parse(new InputSource(new FileInputStream("C:\\Users\\Nelson\\Desktop\\eswiki-20151202-pages-meta-current1.xml")));  
        } catch (SAXException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
  
   }  
  
}  