# Microservices Systeemarchitectuur JAVA

![Screenshot](https://raw.githubusercontent.com/pxlit-projects/project-WesleyKissenPXL/main/architecture/Screenshot%202024-11-18%20171227.png)

Deze repository bevat een gedistribueerde microservices-architectuur met de volgende componenten:

## 1. Frontend: Angular Frontend
De clienttoepassing gebouwd met Angular. Gebruikers communiceren met het systeem via deze frontend. 
Alle gebruikersverzoeken worden doorgegeven aan de **API Gateway** voor verdere verwerking.

## 2. API Gateway
De **API Gateway** fungeert als de centrale toegangspoort voor alle inkomende verzoeken van de frontend. 
Het is verantwoordelijk voor het routeren van verzoeken naar de juiste microservice, zoals:

- **PostService**
- **CommentService**
- **ReviewService**

## 3. Microservices
Het systeem bestaat uit drie afzonderlijke microservices, elk met hun eigen verantwoordelijkheid en database:

### a. PostService
- **Post API**: verabtwoordelijk voor op het beheren van de posts.
- **MySQL Database**: Slaat postgegevens op.
- **Open Feign Communicatie**: Gebruikt Open Feign om te communiceren met andere services (bijv. **CommentService** en **ReviewService**) wanneer dat nodig is.

### b. CommentService
- **Comment API**: Verantwoordelijk voor het beheren van acties op posts.
- **MySQL Database**: Slaat commentaargegevens op.
- **Open Feign Communicatie**: Gebruikt Open Feign om gegevens op te halen van **PostService** of **ReviewService**.

### c. ReviewService
- **Review API**: Beheert de reviewprocessen van posts.
- **MySQL Database**: Slaat beoordelingsgegevens op.
- **Open Feign Communicatie**: Kan andere services raadplegen via Open Feign.

## 4. Open Feign Communicatie (synchroon)
**Open Feign** is een declaratieve HTTP-client die het eenvoudig maakt om HTTP-verzoeken naar andere microservices te sturen.

- Elke microservice kan direct met andere microservices communiceren (synchroon) via Open Feign, zonder dat de **API Gateway** nodig is.
- Bijvoorbeeld, wanneer **CommentService** een opmerking opslaat, kan het een Open Feign-verzoek sturen naar **PostService** om informatie over de bijbehorende post op te halen.

## 5. Asynchrone Communicatie: RabbitMQ Messaging Service
Het systeem maakt gebruik van **RabbitMQ** voor asynchrone communicatie tussen microservices:

- Wanneer er gebeurtenissen plaatsvinden (zoals het creëren van een nieuwe post, opmerking of beoordeling), stuurt de relevante microservice een bericht naar **RabbitMQ**.
- De **Notification Service** luistert naar deze berichten en stuurt meldingen naar gebruikers, zoals e-mails of pushnotificaties.
- Dit decoupleert de services, zodat microservices niet hoeven te wachten op elkaars reacties, wat de prestaties en schaalbaarheid van het systeem verbetert.

## 6. Discovery Service (Eureka)
**Eureka** fungeert als een service registry:

- Alle microservices registreren zich bij Eureka, zodat de **API Gateway** en andere services de locaties van de services dynamisch kunnen ontdekken.
- Dit zorgt ervoor dat het systeem kan schalen en dat services opnieuw kunnen worden opgestart zonder dat hun locaties handmatig moeten worden bijgewerkt.

## 7. Config Service
De **Config Service** beheert de configuraties van alle microservices (**PostService**, **ReviewService**, **CommentService**):

- Microservices halen hun configuratie centraal op bij deze service, wat zorgt voor consistentie en eenvoudig beheer.

## 8. Notification Service
De **Notification Service** luistert naar RabbitMQ-berichten en stuurt meldingen naar gebruikers. Bijvoorbeeld, wanneer een nieuwe opmerking wordt geplaatst op een post die door de gebruiker wordt gevolgd, kan de service een e-mail of pushnotificatie sturen naar de gebruiker.

---

## Hoe het werkt
1. Gebruikers communiceren met de **Angular Frontend**.
2. Verzoeken worden via de **API Gateway** gerouteerd.
3. De API Gateway stuurt verzoeken door naar de juiste microservice (**PostService**, **CommentService** of **ReviewService**).
4. Indien nodig, maakt **Open Feign** inter-microservice communicatie mogelijk.
5. Asynchrone gebeurtenissen (zoals nieuwe posts, opmerkingen of beoordelingen) worden gecommuniceerd via **RabbitMQ**.
6. De **Notification Service** stuurt meldingen naar gebruikers over gebeurtenissen.
