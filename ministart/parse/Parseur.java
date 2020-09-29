package x.ministart.parse;

import java.io.FileNotFoundException;
import static java.lang.Character.isLetter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.FileInputStream;

public class Parseur {

    public static void main(String[] args) throws FileNotFoundException {    
        Scanner buffer = new Scanner(new FileInputStream("test.txt"), "Cp1252"); //Lecture du fichier test, Cp1252 c'est pour windows...
        Scanner dico = new Scanner(new FileInputStream("liste.de.mots.francais.frgut.txt"), "Cp1252"); //Liste des mots d'un dictionnaire de la langue Française
        /*Les différentes listes utilisées, texte == les noms des personnages, dictionnaire == les mots du dictionnaire et banned == les mots qu'on n'ajoute pas à texte (ex : Dans Père Noël, on ajoute n'y Père n'y Noël).*/
        List<String> texte = new ArrayList<>();
        List<String> dictionnaire = new ArrayList<>();
        List<String> banned = new ArrayList<>();
        /*Des "interrupteurs" pour savoir si on vient de rencontrer un signe de ponctuation, une majuscule, etc...*/
        int i,ponctuation,maj;
        String tmp;
        /*On remplit la liste dico*/
        while (dico.hasNext())
            dictionnaire.add(dico.next());
        dico.close(); //on ferme le fichier dico
        /*Le parseur ! Il lit ligne par ligne et mot par mot dans la ligne. Il ajoute les noms des personnages dans la liste texte.*/
        while (buffer.hasNextLine()) { //Tant qu'il y a une ligne à lire 
            String line = buffer.nextLine(); //On la stocke
            if (line.isEmpty()) //Si elle est vide, on cherche la suivante
                continue;
            i = ponctuation = maj = 0 ;// initialisation des variables
            String[] tokens = line.split("\\s"); // on sépare la ligne en mot
label : 
            /*On parse les mots à la recherche de nom*/
           for (String mot : tokens) {
                if (mot.isEmpty()) //si le mot et vide on en prend le suivant
                    continue;
                i++; //i sert à calculer l'écart entre le début de la phrase et le mot courant.
                if(i - ponctuation  == 2) //vérifie l'écart entre le mot courant et la position du dernier signe de ponctuation rencontré.
                    ponctuation = 0;
                if(!isLetter(mot.charAt(0)) && mot.charAt(0) != ',') //on vient de recontrer une signe de ponctuation qui n'est pas une virgule (pour les majuscules), on stocke sa position dans la ligne.
                    ponctuation = i;
                /*On décompose le mot jusqu'à que son dernier caractère ne soit pas un signe de ponctuation (ex : "bonjour," -> "bonjour") */
                while(mot.length()> 1 && !isLetter(mot.charAt(mot.length()-1))){
                    if(mot.charAt(mot.length()-1) != ',')
                        ponctuation = i;
                    mot = mot.substring(0, mot.length()-1);
                }
                /*Un nom est toujours en majuscule, pas la peine de le regarder sinon, de même si sa taille est de 1 ou que le mot est dans la liste banned*/
                if(Character.isUpperCase(mot.charAt(0)) == false || mot.length() == 1 || banned.contains(mot) == true){
                    maj = 0;
                    continue;
                }
                maj++; // le nombre de majuscules consécutives
                if(mot.length()> 1 && mot.contains("'") == true)//on décompose les mots composés avec une ' en 2 mots distincts
                    mot = mot.split("'")[1];
                if(mot.length()> 1 && mot.contains("-") == true)////on décompose les mots composés avec un - en 2 mots distincts
                    mot = mot.split("-")[1];
                if(i > 1 && ponctuation == 0 && maj > 1 && texte.size() > 0){
                    tmp = texte.get(texte.size()-1);
                    if(texte.contains(tmp.concat(" " + mot)) == false){   
                        banned.add(tmp);
                        texte.set(texte.size()-1, tmp.concat(" " + mot));
                    }
                    continue;
                }
                if(dictionnaire.contains(mot.toLowerCase()) == false && texte.contains(mot) == false)
                   texte.add(mot);     
                if(dictionnaire.contains(mot.toLowerCase()) == true && texte.contains(mot) == false && i > 1 && ponctuation == 0){
                   for(String ban : banned){
                        if(texte.contains(ban.concat(" " + mot)) == true)
                            continue label;
                   }
                    texte.add(mot);  
               }
            }    
        }
        buffer.close();
        for(i=0;i<texte.size();i++)
           System.out.println(texte.get(i));  
    }
}
