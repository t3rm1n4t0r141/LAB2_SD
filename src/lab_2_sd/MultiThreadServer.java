/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab_2_sd;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import lab2_sd_mongo.InvertedIndex;

/**
 *
 * @author Nelson
 */
public class MultiThreadServer implements Runnable {
    
    Socket csocket;
    // Arreglo con las particiones index
    ArrayList<Index> particiones;
    private String fromClient;

    MultiThreadServer(Socket csocket, ArrayList<Index> Particiones) {
        this.csocket = csocket;  
        this.particiones = Particiones;
    }

    @Override
    public void run() {
        try {
            //Buffer para leer al cliente
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
            //Buffer para enviar al cliente
            DataOutputStream outToClient = new DataOutputStream(csocket.getOutputStream());

            //Recibimos el dato del cliente y lo mostramos en el server
            fromClient =inFromClient.readLine();
            System.out.println("===== ===== ===== ===== =====");

            String[] tokens = fromClient.split(" ");
            String parametros = tokens[1];

            String http_method = tokens[0];

            String[] tokens_parametros = parametros.split("/");

            String resource = tokens_parametros.length > 1 ? tokens_parametros[1] : "";
            String id = tokens_parametros.length > 2 ? tokens_parametros[2] : "";

            //String meta_data = tokens.length > 2 ? tokens[2] : "";
            String meta_data = "";
            if(tokens.length > 2){
                for (int i = 2; i < tokens.length; i++) {
                    id = id + " " +tokens[i];
                }
            }

            System.out.println("CONSULTA:       " + fromClient);
            System.out.println("HTTP METHOD:    " + http_method);
            System.out.println("Resource:       " + resource);
            System.out.println("ID:             " + id);
            System.out.println("META DATA:      " + meta_data);  
            
            // Determinamos la particion a acceder con una funcion hash
//            int ParticionDestino = hash(id, particiones.size());
//            System.out.println("Particion destino: " + ParticionDestino);
            
            switch (http_method) {
                case "GET":                    
                    System.out.println("Buscando en el index de '" + resource + "' el registro con id " + id);                  
                    
                    // buscar en el index la respuesta la query
                    // String result = particiones.get(particiones.size()).getEntryFromIndex(id);
                    String id_splitted[] = id.split(" ");
                    System.out.println("id mide: " + id.length());
                    ArrayList<String> input = new ArrayList();
                    
                    for (int i = 0; i < id_splitted.length; i++) {
                        input.add(id_splitted[i]);
                    }
                    
                    ArrayList<String[]> ayer = new ArrayList();
                    
                    ArrayList<String[]> respuestas = InvertedIndex.search(input, IndexStart.index_db, IndexStart.coleccion);
                    String sentence, fromServer;
                    
                    // HIT
                    if(!respuestas.isEmpty()){
                        System.out.println("Entrada en el Index encontrada, particion: "+ (particiones.size()) );
                        System.out.println("HIT");
                        
                        sentence = "POST /consulta/" + id;
                        
                        // Enviamos al cache
                        
                            //Socket para el cache (host, puerto)
                            Socket clientSocket = new Socket("localhost", IndexStart.puerto_cache) ;
                            //Buffer para enviar el dato al cache
                            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                            //Buffer para recibir dato del cache
                            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                            //Leemos del front y lo mandamos al cache
                            String respuesta = "{";
                            
                            for (int i = 0; i < respuestas.size(); i++) {
                                respuesta = respuesta + "[" + respuestas.get(i)[0] + "*" + respuestas.get(i)[1] + "]";
                                if (i != respuestas.size()-1) {
                                    respuesta = respuesta + " , ";    
                                }                                
                            }
                            respuesta = respuesta + "}";    
                            
                            
                            outToServer.writeBytes(sentence + "/" + respuesta + '\n');
                            //Recibimos del servidor
                            fromServer = inFromServer.readLine();
                            System.out.println("Cache response: " + fromServer);
                            //Cerramos el socket
                            clientSocket.close();
                        
                        
                        // Enviamos al front
                        System.out.println("enviamos a front");
                        outToClient.writeBytes(respuesta + '\n');
                    // MISS
                    }else{
                        //Enviamos miss al front
                        outToClient.writeBytes("MISS\n");
                    }                    
                    break;
                default:
                    break;
            }
            
        } catch (IOException ex) {
            Logger.getLogger(MultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }     
}
