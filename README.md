Sobre o Projeto / About the Project
PT:
O HiddenCity é uma aplicação móvel nativa criada para ajudar os utilizadores a descobrir e explorar locais de interesse numa cidade. Desenvolvida como projeto de conclusão da licenciatura em Engenharia Informática, a aplicação tem um forte foco em serviços de geolocalização, mapas interativos e persistência de dados locais.

EN:
HiddenCity is a native mobile application designed to help users discover and explore places of interest within a city. Developed as the final-year project for the Bachelor's Degree in Computer Engineering, the app heavily utilises geolocation services, interactive mapping, and local data persistence.


Imagens / Screenshots

<img width="277" height="496" alt="Captura de ecrã 2024-04-10 192617" src="https://github.com/user-attachments/assets/545a5d02-5ca1-488e-a2b4-0267bb432a8d" />



Funcionalidades principais / Key features

PT:
•	Mapas Interativos - Integração com a API do Google Maps para uma navegação fluida.
•	Exploração de POIs - Descubra locais históricos e monumentos importantes na cidade.
•	Criar Pontos de Interesse - Permite aos utilizadores registar e criar os seus próprios locais de interesse no mapa.
•	Performance Nativa - Construída inteiramente em Kotlin para uma experiência Android otimizada.
•	Armazenamento Offline - Gestão e persistência de dados locais através de SQLite.
•	Geolocalização - Monitorização da posição do utilizador em tempo real para sugestões de proximidade.

EN:
•	Interactive Maps - Integration with Google Maps API for smooth navigation.
•	Explore POIs - Discover historical sites and important landmarks in the city.
•	Create Points of Interest - Allows users to register and create their own points of interest on the map.
•	Native Performance - Built entirely in Kotlin for an optimised Android experience.
•	Offline Storage - Local data management and persistence using SQLite.
•	Geolocation - Real-time user position tracking for proximity suggestions.


O que precisas de ter instalado / Prerequisites
•	Android Studio
•	Java JDK 17+
•	Uma chave de API do Google Maps (Google Maps API Key)
•	SQLite


Como pôr a funcionar / How to get it working

PT:
1.	"git clone https://github.com/RicardoAFM2/HiddenCity.git"
2.	Importa o projeto selecionando a pasta HiddenCity no Android Studio.
3.	Abre o ficheiro local.properties (na raiz do projeto) e adiciona a tua chave: MAPS_API_KEY=A_TUA_CHAVE_AQUI
4.	Clica em "Sync Project with Gradle Files" e aguarda que o Android Studio descarregue as dependências.
5.	Clica no botão Run (ícone do play verde) para instalar e correr a aplicação num emulador ou num dispositivo Android físico.


EN:
1.	"git clone https://github.com/RicardoAFM2/HiddenCity.git"
2.	Import the project by selecting the HiddenCity folder in Android Studio.
3.	Open the local.properties file (in the project root) and add your key: MAPS_API_KEY=YOUR_KEY_HERE
4.	Click ‘Sync Project with Gradle Files’ and wait for Android Studio to download the dependencies.
5.	Click the Run button (green play icon) to install and run the app on an emulator or a physical Android device.


Notas importantes antes de arrancar / Important notes before you start

PT:
•	Google Cloud Console - Certifica-te de que a tua API Key tem a "Maps SDK for Android" ativa na Google Cloud Console. Caso contrário, o mapa aparecerá em branco.
•	Permissões de Localização - Ao abrir a app pela primeira vez, será necessário aceitar as permissões de localização (GPS) para que a geolocalização funcione.
•	Base de Dados Local - O SQLite criará automaticamente o esquema da base de dados no primeiro arranque do dispositivo/emulador.


EN:
•	Google Cloud Console - Ensure your API Key has the "Maps SDK for Android" enabled in the Google Cloud Console. Otherwise, the map will appear blank.
•	Location permissions - When opening the app for the first time, you must accept location (GPS) permissions for geolocation to work.
•	Local Database - SQLite will automatically create the database schema upon the first launch on the device/emulator.

