curl localhost:9000/add -H "Content-Type: application/json" -X POST -d '{"title":"MaetKsime","country":"FRA","year" : 1201, "original_title":"MelanieToutletemps", "french_release": "1587/02/02", "synopsis" : "bonchour", "genre":["BienGore"], "ranking" : 5}'

curl localhost:9000/add -H "Content-Type: application/json" -X POST -d '{"title":"MonFilm","country":"FRA","year" : 1201, "original_title":"MelanieToutletemps", "french_release": "1587/03/03", "synopsis" : "bonchour", "genre":["BienGore"]}'

curl localhost:9000/add -H "Content-Type: application/json" -X POST -d '{"title":"Tabmo","country":"FRA","year" : 1300, "original_title":"slt", "french_release": "1587/04/04", "synopsis" : "bonchour", "genre":["test","BienGore"], "ranking" : 5}'

curl localhost:9000/add -H "Content-Type: application/json" -X POST -d '{"title":"radisLand","country":"KAM","year" : 1300, "original_title":"Net", "french_release": "1587", "synopsis" : "bonchour", "genre":["dadada"], "ranking" : 5}'

curl localhost:9000/add -H "Content-Type: application/json" -X POST -d '{"title":"NOP","country":"FRA","year" : 1300, "french_release": "1587", "synopsis" : "bonchour", "genre":["dadada"], "ranking" : 5}'



curl localhost:9000/look

curl localhost:9000/sameThan/BienGore


curl localhost:9000/sameYear/1300
