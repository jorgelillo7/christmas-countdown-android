package com.jorgelillo.whoslying.domain

/**
 * Built-in packs, written for this app. Each line is "word / similar word": civilians get the
 * first, the impostor (CLASSIC and DRIFTER modes) the second.
 */
object WordPacks {

    fun builtIn(language: String): List<WordPack> = if (language == "es") spanish else english

    private fun pack(id: String, name: String, emoji: String, pairs: String) = WordPack(
        id = id,
        name = name,
        emoji = emoji,
        entries = pairs.lines().map { it.trim() }.filter { it.isNotEmpty() }.map { line ->
            val (word, decoy) = line.split("/").map { it.trim() }
            Entry(word, decoy)
        },
    )

    private val spanish = listOf(
        pack(
            "food", "Comida", "🍕",
            """
            Pizza / Hamburguesa
            Paella / Risotto
            Tortilla de patatas / Revuelto
            Churros / Rosquillas
            Croquetas / Empanadillas
            Gazpacho / Salmorejo
            Sushi / Ramen
            Helado / Granizado
            Chocolate / Caramelo
            Café / Té
            Naranja / Mandarina
            Fresa / Cereza
            Jamón / Chorizo
            Queso / Yogur
            Pan / Galletas
            Patatas fritas / Nachos
            Lentejas / Garbanzos
            Tarta / Flan
            Palomitas / Pipas
            Aceitunas / Pepinillos
            """,
        ),
        pack(
            "places", "Lugares", "🗺️",
            """
            Playa / Piscina
            Cine / Teatro
            Hospital / Farmacia
            Aeropuerto / Estación de tren
            Biblioteca / Librería
            Gimnasio / Polideportivo
            Supermercado / Mercado
            Museo / Galería de arte
            Restaurante / Bar
            Colegio / Universidad
            Parque / Jardín botánico
            Hotel / Camping
            Castillo / Palacio
            Zoo / Acuario
            Discoteca / Concierto
            Montaña / Volcán
            Desierto / Selva
            Comisaría / Juzgado
            Estadio / Circo
            Panadería / Cafetería
            """,
        ),
        pack(
            "animals", "Animales", "🦁",
            """
            Perro / Lobo
            Gato / Tigre
            León / Leopardo
            Delfín / Tiburón
            Águila / Halcón
            Caballo / Burro
            Vaca / Oveja
            Pingüino / Foca
            Mono / Gorila
            Elefante / Rinoceronte
            Serpiente / Lagarto
            Abeja / Avispa
            Mariposa / Libélula
            Rana / Sapo
            Ratón / Hámster
            Pulpo / Calamar
            Jirafa / Cebra
            Tortuga / Caracol
            Búho / Murciélago
            Gallina / Pato
            """,
        ),
        pack(
            "home", "En casa", "🛋️",
            """
            Sofá / Sillón
            Cama / Hamaca
            Nevera / Congelador
            Microondas / Horno
            Lavadora / Lavavajillas
            Televisión / Ordenador
            Cuchara / Tenedor
            Toalla / Albornoz
            Almohada / Cojín
            Lámpara / Vela
            Espejo / Ventana
            Escoba / Fregona
            Cepillo de dientes / Peine
            Reloj / Despertador
            Llave / Candado
            Paraguas / Chubasquero
            Mochila / Maleta
            Tijeras / Cuchillo
            Bañera / Ducha
            Alfombra / Felpudo
            """,
        ),
        pack(
            "jobs", "Profesiones", "👩‍🚒",
            """
            Médico / Enfermero
            Bombero / Policía
            Profesor / Entrenador
            Cocinero / Camarero
            Piloto / Auxiliar de vuelo
            Fontanero / Electricista
            Abogado / Juez
            Dentista / Veterinario
            Peluquero / Maquillador
            Carpintero / Albañil
            Periodista / Fotógrafo
            Actor / Cantante
            Astronauta / Científico
            Panadero / Pastelero
            Mecánico / Taxista
            Granjero / Jardinero
            Programador / Diseñador
            Futbolista / Árbitro
            Cartero / Repartidor
            Pintor / Escultor
            """,
        ),
        pack(
            "fun", "Ocio y deporte", "⚽",
            """
            Fútbol / Baloncesto
            Tenis / Pádel
            Natación / Surf
            Ajedrez / Damas
            Esquí / Snowboard
            Ciclismo / Running
            Boxeo / Kárate
            Golf / Minigolf
            Bolos / Billar
            Yoga / Pilates
            Videojuegos / Juegos de mesa
            Karaoke / Concierto
            Pesca / Buceo
            Acampada / Pícnic
            Cartas / Parchís
            Bailar / Cantar
            Leer / Escribir
            Puzle / Lego
            Patinaje / Skate
            Escalada / Senderismo
            """,
        ),
    )

    private val english = listOf(
        pack(
            "food", "Food", "🍕",
            """
            Pizza / Burger
            Pancakes / Waffles
            Sushi / Ramen
            Ice cream / Frozen yogurt
            Chocolate / Caramel
            Coffee / Tea
            Orange / Lemon
            Strawberry / Cherry
            Bacon / Sausage
            Cheese / Butter
            Bread / Bagel
            Fries / Onion rings
            Hot dog / Sandwich
            Popcorn / Pretzels
            Cake / Pie
            Taco / Burrito
            Spaghetti / Lasagna
            Donut / Muffin
            Salad / Soup
            Apple / Pear
            """,
        ),
        pack(
            "places", "Places", "🗺️",
            """
            Beach / Pool
            Cinema / Theatre
            Hospital / Pharmacy
            Airport / Train station
            Library / Bookshop
            Gym / Sports centre
            Supermarket / Market
            Museum / Art gallery
            Restaurant / Pub
            School / University
            Park / Garden
            Hotel / Campsite
            Castle / Palace
            Zoo / Aquarium
            Nightclub / Concert
            Mountain / Volcano
            Desert / Jungle
            Police station / Courthouse
            Stadium / Circus
            Bakery / Café
            """,
        ),
        pack(
            "animals", "Animals", "🦁",
            """
            Dog / Wolf
            Cat / Tiger
            Lion / Leopard
            Dolphin / Shark
            Eagle / Hawk
            Horse / Donkey
            Cow / Sheep
            Penguin / Seal
            Monkey / Gorilla
            Elephant / Rhino
            Snake / Lizard
            Bee / Wasp
            Butterfly / Dragonfly
            Frog / Toad
            Mouse / Hamster
            Octopus / Squid
            Giraffe / Zebra
            Turtle / Snail
            Owl / Bat
            Chicken / Duck
            """,
        ),
        pack(
            "home", "At home", "🛋️",
            """
            Sofa / Armchair
            Bed / Hammock
            Fridge / Freezer
            Microwave / Oven
            Washing machine / Dishwasher
            TV / Computer
            Spoon / Fork
            Towel / Bathrobe
            Pillow / Cushion
            Lamp / Candle
            Mirror / Window
            Broom / Mop
            Toothbrush / Comb
            Clock / Alarm clock
            Key / Padlock
            Umbrella / Raincoat
            Backpack / Suitcase
            Scissors / Knife
            Bathtub / Shower
            Rug / Doormat
            """,
        ),
        pack(
            "jobs", "Jobs", "👩‍🚒",
            """
            Doctor / Nurse
            Firefighter / Police officer
            Teacher / Coach
            Chef / Waiter
            Pilot / Flight attendant
            Plumber / Electrician
            Lawyer / Judge
            Dentist / Vet
            Hairdresser / Make-up artist
            Carpenter / Builder
            Journalist / Photographer
            Actor / Singer
            Astronaut / Scientist
            Baker / Pastry chef
            Mechanic / Taxi driver
            Farmer / Gardener
            Programmer / Designer
            Footballer / Referee
            Postal worker / Delivery driver
            Painter / Sculptor
            """,
        ),
        pack(
            "fun", "Sports & fun", "⚽",
            """
            Football / Basketball
            Tennis / Badminton
            Swimming / Surfing
            Chess / Checkers
            Skiing / Snowboarding
            Cycling / Running
            Boxing / Karate
            Golf / Mini golf
            Bowling / Pool
            Yoga / Pilates
            Video games / Board games
            Karaoke / Concert
            Fishing / Diving
            Camping / Picnic
            Card games / Dominoes
            Dancing / Singing
            Reading / Writing
            Jigsaw puzzle / Lego
            Ice skating / Skateboarding
            Climbing / Hiking
            """,
        ),
    )
}
