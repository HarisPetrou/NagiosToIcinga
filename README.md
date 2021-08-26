# NagiosToIcinga
A migration program for hosts in Nagios to Icinga 2.

The program input is a .txt file and the output is a .txt file, too.

First we rename the 'hosts.cfg' file to 'hosts.txt' and put it in the project folder.

### Important!
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

