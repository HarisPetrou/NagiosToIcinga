package com.company;
import java.io.*;
import java.lang.String;
import java.io.FileWriter;
import java.util.ArrayList;

public class Main {

    //This function creates an array from line's different words.
    //Every word is a different array position.
    public static ArrayList<String> createArray (String line){
        ArrayList<String> str = new ArrayList<>();
        //words separate with spaces in a line
        for (String s : line.split(" ")) {
            str.add(s);
        }
        return str;
    }



    //This function takes a command in nagios and replaces it with the
    //corresponding command in icinga2
    public static String objectTypeMatching (ArrayList<String> str ){
        String icinga2_name = str.get(0);

        switch (str.get(0)) {
            case "use":
                icinga2_name = "import ";
                break;
            case "alias":
                icinga2_name = "display_name ";
                break;
            case "hostgroups":
                icinga2_name = "groups ";
                break;
            case "active_checks_enabled":
                icinga2_name = "enable_active_checks ";
                break;
            case "passive_checks_enabled":
                icinga2_name = "enable_passive_checks ";
                break;
            case "event_handler_enabled":
                icinga2_name = "enable_event_handler ";
                break;
            case "low_flap_threshold":
            case "high_flap_threshold":
                icinga2_name = "flapping_threshold ";
                break;
            case "flap_detection_enabled":
                icinga2_name = "enable_flapping ";
                break;
            case "process_perf_data":
                icinga2_name = "enable_perfdata ";
                break;
            case "notifications_enabled":
                icinga2_name = "enable_notifications ";
                break;
            case "is_volatile":
                icinga2_name = "volatile ";
                break;
            case "normal_check_interval":
            case "check_interval":
                icinga2_name = "check_interval ";
                break;
            case "retry_check_interval":
            case "retry_interval":
                icinga2_name = "retry_interval ";
                break;
            case "contacts":
                icinga2_name = "vars.notification['notifName'] = {users = [";
                break;
            case "contact_groups":
                icinga2_name = "vars.notification['notifName'] = {groups = [";
                break;
            //In these cases the command has the same name in icinga2
            case "notes":
            case "icon_image_alt":
            case "max_check_attempts":
            case "address":
            case "address6":
            case "{":
            case "}":
                break;
            default:
                //for custom variables
                if (str.get(0).startsWith("_")) {
                    icinga2_name = "vars." +str.get(0).substring(1);
                }
                //for commands without matching in icinga2
                else {
                    icinga2_name = null;
                }
        }
        return icinga2_name;
    }

    //This function takes command's value and
    //replace nagios command's 0 or 1 with true or false for some specific commands
    public static String objectValueMatching (ArrayList<String> str ){
        String value = null;
        String nagios_command = str.get(0);
        str.remove(0);
        value = str.toString();
        //remove value's [ ] , due to str.toString (str is an arraylist)
        value = value.substring(1, value.length()-1);
        //if nagios_command is one from the following, replace 0 or 1 with true or false and return it
        if(nagios_command.equals("active_checks_enabled") || nagios_command.equals("passive_checks_enabled")
                || nagios_command.equals("process_perf_data") || nagios_command.equals("notifications_enabled")
                || nagios_command.equals("is_volatile")){
            if (value.equals("0"))
                value = "false";
            else
                value = "true";
        }
        return value;
    }

    //This function checks if a command's value is numeric
    //We need this to remove " " from numeric values
    public static Boolean checkIfIsNumeric (String str){
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public static void main(String[] args) {
        try{
            FileInputStream fstream = new FileInputStream("hosts.txt");
            PrintWriter fileWriter = new PrintWriter(new FileWriter("icinga_hosts.txt"));
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine, definition ="", host_name;
            while((strLine=br.readLine())!=null)
            {
                String newLine = strLine;
                //remove many spaces from line, it helps us for createArray function (later)
                strLine = strLine.trim().replaceAll("\\s{2,}", " ");
                //if line starts with ;, don't care it's a comment
                if (strLine.startsWith(";")) {
                    continue;
                }
                //if line contains "define host", it defines a new host
                if (strLine.contains("define host")){
                    definition = "object Host ";
                    continue;
                }
                //host_name in nagios is a separate command and it's not after define host (in the same line), so
                //if next line contains host_name, take it and write it to file in the same line with object host
                if (strLine.contains("host_name")){
                    host_name = strLine.contains(" ") ? strLine.substring(strLine.indexOf(' ')).trim() : "";
                    newLine = definition + '"'+ host_name +'"' +" {";
                    fileWriter.println(newLine);
                    continue;
                }
                //create an array from line's different words
                ArrayList<String> str = createArray(strLine);
                //if array is empty, go to next iteration
                if (str.isEmpty()) {
                    continue;
                } else {
                    //commandsPart represents icinga's command name
                    //valuesPart represents command's value
                    String commandsPart = objectTypeMatching(str);
                    String valuesPart = objectValueMatching(str);
                    if (commandsPart!=null && valuesPart!=null && !valuesPart.isEmpty()){
                        //if command's name is display name, checks command's value if it's in English otherwise,
                        //convert it (GreekToGreeklish class)
                        if(commandsPart.equals("display_name ")){
                            valuesPart = GreekToGreeklish.convert(valuesPart);
                            valuesPart = valuesPart.replaceAll(",","");
                        }
                        valuesPart = valuesPart.replaceAll(",", "\",\"");
                        //if icinga command (=commandsPart) defines notification or host's group,
                        // put additional brackets etc.
                        if(commandsPart.contains("vars.notification['notifName']")){
                            newLine = commandsPart + '"'+valuesPart+'"'+" ]}";
                        }else if (commandsPart.contains("groups")){
                            newLine = commandsPart +" = "+ "[\"" +valuesPart+'"'+"]";
                        }else{
                            //create the new line from icinga command (commandsPart) and it's value (valuesPart)
                            newLine = commandsPart + " = " + '"'+valuesPart+'"';
                            //check if command's value is numeric,to remove ""
                            if(checkIfIsNumeric(valuesPart) || valuesPart.equals("true")|| valuesPart.equals("false"))
                                newLine = newLine.replaceAll("\"","");
                        }
                        //if icinga command is import, remove = and if contains comma, remove it too
                        if(commandsPart.contains("import")){
                            if (newLine.contains(","))
                                newLine = newLine.substring(0, newLine.indexOf(","));
                            newLine = newLine.replace("=","");
                        }
                    }
                    //this else helps us to print the brackets to the new file
                    else {
                        newLine = commandsPart;
                    }
                }
                //if new line is not an empty line, write it to the new file
                if (newLine!=null) {
                    fileWriter.println(newLine);
                }
            }
            in.close();
            fileWriter.close();
        }catch (Exception e){

            e.printStackTrace();
        }
    }
}
