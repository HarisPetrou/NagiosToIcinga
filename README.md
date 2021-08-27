# NagiosToIcinga
A migration program for hosts in Nagios to Icinga 2.

The program input is a .txt file and the output is a .txt file, too.

### How to run it:

First we rename the 'hosts.cfg' file to 'hosts.txt' and put it in the project folder.

#### Important!
The hosts must be define exactly like:

##### define host{
		attitudes & values
##### }

eg.
##### define host{
    host_name               CCNGateway
    use             generic-host
    alias               CCNGateway
    address             ##.##.##.##
    _HOST_ID                1038
    hostgroups          Servers-Prj-Icisnet,Servers-Type-Virtual
##### }

Then, in Main.java we find and replace "notifName", with a notification object that we already have.

Finally, we run the program and a .txt file is created (icinga_hosts.txt) that we can easily rename to hosts.conf and put it in our icinga service.

In case of the program finds Greek language in a Host, it's converted to English to avoid errors during compilation.

### How it works:

- Programm reads line by line the 'hosts.txt' file. 
- For every line takes nagios's command and replaces it with the corresponding command in icinga2. 
- Next, add command's value, so it's created a new line with command and its value in icinga2 code.
- Then, it checks for words or pharses in greek language and translate them in English (GreekToGreeklish class).
- Finally, the new line prints in 'icinga_hosts.txt' file.
