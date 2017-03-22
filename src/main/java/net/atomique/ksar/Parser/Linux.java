package net.atomique.ksar.Parser;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Locale;

import net.atomique.ksar.Config;
import net.atomique.ksar.GlobalOptions;
import net.atomique.ksar.OSParser;
import net.atomique.ksar.Graph.Graph;
import net.atomique.ksar.Graph.List;
import net.atomique.ksar.UI.LinuxDateFormat;
import net.atomique.ksar.XML.GraphConfig;

public class Linux extends OSParser {

    private String LinuxDateFormat;
    
    private ArrayList<String> IgnoreLinesBeginningWith;

    
    public Linux() {
		
    	LinuxDateFormat = Config.getInstance().getLinuxDateFormat();
    	IgnoreLinesBeginningWith = Config.getInstance().getIgnoreLinesBeginingWith();
	}
    


    public void parse_header(String s) {
 
        String[] columns = s.split("\\s+");
        String tmpstr;
        setOstype(columns[0]);
        setKernel(columns[1]);
        tmpstr = columns[2];
        setHostname(tmpstr.substring(1, tmpstr.length() - 1));
        checkDateFormat();
        setDate(columns[3]);

    }

    private void checkDateFormat() {

        if ("Always ask".equals(LinuxDateFormat)) {
            askDateFormat("Provide date Format");
        }

        // day and year format specifiers must be lower case, month upper case
        String[] parts= LinuxDateFormat.split(" ",3);

        dateFormat = parts[0];
        dateFormat = dateFormat.replaceAll("D{2}","dd");
        dateFormat = dateFormat.replaceAll("Y{2}","yy");

        //12hour
        if (parts.length == 3 && parts[2].contains("AM|PM")) {
            timeFormat = "hh:mm:ss a";
            timeColumn=2;
        }

    }

    private void askDateFormat(String s) {
        if ( GlobalOptions.getInstance().hasUI() ) {
            LinuxDateFormat tmp = new LinuxDateFormat(GlobalOptions.getInstance().getUI(),true);
            tmp.setTitle(s);
            if ( tmp.isOk()) {
                LinuxDateFormat=tmp.getDateFormat();
                if ( tmp.hasToRemenber() ) {
                    Config.getInstance().setLinuxDateFormat(tmp.getDateFormat());
                }
            }
        }
    }
    
    @Override
    public int parse(String line, String[] columns) {

        if (IgnoreLinesBeginningWith.contains(columns[0])) {
            currentStat = "NONE";
            return 0;
        }

        if (line.contains("LINUX RESTART")) {
            return 0;
        }

        try {
            if ( timeColumn == 2 ) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormat,Locale.getDefault());
                parsetime = LocalTime.parse(columns[0]+" "+columns[1], formatter);
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormat);
                parsetime = LocalTime.parse(columns[0],formatter);
            }

            LocalDateTime nowStat;
            if ( parsedate != null  &&  parsetime != null ){
                nowStat = LocalDateTime.of(parsedate, parsetime);
            } else {
                throw new IllegalArgumentException("date/time is missing");
            }

            if (startofgraph == null) {
                startofgraph = nowStat;
            }
            if (endofgraph == null) {
                endofgraph = nowStat;
            }
            if (nowStat.compareTo(endofgraph) > 0) {
                endofgraph = nowStat;
            }
            firstdatacolumn = timeColumn;
        } catch (DateTimeParseException|IllegalArgumentException ex) {
            System.out.println("unable to parse time " + columns[0]);
            return -1;
        }


        //00:20:01     CPU  i000/s  i001/s  i002/s  i008/s  i009/s  i010/s  i011/s  i012/s  i014/s
        if ("CPU".equals(columns[firstdatacolumn]) && line.matches(".*i([0-9]+)/s.*")) {
            currentStat = "IGNORE";
            return 1;
        }
        // XML COLUMN PARSER
        String checkStat = myosconfig.getStat(columns, firstdatacolumn);
        if (checkStat != null) {
            Object obj = ListofGraph.get(checkStat);
            if (obj == null) {
                GraphConfig mygraphinfo = myosconfig.getGraphConfig(checkStat);
                if (mygraphinfo != null) {
                    if ("unique".equals(mygraphinfo.getType())) {
                        obj = new Graph(mysar, mygraphinfo, mygraphinfo.getTitle(), line, firstdatacolumn, mysar.graphtree);

                        ListofGraph.put(checkStat, obj);
                        currentStat = checkStat;
                        return 0;
                    }
                    if ("multiple".equals(mygraphinfo.getType())) {
                        obj = new List(mysar, mygraphinfo, mygraphinfo.getTitle(), line, firstdatacolumn);

                        ListofGraph.put(checkStat, obj);
                        currentStat = checkStat;
                        return 0;
                    }
                } else {
                    // no graph associate
                    currentStat = checkStat;
                    return 0;
                }
            } else {
                currentStat = checkStat;
                return 0;
            }
        }

        //System.out.println( currentStat +" " + line);



        if (lastStat != null) {
            if (!lastStat.equals(currentStat) ) {
                if (  GlobalOptions.getInstance().isDodebug())  {
                System.out.println("Stat change from " + lastStat + " to " + currentStat);
                }
                lastStat = currentStat;
            }
        } else {
            lastStat = currentStat;
        }
        
        if ("IGNORE".equals(currentStat)) {
            return 1;
        }
        if ("NONE".equals(currentStat)) {
            return -1;
        }

        currentStatObj = ListofGraph.get(currentStat);
        if (currentStatObj == null) {
            return -1;
        } else {

            LocalDateTime nowStat = LocalDateTime.of(parsedate, parsetime);

            DateSamples.add(nowStat);

            if (currentStatObj instanceof Graph) {
                Graph ag = (Graph) currentStatObj;
                return ag.parse_line(nowStat, line);
            }
            if (currentStatObj instanceof List) {
                List ag = (List) currentStatObj;
                return ag.parse_line(nowStat, line);
            }
        }
        return -1;
    }

}
