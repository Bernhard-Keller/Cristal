
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bernhard
 */
public class Group {
    String name;
    int number;
    Subgroup[] subgroups;
    Motive symchart;
    
public void read(String groupname, int subgroupnumber){
    
    name=groupname;
    subgroups=new Subgroup[subgroupnumber];
    
    ClassLoader cLoader=Crystal.class.getClassLoader();
    URL url = cLoader.getResource("groups/"+groupname);
       //System.out.println("Value = " + url.getFile());
       try {
            BufferedReader in = new BufferedReader(new FileReader(url.getFile()));
            //BufferedReader in=new BufferedReader("groups/"+groupname);
            for (int i=0; i<subgroupnumber; i++){
                subgroups[i]=new Subgroup();
                subgroups[i].read(in);
                subgroups[i].setNumber(i);
                subgroups[i].setGroup(this);
            }
            in.close();
            }
       catch (Exception e)
            {
                System.err.format("Exception occurred trying to read '%s'.", url.getFile());
                e.printStackTrace();
            }
}
    
}
