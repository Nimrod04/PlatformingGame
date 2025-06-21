# PlatformingGame

Ez egy egyetemi Java platformjáték projekt, amely NetBeans környezetben készült.

## Főbb jellemzők

- Platformer játék Java nyelven
- Highscore adatbázis MySQL-lel
- Egyszerű grafika és játékmechanika
- 10 előre elkészített pálya
- Ranglista mentés és megjelenítés
- Menü: Játék indítása, ranglista megtekintése, kilépés

## Játékmenet röviden

Egy kalózt irányítasz, célod az összes ellenség (Crabby) elpusztítása.  
- **Mozgás:** WASD  
- **Ugrás:** SPACE  
- **Támadás:** Jobb egérgomb  
- **Veszítesz:** ha az életerőd 0-ra csökken vagy csapdába esel  
- **Halál után:** nevet adhatsz meg, pontszámod mentésre kerül a ranglistán  
- **Menü:** Játék indítása, ranglista, kilépés

## Főbb osztályok

- **Leaderboard:** Játékosnév és pontszám mentése adatbázisba
- **Entity:** Absztrakt ősosztály minden entitásnak (játékos, ellenség)
- **Player:** Játékos logika, pontszám, időzítő
- **Enemy / Crabby:** Ellenség logika, támadás, mozgás
- **Gamestate:** Játékállapotok (menü, játék, ranglista, kilépés)
- **Keyboard- és MouseInputs:** Bemenetkezelés
- **Level:** Pálya reprezentáció, ellenségek, csapdák
- **Game:** Játék főciklusa, FPS/UPS kezelés
- **GameObject / Spike:** Játékobjektumok, csapdák
- **GameOverOverlay / LevelCompletedOverlay:** Halál vagy pályateljesítés utáni képernyő
- **Constants, HelpMethods, LoadSave:** Konstansok, segédmetódusok, pályabetöltés

## Futtatás

### 1. Követelmények

- Java 8 vagy újabb
- [MySQL szerver](https://www.mysql.com/)
- (Opcionális) NetBeans IDE

### 2. Adatbázis beállítása

1. Hozd létre az adatbázist a `highscores_db.sql` fájl futtatásával MySQL-ben:
   ```sql
   source highscores_db.sql
   ```
2. Ellenőrizd, hogy a forráskódban a MySQL elérési adatok megfelelnek a saját beállításaidnak (felhasználónév, jelszó).

### 3. Fordítás és futtatás parancssorból

1. Fordítsd le a forráskódot:
   ```sh
   javac -cp "lib/mysql-connector-java-8.0.19.jar" -d build/classes src/main/MainClass.java
   ```
   *(A `MainClass.java` helyére írd be a tényleges főosztályt!)*

2. Futtasd a programot:
   ```sh
   java -cp "build/classes;lib/mysql-connector-java-8.0.19.jar" main.MainClass
   ```

### 4. Fordítás és futtatás NetBeans-ből

- Nyisd meg a projektet NetBeans-ben, majd használd a "Futtatás" gombot.

### 5. JAR készítése

```sh
ant jar
java -jar dist/PlatformingGame.jar
```

## Tesztelési példák

| Bemenet                        | Leírás                        | Elvárt kimenet                                      |
|---------------------------------|-------------------------------|-----------------------------------------------------|
| Játékos megüt egy ellenfelet    | Támadás                       | Ellenfél sebződik, ha meghal, eltűnik, pont jár     |
| Játékos platformon áll          | Mozgás                        | Nem esik át, tud ugrani                             |
| Játékos csapdába esik           | Csapda                        | Meghal, újraindul a pálya, pontszám nullázódik      |
| A billentyűk (A/D/SPACE)        | Mozgás/ugrás                  | Karakter mozog/ugrik                                |

## Szerző

- Név: Szekeres Nimród
- ELTE, Programozási technológiák beadandó
- Dátum: 2024.12.15.

---

*Ez a projekt tanulmányi célokat szolgál.*
