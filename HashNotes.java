package hashnotes;

import java.util.Calendar;
import java.util.Date;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

/**
 * @author Pragadheesh
 */


public class HashNotes extends MIDlet implements CommandListener 
{
    final static String DB = "note";
    final static String DBtopic = "hashtopics";
    private Form fsearch,finput,flist_topics,fdelete,flist_delete;
    private Display d; String ht,n;
    private TextField search_hash_topic,hash_topic,notes,del;
    private Command about,save,exit,search,searchpage,back,enter,list_topics,deletepage,delete,okdel,canceldel,list_delete;
    private RecordStore rs=null,rstopic=null;
    
    public HashNotes()
    {
        search_hash_topic = new TextField("Hash Topic : ","#",30,TextField.ANY);
        hash_topic = new TextField("Hash Topic : ","#",30,TextField.ANY);
        del = new TextField("Delete TopicID : ","",30,TextField.NUMERIC);
        
        notes = new TextField("Notes : ","",20000,TextField.ANY);
        save=new Command("Save",Command.OK,1);
        list_topics=new Command("List #Topics",Command.OK,2);
        list_delete=new Command("List #Deleted",Command.OK,2);
        exit=new Command("Exit",Command.EXIT,1);
        searchpage=new Command("Search Notes",Command.OK,3);
        search=new Command("Search",Command.OK,2);
        back=new Command("Back",Command.CANCEL,1);
        enter=new Command("Enter",Command.OK,1);
        deletepage=new Command("Delete Page",Command.OK,4);
        delete = new Command("Delete",Command.OK,5);
        about = new Command("About",Command.OK,10);
        
        
    }
    
    public void deletenotes()//for deletepage
    {
        d.setCurrent(fdelete);
    }
    
    
    
    public void ndelete() throws RecordStoreException   //delete confirmation - if pressed no!
    {
        String data; 
        fdelete.deleteAll();
        fdelete.append(del);
        Alert msgalert; byte [] msg;
        int del_id=Integer.parseInt(del.getString());
        if(del_id>rs.getNumRecords())
        {
            fdelete.append("\nNo Such ID exists");
        }
        else
        {
            msg=rs.getRecord(del_id);
            data=new String(msg);
              if("@#Deleted#@".equals(data.substring(0,11)))
            {
                fdelete.append("\nNo Such ID exists");
            }
            else
            {
            msgalert = new Alert("Permanently Delete?", data, null, AlertType.CONFIRMATION);
            okdel = new Command("Yes", Command.OK, 2);
            msgalert.addCommand(okdel);
            canceldel = new Command("No", Command.CANCEL, 1);
            msgalert.addCommand(canceldel);

            msgalert.setCommandListener(this);
            d.setCurrent(msgalert);
            }
        }

    }
    
    public void yesdelete() throws RecordStoreException //confirm deletion
    {
        String data; 
        int del_id=Integer.parseInt(del.getString());
        byte [] msg = rs.getRecord(del_id);
        data=new String(msg);
        
        Calendar c= Calendar.getInstance();
        Date cd; String time,am_pm,min;
        cd=new Date();
        c.setTime(cd);
        if(c.get(Calendar.AM_PM)==1) {
            am_pm="PM";
        }
        else {
            am_pm="AM";
        }
        
        if(c.get(Calendar.MINUTE)<10) {
            min="0"+c.get(Calendar.MINUTE);
        }
        else {
            min=new Integer(c.get(Calendar.MINUTE)).toString();
        }
        
        time = "\n\nDeleted on :\n" + c.get(Calendar.DATE)+"/"+ (c.get(Calendar.MONTH) + 1) +"/"+c.get(Calendar.YEAR) + "\n" + c.get(Calendar.HOUR) + ":" + min + ":" + c.get(Calendar.SECOND) + am_pm;
        
        data = "@#Deleted#@" + " " + data + time;
        byte[] msgupd = data.getBytes();
        rs.setRecord(del_id,msgupd,0,data.length() );
        d.setCurrent(fsearch);
    }
    
    
    public void add()   //saves notes
    {
        Calendar c= Calendar.getInstance(); //to add time while the note is being saved
        if( (!(notes.getString().equals(""))) && (!(notes.getString().equals(" "))) && (!(hash_topic.getString().equals("#"))) && (!(hash_topic.getString().equals(""))) )  //checks if the text entered is not empty
        {
            Date cd; String time,am_pm,min;
            cd=new Date();
            c.setTime(cd);
            if(c.get(Calendar.AM_PM)==1) 
            {
                am_pm="PM";
            }
            else 
            {
                am_pm="AM";
            }
            if(c.get(Calendar.MINUTE)<10) {
                min="0"+c.get(Calendar.MINUTE);
            }
            else {
                min=new Integer(c.get(Calendar.MINUTE)).toString();
            }
            time = "\n\nAdded on :\n" + c.get(Calendar.DATE)+"/"+ (c.get(Calendar.MONTH) + 1) +"/"+c.get(Calendar.YEAR) + "\n" + c.get(Calendar.HOUR) + ":" + min + ":" + c.get(Calendar.SECOND) + " " +am_pm;
          try 
          {
            String data;
            String topic_add = hash_topic.getString();
            String tbmod = notes.getString();
            tbmod += " "+topic_add;
            tbmod += time;
            byte[] rtopic=topic_add.getBytes();
            int cnt=0;
            if(topic_add!=null) //adds topics to the "TOPICS DB" only if a new #topic is mentioned
            {
                RecordEnumeration ts = rstopic.enumerateRecords(null,null,false);
                while(ts.hasNextElement())
                {
                    byte [] msg = ts.nextRecord();
                    data = new String(msg);
                    if(data.equals(topic_add))
                    {
                       cnt++;
                       break;
                    }
                }
                if(cnt<=0)
                {
                    rstopic.addRecord(rtopic,0,rtopic.length);
                }
            }
            byte[] bnotes=tbmod.getBytes();
            if(notes.getString()!=null) {
                rs.addRecord(bnotes,0,bnotes.length);
            }
            notes.setString(null);
         } 
        catch (RecordStoreException ex) 
        {
        }
       }
    }
    
    public void moveback()  //Move to the main save notes page
    {
        d.setCurrent(finput);
    }
    
    public void movetomainpage()    //Move to the main save notes page
    {
        d.setCurrent(finput);
    }
    
    public void searchnotes() throws RecordStoreNotOpenException, RecordStoreException
    {
        fsearch.deleteAll();
        fsearch.append(search_hash_topic);
        d.setCurrent(fsearch);
    }
    
    
    
    
    public void search() throws RecordStoreNotOpenException, RecordStoreException   //Searches for notes based on 'hash topic' or based on the saved notes
    {
        String topic=search_hash_topic.getString(); String data;
        fsearch.deleteAll();
        fsearch.append(search_hash_topic);
        
        int cnt=0,total;
        RecordEnumeration s = rs.enumerateRecords(null,null,false);
        total=s.numRecords();
        byte [] temp = rs.getRecord(total); //setting the pointer to the last Record
        System.out.println("total " + total);
        fsearch.append("\n\n");
        
        //Listing records from last (Recent Record onwards)
        while(s.hasPreviousElement())
        {
            int rid = s.previousRecordId();
            System.out.println("rid "+rid);
            byte [] msg = rs.getRecord(rid);
            data = new String(msg);
            
            if(data.indexOf(topic)>=0 && data.indexOf("@#Deleted#@")<0) {
                cnt++;
                fsearch.append(rid+". "+data+"\n"+"-----------------------------------"+"\n");
            }
        }
        if(cnt<=0)
        {
            fsearch.append("Topic not currently #-ed! It might have been #-ed earlier and deleted!");
        }
        d.setCurrent(fsearch);
    }
    
    void listtopics() throws RecordStoreNotOpenException, RecordStoreException  //Lists all #-ed Topics
    {
        flist_topics.deleteAll();
        flist_topics.append("Topics\n\n");
        String data;
        RecordEnumeration ts = rstopic.enumerateRecords(null,null,false);
        while(ts.hasNextElement())
        {
                    byte [] msg = ts.nextRecord();
                    data = new String(msg);
                    flist_topics.append(data+"\n");
        }
        d.setCurrent(flist_topics);
    }
    
    void listdelete () throws RecordStoreNotOpenException, RecordStoreException //List all deleted notes
    {
        flist_delete.deleteAll();
        String data;
        RecordEnumeration s = rs.enumerateRecords(null,null,false);
        byte [] temp = rs.getRecord(s.numRecords());
        while(s.hasPreviousElement())
        {
                    int rid = s.previousRecordId();
                    byte [] msg = rs.getRecord(rid);
                    data = new String(msg);
                    if(data.indexOf("@#Deleted#@")>=0) 
                    {
                        flist_delete.append(data+"-----------------------------------\n");
                    }
        }
        d.setCurrent(flist_delete);
    }
        
    
    
    
    public void startApp() {
        
        
        Canvas homepage = new Intro();
        homepage.addCommand(enter);
        homepage.addCommand(exit);
        homepage.setCommandListener(this);
        d=Display.getDisplay(this);
        d.setCurrent(homepage);
        
        
        try {
            rs = RecordStore.openRecordStore(DB,true);
            rstopic = RecordStore.openRecordStore(DBtopic,true);
        } catch (RecordStoreException ex) {
        }
        
        finput = new Form("Get #-ed : SPC's Hash your notes");
        finput.append(hash_topic);
        finput.append(notes);
        finput.addCommand(save);
        finput.addCommand(searchpage);
        finput.addCommand(exit);
        finput.addCommand(list_topics);
        finput.addCommand(list_delete);
        finput.addCommand(deletepage);
        finput.addCommand(about);
        finput.setCommandListener(this);
        //d.setCurrent(finput);
        
        fsearch=new Form("Search #-ed Notes");
        fsearch.append(search_hash_topic);
        fsearch.addCommand(back);
        fsearch.addCommand(search);
        fsearch.addCommand(list_topics);
        fsearch.addCommand(deletepage);
        fsearch.addCommand(about);
        fsearch.setCommandListener(this);
        
        flist_topics = new Form("List #Topics @ #-ed Notes");
        flist_topics.addCommand(searchpage);
        flist_topics.addCommand(back);
        flist_topics.addCommand(exit);
        flist_topics.addCommand(about);
        flist_topics.setCommandListener(this);
        
        flist_delete = new Form("#Deleted @ #-ed Notes");
        flist_delete.addCommand(back);
        flist_delete.addCommand(search);
        flist_delete.setCommandListener(this);
        
        fdelete = new Form("Delete Notes: #-ed Notes");
        fdelete.append(del);
        fdelete.addCommand(searchpage);
        fdelete.addCommand(back);
        fdelete.addCommand(delete);
        fdelete.setCommandListener(this);
                
        
       
    }
    
    public void pauseApp() {
    }
    
    public void destroyApp(boolean unconditional) {
         notifyDestroyed();
    }

    public void commandAction(Command c, Displayable dis) {
        String label=c.getLabel();
        if(label.equals("Save")) 
        {
            add();
        }
        else if(label.equals("Search Notes"))
        {
            try {
                searchnotes();
            } catch (RecordStoreNotOpenException ex) {
            } catch (RecordStoreException ex) {
            }
        }
        else if(label.equals("Search"))
        {
            try {
                search();
            } catch (RecordStoreNotOpenException ex) {
            } catch (RecordStoreException ex) {
            }
        }
        else if(label.equals("Enter"))
        {
            movetomainpage();
        }
        
        else if(label.equals("Back"))
        {
            moveback();
        }
        else if(label.equals("Exit"))
        {
            destroyApp(true);
        }
        else if(label.equals("List #Topics"))
        {
            try {
                listtopics();
            } catch (RecordStoreNotOpenException ex) {
            } catch (RecordStoreException ex) {
            }
        }
        else if(label.equals("Delete Page"))
        {
            deletenotes();
        }
        else if(label.equals("Delete"))
        {
            try {
                ndelete();
            } catch (RecordStoreException ex) {
            }
        }
        else if(label.equals("Yes"))
        {
            try {            
                yesdelete();
            } catch (RecordStoreException ex) {
            }
             
        }
        else if(label.equals("List #Deleted"))
        {
            try {            
                listdelete();
            } catch (RecordStoreException ex) {
            }
             
        }
        else if(label.equals("No"))
        {
            deletenotes();
        }
        else if(label.equals("About"))
        {
            Canvas finish = new About();
            finish.addCommand(back);
            finish.addCommand(exit);
            finish.setCommandListener(this);
            d=Display.getDisplay(this);
            d.setCurrent(finish);
        }
    }  
        
   }

class Intro extends Canvas      //Introduction Page
{
    public void paint(Graphics g)
    {
        
        g.setColor(0x6666FF);
        g.fillRect(0,0,getWidth(),getHeight());
        g.setColor(0xFFFFFF);
        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
        g.drawString("#-ed Notes", getWidth() / 2,getHeight() / 2, Graphics.BASELINE | Graphics.HCENTER);
        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_ITALIC, Font.SIZE_SMALL));
        g.drawString("v1.0", getWidth() / 2,getHeight() / 2 + 20, Graphics.BASELINE | Graphics.HCENTER);
    }
}


class About extends Canvas  //About Page
{
    public void paint(Graphics g)
    {
        
        g.setColor(0x6666FF);
        g.fillRect(0,0,getWidth(),getHeight());
        g.setColor(0xFFFFFF);
      
        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_ITALIC, Font.SIZE_SMALL));
        g.drawString("Developed By", getWidth() / 2,getHeight() / 2 - 20, Graphics.BASELINE | Graphics.HCENTER);
        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
        g.drawString("S.Pragadheesh Chander", getWidth() / 2,getHeight() / 2, Graphics.BASELINE | Graphics.HCENTER);
    }
}
    


