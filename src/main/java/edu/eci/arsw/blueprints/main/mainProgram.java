package edu.eci.arsw.blueprints.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.AuthorNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.persistence.impl.InMemoryBlueprintPersistence;
import edu.eci.arsw.blueprints.services.BlueprintsServices;

public class mainProgram{

    public static void main(String[] args) throws BlueprintNotFoundException, BlueprintPersistenceException, AuthorNotFoundException{
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        BlueprintsServices bps = ac.getBean(BlueprintsServices.class);

        Point[] pts0=new Point[]{new Point(40, 40),new Point(15, 15)};
        Blueprint bp0=new Blueprint("mack", "mypaint",pts0);

        Point[] pts1=new Point[]{new Point(20, 20),new Point(10, 10)};
        Blueprint bp1=new Blueprint("etienne", "arsw",pts1);

        Point[] pts2=new Point[]{new Point(10, 10),new Point(5, 5)};
        Blueprint bp2=new Blueprint("etienne", "third",pts2);

        bps.addNewBlueprint(bp0);
        bps.addNewBlueprint(bp1);
        bps.addNewBlueprint(bp2);

        Blueprint b = bps.getBlueprint("mack", "mypaint");

        System.out.println("First Blueprint Name : " + b.getName());
        System.out.println("First Blueprint Author : " + b.getAuthor());


        System.out.println("Number of blueprints from the author \"etienne\" : " + bps.getBlueprintsByAuthor("etienne").size());
 
        
        

    }

}