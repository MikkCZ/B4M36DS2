#!/bin/bash

# insert online registered visitors "name,password,registered,city"
curl -X PUT -H 'Content-Type: text/csv' -d 'Michal Stanke,pass,1451365554,Praha' http://localhost:10011/buckets/stankmic_visitors/keys/stankmic -H 'Link: </buckets/stankmic_tickets/keys/8576de21-8181-4616-a42b-556e650c80ca>; riaktag="ticket"'
curl -X PUT -H 'Content-Type: text/csv' -d 'John Doe,heslo,1451665554,Mexico City' http://localhost:10011/buckets/stankmic_visitors/keys/jdoe
curl -X PUT -H 'Content-Type: text/csv' -d 'Alice,12345,1451665554,Praha' http://localhost:10011/buckets/stankmic_visitors/keys/aluska
curl -X PUT -H 'Content-Type: text/csv' -d 'Bob,bobicek,1451665554,Mexico City' http://localhost:10011/buckets/stankmic_visitors/keys/bobby
curl -X PUT -H 'Content-Type: text/csv' -d 'Bilbo Baggins,theRING,1451665554,Hobbiton' http://localhost:10011/buckets/stankmic_visitors/keys/mrbaggins -H 'Link: </buckets/stankmic_tickets/keys/d5129736-b08a-4bd0-b1b3-1386018cb6de>; riaktag="ticket"'
curl -X PUT -H 'Content-Type: text/csv' -d 'Samwise Gamgee,mordor,1451665554,Hobbiton' http://localhost:10011/buckets/stankmic_visitors/keys/sam
curl -X PUT -H 'Content-Type: text/csv' -d 'Peregrin Took,matrix,1451665554,Shire' http://localhost:10011/buckets/stankmic_visitors/keys/thechosenone
echo "=== Visitors registered. ==="

# insert parks "name,city,country,opened"
curl -X PUT -H 'Content-Type: text/csv' -d 'Legoland Billund Resort,Billund,Denmark,1968' http://localhost:10011/buckets/stankmic_parks/keys/billund
curl -X PUT -H 'Content-Type: text/csv' -d 'Legoland Windsor Resort,Windsor,United Kingdom,1996' http://localhost:10011/buckets/stankmic_parks/keys/windsor
curl -X PUT -H 'Content-Type: text/csv' -d 'Legoland California,Carlsbad,United States,1999' http://localhost:10011/buckets/stankmic_parks/keys/california
curl -X PUT -H 'Content-Type: text/csv' -d 'Legoland Deutschland Resort,Guenzburg,Germany,2002' http://localhost:10011/buckets/stankmic_parks/keys/guenzburg -H 'Link: </buckets/stankmic_tickets/keys/8576de21-8181-4616-a42b-556e650c80ca>; riaktag="ticket"' -H 'Link: </buckets/stankmic_tickets/keys/d5129736-b08a-4bd0-b1b3-1386018cb6de>; riaktag="ticket"'
curl -X PUT -H 'Content-Type: text/csv' -d 'Legoland Florida Resort,Winter Haven,United States,2011' http://localhost:10011/buckets/stankmic_parks/keys/florida
curl -X PUT -H 'Content-Type: text/csv' -d 'Legoland Malaysia Resort,Iskandar Puteri,Malaysia,2012' http://localhost:10011/buckets/stankmic_parks/keys/malaysia
echo "=== Parks build. ==="

# insert bought tickets "valid_date,persons,used,price,sold_date"
curl -X PUT -H 'Content-Type: text/csv' -d '1451606400,2,false,100,1451365554' http://localhost:10011/buckets/stankmic_tickets/keys/8576de21-8181-4616-a42b-556e650c80ca -H 'Link: </buckets/stankmic_visitors/keys/stankmic>; riaktag="owner"' -H 'Link: </buckets/stankmic_parks/keys/guenzburg>; riaktag="for-park"'
curl -X PUT -H 'Content-Type: text/csv' -d '1454284800,20,false,850,1448755200' http://localhost:10011/buckets/stankmic_tickets/keys/273f07c9-295f-4077-911d-f6d4dd9f0ce4
curl -X PUT -H 'Content-Type: text/csv' -d '1456790400,4,true,200,1451347200' http://localhost:10011/buckets/stankmic_tickets/keys/be6c273d-345a-4ea8-a640-7b71cc553311
curl -X PUT -H 'Content-Type: text/csv' -d '1459468800,1,false,60,1449446400' http://localhost:10011/buckets/stankmic_tickets/keys/d5129736-b08a-4bd0-b1b3-1386018cb6de -H 'Link: </buckets/stankmic_visitors/keys/mrbaggins>; riaktag="owner"' -H 'Link: </buckets/stankmic_parks/keys/guenzburg>; riaktag="for-park"'
curl -X PUT -H 'Content-Type: text/csv' -d '1451692800,5,true,450,1451365554' http://localhost:10011/buckets/stankmic_tickets/keys/2b40aae0-2fc6-4441-af63-0430ff529105
curl -X PUT -H 'Content-Type: text/csv' -d '1451606400,6,false,510,1449446400' http://localhost:10011/buckets/stankmic_tickets/keys/989786f0-5065-4401-84b3-0085b890bf79
echo "=== Tickets sold. ==="

# check buckets are filled with something
#echo "Buckets filled:"
#curl -X GET http://localhost:10011/buckets?buckets=true

# read myself
echo "=== This is me: ==="
curl -X GET http://localhost:10011/buckets/stankmic_visitors/keys/stankmic
echo

# mark ticket as used
old_ticket_info=$(curl -s -X GET http://localhost:10011/buckets/stankmic_tickets/keys/989786f0-5065-4401-84b3-0085b890bf79)
echo "=== Ticket before: ==="
echo "$old_ticket_info"
new_ticket_info=$(echo "$old_ticket_info" | sed 's/false/true/g')
curl -X PUT -H 'Content-Type: text/csv' -d "$new_ticket_info" http://localhost:10011/buckets/stankmic_tickets/keys/989786f0-5065-4401-84b3-0085b890bf79
echo "=== Ticket after: ==="
curl -X GET http://localhost:10011/buckets/stankmic_tickets/keys/989786f0-5065-4401-84b3-0085b890bf79
echo

# delete the group ticket
curl -X DELETE http://localhost:10011/buckets/stankmic_tickets/keys/273f07c9-295f-4077-911d-f6d4dd9f0ce4
echo "=== The group ticket cancelled and removed. ==="

# list all my tickets
echo "=== List my tickets: ==="
curl -X GET http://localhost:10011/buckets/stankmic_visitors/keys/stankmic/stankmic_tickets,ticket,1

# list all visitor, who have bought a ticket for Guenzburg Legoland
echo "=== Who have bought a ticket into Guenzburg Legoland: ==="
curl -X GET http://localhost:10011/buckets/stankmic_parks/keys/guenzburg/stankmic_tickets,ticket,0/stankmic_visitors,owner,1

