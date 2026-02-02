# Polices Montserrat

## Installation des polices

Pour que l'application utilise la police Montserrat, téléchargez les fichiers de police et placez-les dans ce dossier.

### Téléchargement

1. Téléchargez la police Montserrat depuis Google Fonts :
   - https://fonts.google.com/specimen/Montserrat
   - Cliquez sur "Download family"

2. Extrayez l'archive ZIP téléchargée

3. Copiez les fichiers suivants dans ce dossier (`src/fonts/`) :
   - `Montserrat-Regular.ttf`
   - `Montserrat-Bold.ttf`
   - `Montserrat-Medium.ttf`
   - `Montserrat-SemiBold.ttf`
   - `Montserrat-Light.ttf`

### Structure attendue

```
src/
└── fonts/
    ├── README.md (ce fichier)
    ├── Montserrat-Regular.ttf
    ├── Montserrat-Bold.ttf
    ├── Montserrat-Medium.ttf
    ├── Montserrat-SemiBold.ttf
    └── Montserrat-Light.ttf
```

### Alternative via ligne de commande

Vous pouvez également télécharger les polices avec wget :

```bash
cd src/fonts/

# Télécharger depuis un miroir GitHub
wget -O Montserrat.zip "https://github.com/JulietaUla/Montserrat/archive/refs/heads/master.zip"
unzip Montserrat.zip
cp Montserrat-master/fonts/ttf/Montserrat-Regular.ttf .
cp Montserrat-master/fonts/ttf/Montserrat-Bold.ttf .
cp Montserrat-master/fonts/ttf/Montserrat-Medium.ttf .
cp Montserrat-master/fonts/ttf/Montserrat-SemiBold.ttf .
cp Montserrat-master/fonts/ttf/Montserrat-Light.ttf .
rm -rf Montserrat.zip Montserrat-master/
```

### Note

Si les polices ne sont pas trouvées, l'application utilisera automatiquement une police système par défaut (SansSerif).
