# MovieReviews - Filmaren Kritiken Sailkapen Sistema

## Proiektuaren Deskribapena

MovieReviews proiektua filmen kritiketako sentimendu-sailkapena egiteko sistema da. Proiektu honek datu-zientzian eta makina-ikaskuntzaren teknikak erabiltzen ditu kritiketatiko iritzi positibo edo negatiboak identifikatzeko.

### Proiektuaren Ezaugarriak

- **Daten Aurreprozesamendua**: Testuaren garbiketa eta normalizazioa
- **Bektorizazioa**: BOW erabiliz testua atributu sorta batean bihurtu
- **Atributuen Hautapena**: InfoGain altuena duten atributuak hautatu
- **Modeloen Entrenamendua**: WEKA liburutegian oinarritutako sailkatzailea erabiliz
- **Parametroen Optimizazioa**: Grid search bidez konfigurazio egokienen bilaketa

---

## Proiektuaren Egiturak

```
MovieReviews/
├── src/                          # Java iturburu-kodea
│   ├── MainDatuZientzia.java      # Zikloa osoa exekutatzen duen programa nagusia
│   ├── MainErabiltzaile.java      # kritikak sailkatzeko programa
│   ├── Aurreprozesamendua.java    # Testuaren garbiketa eta prozesamindu
│   ├── DatuKarga.java             # Datuak ARFF formatutik kargatzea
│   ├── Bektorizazioa.java         # Testuaren bihurtzea bektoreetan
│   ├── DatuAnalisia.java          # Daten estatistika eta analisia
│   ├── Iragarpenak.java           # Sailkapena egitea
│   ├── KalitateEstimatzaile.java  # Joko (precision, recall, F1...)
│   ├── AtributuHautapena.java     # Relevanteak diren atributuak aukeratu
│   ├── ParametroEkorketa.java     # Parametroen saiaketaren kudeaketa
│   ├── OptimalModelCreator.java   # Ondoen sailkatzen duen modeloa sortzea
│   ├── PartiketaSortzailea.java   # Entrenaketarako partiketa sortzea
│   └── Saver.java                 # Datuak ARFF formatuan gorde
│
├── bin/                           # Bilegatutako klaseak (JAR karpeta barnean)
├── doc/                           # Javadoc dokumentazioa
├── MovieReviews.jar              # Exekutagarri Java (JAR)
└── dictionary.txt                # Euskararen hiztegia
```

---

## Exekutagarriak eta Nola Erabili

### 1. **MovieReviews.jar** - Parametroen Bilaketa (Grid Search)

Honek bektorizazioaren parametro desberdinak saiaka ditzake, bakoitzaren emaitzak kalkulatzean.

**Erabilera:**
```bash
java -jar MovieReviews.jar <datu-karpeta> <W> <S> <TF> <IDF> <WC> <Rank>
```

**Parametroak:**
- `<datu-karpeta>`: Datuak dituen karpetaren kokalekua
- `<W>`: Mantendu behar diren hitzak (adib: 10000, 50000, 100000)
- `<S>`: Stemming erabiltzea (0=ez, 1=bai)
- `<TF>`: Term Frequency erabiltzea (0=ez, 1=bai)
- `<IDF>`: Inverse Document Frequency erabiltzea (0=ez, 1=bai)
- `<WC>`: Hitzaren maiztasun absolutua erabiltzea (0=ez, 1=bai)
- `<Rank>`: Kontserbatuko diren atributuak (adib: 800)

**Adibidez:**
```bash
java -jar MovieReviews.jar /home/movies_data 10000 1 1 0 1 800
```

### 2. **script.sh** - Parametroen Grid Search Automatikoa

Script honek, bektorizazio era ezberdinen grid search bat egiten du, log-ak gorde egiten ditu karpeta batean

**Nola exekutatu:**
```bash
bash script.sh
```

**Script-ak egiten duena:**

1. **Konfigurazioa**: Parametroen barrutiak eta balioak ezartzen ditu
2. **Begizta Ugaria (6 maila)**: Parametro kombinazio guztiaren sorketa:
   - WordsToKeep (W): 10000, 50000, 100000
   - Stemmer (S): ez/bai (0/1)
   - TF (TF): erabiltzea (1)
   - IDF (IDF): ez (0)
   - WordCount (WC): erabiltzea (1)
   - Ezaugarri Kopurua (F): 800

3. **Exekuzioetatik gordetzen du**:
   - Bakoitzeko emaitza `log_experimetnalak/` karpetan gordetzen du
   - Fitxeroaren izena parametroen espezifikazioa dute: `res_W{W}_S{S}_TF{TF}_I{IDF}_WC{WC}_F{F}.txt`

4. **Emaitzak**: Bakoitzean, sailkatazailearen jokoa eta emaitzak gordetzen dira

**Adibidez, lortzen diren fitxeroak:**
```
log_experimetnalak/
├── res_W10000_S0_TF1_I0_WC1_F800.txt
├── res_W10000_S1_TF1_I0_WC1_F800.txt
├── res_W50000_S0_TF1_I0_WC1_F800.txt
└── ...
```

---

## Zikloa osoa

Proiektuaren zikloa hurrengoa da:

1. **Aurreprozesamendua** (`Aurreprozesamendua.java`): Testuaren garbiketa
2. **Daten Karga** (`DatuKarga.java`): ARFF_RAW fitxeroak sortu eta datuak testutik irakurri
3. **Analisia** (`DatuAnalisia.java`): Datuen estatistika kalkulatzea
4. **Parametroen Ekorketa** (`ParametroEkorketa.java`): Parametroen grid search
5. **Bektorizazioa** (`Bektorizazioa.java`): Instantziei BOW aplikatu
6. **Atributuen Hautapena** (`AtributuHautapena.java`): InfoGain eta Ranker aplikatu
7. **Sailkapen Modeloa** (`Iragarpenak.java`): Iragarpena egitea
8. **Emaitzak Gordetzea** (`Saver.java`): Artxiboak gorde

---

## Informazio Osagarria

- **WEKA**: Datu-mainerako liburutegi abiertua
- **Java 21+**: Proiektua gauzatzeko beharrezkoa

