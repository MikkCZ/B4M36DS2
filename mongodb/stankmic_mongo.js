db.dropDatabase()

db.parks.save({ _id:"billund", name:"Legoland Billund Resort", country:"Denmark" })
db.parks.save({ _id:"windsor", name:"Legoland Windsor Resort", country:"United Kingdom" })
db.parks.save({ _id:"carlsbad", name:"Legoland California", country:"United States" })
db.parks.save({ _id:"guenzburg", name:"Legoland Deutschland Resort", country:"Germany" })
db.parks.save({ _id:"winterhaven", name:"Legoland Florida Resort", country:"United States" })
db.parks.save({ _id:"iskadarputeri", name:"Legoland Malaysia Resort", country:"Malaysia" })
db.parks.save({ _id:"baylake", name:"Walt Disney World Resort", country:"United States" })

db.amusements.save({ _id:"pirates", name:"Pirate ship", parks:["baylake", "iskadarputeri", "guenzburg"] })
db.amusements.save({ _id:"pharaon", name:"Pharaon ride", parks:["winterhaven"] })
db.amusements.save({ _id:"cinema3D", name:"3D kino", parks:["carlsbad", "guenzburg"] })
db.amusements.save({ _id:"cinema2D", name:"2D kino", parks:["carlsbad"] })
db.amusements.save({ _id:"dragon-railway", name:"Dragon railway", parks:["billund", "windsor"] })

db.visitors.save({ _id:"stankmic", name:"Michal Stanke", country:"Czech Republic", tickets:[{ park:"guenzburg", valid:false, persons:3 }] })
db.visitors.save({ _id:"jdoe", name:"John Doe", country:"Mexico", tickets:[{ park:"baylake", valid:true, persons:4 }, { park:"carlsbad", valid:false, persons:2 }, { park:"carlsbad", valid:true, persons:4 }] })
db.visitors.save({ _id:"bobicek", name:"Bob", country:"Mexico", tickets:[{ park:"winterhaven", valid:false, persons:1 }, { park:"baylake", valid:true, persons:3 }] })
db.visitors.save({ _id:"aluska", name:"Alice", country:"Czech Republic", tickets:[{ park:"iskadarputeri", valid:false, persons:2 }, { park:"iskadarputeri", valid:true, persons:5 }] })
db.visitors.save({ _id:"mrbaggins", name:"Bilbo Baggins", country:"Shire", tickets:[] })

db.parks.update({ _id:"billund" }, { name:"Legoland Billund Resort", country:"Denmark", opened:"1968" })
db.visitors.updateOne({ _id:"bobicek", "tickets.park":"baylake" }, {$set:{"tickets.$.valid":false}, $inc:{"tickets.$.persons":1}})

/* find all visitors by country */
db.visitors.aggregate([{ $group:{_id:{country:"$country"},visitors:{$addToSet:"$_id"}} }, { $sort:{_id:1} }]).forEach(printjson)

/* find all amusements in Legoland California */
db.amusements.find({parks:{$in:["carlsbad"]}}, {_id:true,parks:true}).sort({_id:1}).forEach(printjson)

/* find all amusements in more than one park */
db.amusements.find({ "parks.1":{$exists:true} }, {_id:false}).forEach(printjson)
db.amusements.find({ $where:"this.parks.length>1" }, {_id:false}).forEach(printjson)

/* find all visitors being somewhere twice */
db.visitors.aggregate([{ $unwind:"$tickets" }, {$group:{_id:{visitor:"$name", park:"$tickets.park"},count:{$sum:1}}}, {$match:{count:{$gt:1}}}]).forEach(printjson)
db.visitors.mapReduce( function() {var name=this.name; this.tickets.forEach(function(ticket){emit(name+","+ticket.park, 1)})}, function(key, values) {return Array.sum(values)}, {out:{inline:1}})

