# ARSW_2019-2_Lab-3_Blueprint-Management

## Question 1) 
To add the dependencies of Spring, I added an XML file that I called *applicationContext.xml* in which I specified the base package in which to look for components : 

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"

       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.2.xsd
          http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.2.xsd
">

    <context:component-scan base-package="edu.eci.arsw"/>

    
</beans>

```


Then, to make that the class *BlueprintsServices* gets injected by a *BlueprintPersistence* when the bean is created, I added these annotations : 

```
@Service
public class BlueprintsServices {
   
    @Autowired
    BlueprintsPersistence bpp=null;
    
    /*
    ...
    */
}
```

And : 

```
@Service
public class InMemoryBlueprintPersistence implements BlueprintsPersistence{

    private final Map<Tuple<String,String>,Blueprint> blueprints=new HashMap<>();
    
    /*
    ...
    */
}
```

I then implemented the functions *getBluePrint()* and *getBlueprintsByAuthor()* :

```
@Service
public class BlueprintsServices {
   
    @Autowired
    BlueprintsPersistence bpp=null;
    
    /*
    ...
    */
    
    /**
     * 
     * @param author blueprint's author
     * @param name blueprint's name
     * @return the blueprint of the given name created by the given author
     * @throws BlueprintNotFoundException if there is no such blueprint
     */
    
    public Blueprint getBlueprint(String author,String name) throws BlueprintNotFoundException{

        return this.bpp.getBlueprint(author, name);
    }
    
    /**
     * 
     * @param author blueprint's author
     * @return all the blueprints of the given author
     * @throws BlueprintNotFoundException if the given author doesn't exist
     */
    
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws AuthorNotFoundException{
        return this.bpp.getBlueprintsByAuthor(author);
    }
}

```

* *getBluePrint()* : 

```
public class InMemoryBlueprintPersistence implements BlueprintsPersistence{
  @Override
  public Blueprint getBlueprint(String author, String bprintname) throws BlueprintNotFoundException {
          return blueprints.get(new Tuple<>(author, bprintname));
      }
}
```

* *getBlueprintsByAuthor()* :

```
public class InMemoryBlueprintPersistence implements BlueprintsPersistence{
    @Override
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws AuthorNotFoundException {
        Set<Blueprint> setOfBlueprints = new HashSet<Blueprint>();
        
        for(Tuple<String, String> t : blueprints.keySet()){
            if(t.o1 == author){
                setOfBlueprints.add(blueprints.get(t));
            }
        }
        
        return setOfBlueprints;
        
    }
}
```

I then added two test to test if it was possible to retrieve blueprints with these functions and if these functions were working correctly :

* Testing to add a blueprint and retrieving it with *getBluePrint()* :

```
@Test
public void saveAndGetBpTest(){
    InMemoryBlueprintPersistence ibpp=new InMemoryBlueprintPersistence();

    Point[] pts=new Point[]{new Point(0, 0),new Point(10, 10)};
    Blueprint bp=new Blueprint("john", "thepaint",pts);

    try {
        ibpp.saveBlueprint(bp);
    } catch (BlueprintPersistenceException ex) {
        fail("Blueprint persistence failed inserting the first blueprint.");
    }

    try {
        Blueprint retrievedBp = ibpp.getBlueprint("john", "thepaint");
        assertEquals("Wrong author name", "john", bp.getAuthor());
        assertEquals("Wrong bpName", "thepaint", bp.getName());
    }catch (BlueprintNotFoundException ex){
        fail("Blueprint not found");
    }
}
```

* Testing to add several blueprints and retrieve all the blueprints of an author :

```
@Test
public void saveAndGetSetOfBpTest(){
  InMemoryBlueprintPersistence ibpp=new InMemoryBlueprintPersistence();

  Point[] pts=new Point[]{new Point(0, 0),new Point(10, 10)};
  Blueprint bp=new Blueprint("john", "thepaint",pts);

  Point[] pts2=new Point[]{new Point(2, 2),new Point(12, 12)};
  Blueprint bp2=new Blueprint("john", "thepaint2",pts2);

  Point[] pts3=new Point[]{new Point(2, 2),new Point(12, 12)};
  Blueprint bp3=new Blueprint("jAnother author", "thepaint2",pts3);

  try {
      ibpp.saveBlueprint(bp);
      ibpp.saveBlueprint(bp2);
      ibpp.saveBlueprint(bp3);
  } catch (BlueprintPersistenceException ex) {
      fail("Blueprint persistence failed inserting the first blueprint.");
  }

  try{
      Set<Blueprint> s = ibpp.getBlueprintsByAuthor("john");
      // Testing if the function returns the good number of BPs
      assertEquals("Wrong number of bp returned", 2, s.size());
      for(Blueprint p : s){
          // Testing if each BPs is from the right author
          assertEquals("Wrong author name", "john", p.getAuthor());
      }


  }catch(AuthorNotFoundException ex){
      fail("Author not found");
  }      
}
```

## Question 2)

For this question, I added a *mainProgam* with a *main* function that is creating and adding new blueprints to a *BlueprintService*. Then it retrieves one of the added blueprints and outputs its name and its author name :

```
public class mainProgram{

    public static void main(String[] args) throws BlueprintNotFoundException, BlueprintPersistenceException, AuthorNotFoundException{
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        BlueprintsServices bps = ac.getBean(BlueprintsServices.class);

        System.out.println("Creating new blueprints...");
        Point[] pts0=new Point[]{new Point(40, 40),new Point(15, 15), new Point(40, 40), new Point(35, 35)};
        Blueprint bp0=new Blueprint("mack", "mypaint",pts0);

        Point[] pts1=new Point[]{new Point(20, 20),new Point(10, 10)};
        Blueprint bp1=new Blueprint("etienne", "arsw",pts1);

        Point[] pts2=new Point[]{new Point(10, 10),new Point(5, 5)};
        Blueprint bp2=new Blueprint("etienne", "third",pts2);

        System.out.println("Adding new blueprints to the BlueprintService...");
        bps.addNewBlueprint(bp0);
        bps.addNewBlueprint(bp1);
        bps.addNewBlueprint(bp2);

        System.out.println("Retrieving blue print \"mypaint\" from the author \"mack\"");
        Blueprint b = bps.getBlueprint("mack", "mypaint");

        System.out.println("Name of the retrieved blueprint : " + b.getName());
        System.out.println("Author of the retrieved blueprint : " + b.getAuthor());
    }

}
```

The output : 

```
Creating new blueprints...
Adding new blueprints to the BlueprintService...
Retrieving blue print "mypaint" from the author "mack"
Name of the retrieved blueprint : mypaint
Author of the retrieved blueprint : mack
```

## Question 3)

To implement filters, I created new java interface name *BlueprintsFilter* and then I created two class, *RedundancyFilter* and *SubsamlpingFilter* that implement that interface : 

* *BlueprintsFilter* :
```
public interface BlueprintsFilter{
    /**
     * 
     * @param bp The blueprint to filter
     * @return the filtered blueprint
     */
    public Blueprint filterBlueprint(Blueprint bp);
}

```

* *RedundancyFilter* :
```
public class RedundancyFilter implements BlueprintsFilter {

    @Override
    public Blueprint filterBlueprint(Blueprint bp) {
        Blueprint filteredBlueprint;
        List<Point> filteredPointList = new ArrayList<Point>();
        
        for(Point p : bp.getPoints()){
            if(!filteredPointList.contains(p))
                filteredPointList.add(p);
        }

        Point[] array = new Point[filteredPointList.size()];

        for(int i = 0; i < filteredPointList.size(); i++)
            array[i] = filteredPointList.get(i);
        filteredBlueprint = new Blueprint(bp.getAuthor(), bp.getName(),array);

        return filteredBlueprint;

	}

}
```

* *SubsamplingFilter* :

```
@Service
public class SubsamplingFilter implements BlueprintsFilter {

    @Override
    public Blueprint filterBlueprint(Blueprint bp) {
        Blueprint filteredBlueprint;
        List<Point> filteredPointList = new ArrayList<Point>();
        boolean toFilter = false;
        for(Point p : bp.getPoints()){
            if(!toFilter)
                filteredPointList.add(p);
            toFilter = !toFilter;
        }

        Point[] array = new Point[filteredPointList.size()];

        for(int i = 0; i < filteredPointList.size(); i++)
            array[i] = filteredPointList.get(i);
        filteredBlueprint = new Blueprint(bp.getAuthor(), bp.getName(),array);

        return filteredBlueprint;
	}

}
```

I then modified the *BlueprintsService* so that it uses a filter :

```
@Service
public class BlueprintsServices {
   
    @Autowired
    BlueprintsPersistence bpp=null;

    @Autowired
    BlueprintsFilter bpf = null;
    
    public void addNewBlueprint(Blueprint bp) throws BlueprintPersistenceException{
        this.bpp.saveBlueprint(bp);
    }
    
    public Set<Blueprint> getAllBlueprints(){
        return null;
    }
    
    /**
     * 
     * @param author blueprint's author
     * @param name blueprint's name
     * @return the blueprint of the given name created by the given author
     * @throws BlueprintNotFoundException if there is no such blueprint
     */
    
    public Blueprint getBlueprint(String author,String name) throws BlueprintNotFoundException{

        return  this.bpf.filterBlueprint(this.bpp.getBlueprint(author, name));
    }
    
    /**
     * 
     * @param author blueprint's author
     * @return all the blueprints of the given author
     * @throws BlueprintNotFoundException if the given author doesn't exist
     */
    
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws AuthorNotFoundException{
        Set<Blueprint> setToReturn = this.bpp.getBlueprintsByAuthor(author);
        for(Blueprint b : setToReturn){
            b = bpf.filterBlueprint(b);
        }
        return setToReturn;
    }
    
}
```

By adding *@Service* before either the *SubsamplingFilter* class definition, or before *RedundancyFilter*, we can then chose to injected either filter, and so to use the filter that we want.


## Question 4)

I added the two tests to the corresponding filters. These tests add a blueprint with an array of points, retrieve it by filtering them, and then compare the filtered array with the array that we are supposed to get after filtering the blueprint :
* *RedundancyFilterTest* :

```
public class RedundancyFilterTest {

    @Test
    public void saveNewAndLoadFilteredTest() throws BlueprintPersistenceException, BlueprintNotFoundException{
        InMemoryBlueprintPersistence ibpp=new InMemoryBlueprintPersistence();
        RedundancyFilter rf = new RedundancyFilter();

        Point[] pts0=new Point[]{new Point(40, 40),new Point(15, 15), new Point(40, 40), new Point(40, 40), new Point(15, 15), new Point(30, 30)};
        Blueprint bp0=new Blueprint("mack", "mypaint",pts0);
        
        ibpp.saveBlueprint(bp0);

        Blueprint bpToTest = rf.filterBlueprint(ibpp.getBlueprint("mack", "mypaint"));

        assertArrayEquals("Redundant filter didn't filter well",new Object[]{new Point(40, 40),new Point(15, 15), new Point(30, 30)}, bpToTest.getPoints().toArray());  
    }
}

```

* *SubsamplingFilterTest* :

```
public class SubsamplingFilterTest{

    @Test
    public void saveNewAndLoadFilteredTest() throws BlueprintPersistenceException, BlueprintNotFoundException{
        InMemoryBlueprintPersistence ibpp=new InMemoryBlueprintPersistence();
        SubsamplingFilter sf = new SubsamplingFilter();

        Point[] pts0=new Point[]{new Point(40, 40),new Point(15, 15), new Point(40, 40), new Point(40, 40), new Point(15, 15), new Point(30, 30)};
        Blueprint bp0=new Blueprint("mack", "mypaint",pts0);
        
        ibpp.saveBlueprint(bp0);

        Blueprint bpToTest = sf.filterBlueprint(ibpp.getBlueprint("mack", "mypaint"));

        assertArrayEquals("Subsampling filter didn't filter well",new Object[]{new Point(40, 40), new Point(40, 40), new Point(15, 15)}, bpToTest.getPoints().toArray());  
    }

}
```

Now, by adding the *@Service* before either filter class definition, we can use the desired filter that will be used when retrieving blueprints. 
